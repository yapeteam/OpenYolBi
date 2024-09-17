package cn.yapeteam.yolbi.module.impl.visual.nametags;

import cn.yapeteam.yolbi.module.impl.visual.NameTags;
import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.Map;

public class TextureTag implements IMinecraft {
    public static void renderNameTag(EntityLivingBase entity, boolean invis, Map<EntityLivingBase, double[]> entityPositions, int alpha, int size, ScaledResolution sr) {
        if (entity != mc.player && (invis || !entity.isInvisible())) {
            GlStateManager.pushMatrix();
            if (entity instanceof EntityPlayer) {
                double[] array = entityPositions.get(entity);
                if (array[3] < 0.0D || array[3] >= 1.0D) {
                    GlStateManager.popMatrix();
                    return;
                }
                GlStateManager.translate(array[0] / sr.getScaleFactor(), array[1] / sr.getScaleFactor(), 0.0D);
                scale();
                GlStateManager.translate(0.0D, -2.5D, 0.0D);
                RenderUtil.drawImage(NameTags.texture, -size / 2f, 0, size, size, new Color(255, 255, 255, alpha).getRGB());
            }
            GlStateManager.popMatrix();
        }
    }

    private static void scale() {
        final float n = 1.0f;
        GlStateManager.scale(n, n, n);
    }
}
