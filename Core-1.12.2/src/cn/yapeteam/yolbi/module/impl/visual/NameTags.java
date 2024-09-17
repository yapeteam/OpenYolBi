package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.font.FontUtil;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.visual.nametags.DistanceNameTag;
import cn.yapeteam.yolbi.module.impl.visual.nametags.DistanceShortNameTag;
import cn.yapeteam.yolbi.module.impl.visual.nametags.TextureTag;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sigma
 */
public class NameTags extends Module {
    public ModeValue<Modes> mode = new ModeValue<>("Mode", Modes.Distance, Modes.values());
    public NumberValue<Integer> headSize = new NumberValue<>("Texture size", () -> mode.is(Modes.Texture), 60, 0, 200, 1);
    public static Map<EntityLivingBase, double[]> entityPositions = new HashMap<>();
    public BooleanValue invis = new BooleanValue("ShowInvisible", true);
    public static NumberValue<Double> alpha = new NumberValue<>("Alpha", 80d, 10d, 255d, 1d);
    public static int texture = -1;

    private static void updateTexture() {
        texture = GL11.glGenTextures();
        try {
            TextureUtil.uploadTextureImageAllocate(texture, ImageIO.read(Objects.requireNonNull(ResourceManager.resources.getStream("imgs/bighead.png"))), false, false);
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    public NameTags() {
        super("NameTags", ModuleCategory.VISUAL);
        this.addValues(mode, headSize, invis, alpha);
    }

    @Listener
    public void update(EventRender3D e) {
        try {
            this.updatePositions();
        } catch (Exception ignored) {
        }
    }

    @Listener(Priority.HIGH)
    public void onRender2D(EventRender2D e) {
        if (texture == -1) updateTexture();
        GlStateManager.pushMatrix();
        for (EntityLivingBase entity : entityPositions.keySet()) {
            switch (mode.getValue()) {
                case Distance:
                    DistanceNameTag.renderNameTag(entity, invis.getValue(), entityPositions, alpha.getValue().intValue(), e.getScaledresolution());
                    break;
                case Short:
                    DistanceShortNameTag.renderNameTag(entity, invis.getValue(), entityPositions, alpha.getValue().intValue(), e.getScaledresolution());
                    break;
                case Texture:
                    TextureTag.renderNameTag(entity, invis.getValue(), entityPositions, alpha.getValue().intValue(), headSize.getValue(), e.getScaledresolution());
            }
        }
        GlStateManager.popMatrix();
    }

    private void updatePositions() {
        entityPositions.clear();
        float pTicks = Objects.requireNonNull(ReflectUtil.Minecraft$getTimer(mc)).field_194147_b;
        for (Entity o : mc.world.loadedEntityList) {
            if ((o != mc.player) && ((o instanceof EntityPlayer))
                    && (!o.isInvisible() || !this.invis.getValue())) {
                double x = o.lastTickPosX + (o.posX - o.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX;
                double y = o.lastTickPosY + (o.posY - o.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
                double z = o.lastTickPosZ + (o.posZ - o.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ;
                y += o.height + 0.2D;
                double[] res = convertTo2D(x, y, z);
                if (res == null) continue;
                if ((res[2] >= 0.0D) && (res[2] < 1.0D)) {
                    entityPositions.put((EntityLivingBase) o,
                            new double[]{res[0], res[1], Math.abs(convertTo2D(x, y + 1.0D, z, o)[1] - convertTo2D(x, y, z, o)[1]), res[2]});
                }
            }
        }
    }

    public static String getTag(EntityLivingBase entity) {
        StringBuilder sb = new StringBuilder();
        if (PlayerUtil.sameTeam(entity))
            sb.append("§a[Team]");
        else sb.append("§f");
        sb.append(entity.getDisplayName().getUnformattedText());
        return sb.toString();
    }

    private double[] convertTo2D(double x, double y, double z, Entity ent) {
        float pTicks = Objects.requireNonNull(ReflectUtil.Minecraft$getTimer(mc)).field_194147_b;
        float prevYaw = mc.player.rotationYaw;
        float prevPrevYaw = mc.player.prevRotationYaw;
        float[] rotations = RotationsUtil.getRotationsToPosition(
                ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks,
                ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks,
                ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - 1.6D
        );
        mc.getRenderViewEntity().rotationYaw = (mc.getRenderViewEntity().prevRotationYaw = rotations[0]);
        ReflectUtil.EntityRenderer$setupCameraTransform(mc.entityRenderer, pTicks, 0);
        double[] convertedPoints = convertTo2D(x, y, z);
        mc.getRenderViewEntity().rotationYaw = prevYaw;
        mc.getRenderViewEntity().prevRotationYaw = prevPrevYaw;
        ReflectUtil.EntityRenderer$setupCameraTransform(mc.entityRenderer, pTicks, 0);
        return convertedPoints;
    }

    private double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
        }
        return null;
    }

    public static int clampColor(int p_clamp_int_0_, int p_clamp_int_0_2, int p_clamp_int_0_3, int p_clamp_int_0_4) {
        return MathHelper.clamp(p_clamp_int_0_4, 0, 255) << 24 | MathHelper.clamp(p_clamp_int_0_, 0, 255) << 16 | MathHelper.clamp(p_clamp_int_0_2, 0, 255) << 8 | MathHelper.clamp(p_clamp_int_0_3, 0, 255);
    }

    public static int getColor(String s) {
        int color = Color.RED.getRGB();
        int i = 0;
        while (i < s.length()) {
            if (s.charAt(i) == '§' && i + 1 < s.length()) {
                int index = "0123456789abcdefklmnorg".indexOf(Character.toLowerCase(s.charAt(i + 1)));
                if (index < 16) {
                    try {
                        Color c = new Color(FontUtil.colorCode[index]);
                        color = clampColor(c.getRed(), c.getGreen(), c.getBlue(), 255);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
            }
            ++i;
        }
        return color;
    }

    private static void drawEnchantTag(String s, int n, int n2) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        n2 -= 6;
        YolBi.instance.getFontManager().getPingFang10().drawStringWithShadow(s, (float) (n + 9), (float) (-30 - n2), new Color(255));
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static void fixGlintShit() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private static String getColor(int n) {
        if (n != 1) {
            if (n == 2) {
                return "§a";
            }
            if (n == 3) {
                return "§3";
            }
            if (n == 4) {
                return "§4";
            }
            if (n >= 5) {
                return "§6";
            }
        }
        return "§f";
    }

    public enum Modes {
        Distance,
        Short,
        Texture
    }
}
