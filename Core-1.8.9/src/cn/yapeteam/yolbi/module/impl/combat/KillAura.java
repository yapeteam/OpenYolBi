package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoadWorld;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.movement.StrafeFix;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Animation;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT);
        minRotationSpeed.setCallback((oldV, newV) -> newV > maxRotationSpeed.getValue() ? oldV : newV);
        maxRotationSpeed.setCallback((oldV, newV) -> newV < minRotationSpeed.getValue() ? oldV : newV);
        addValues(cps, cpsRange, searchRange, autoBlock, blockDelay, maxRotationSpeed, minRotationSpeed, autoRod, invisibility, death, captureMark);
    }

    private final NumberValue<Double> searchRange = new NumberValue<>("Range", 3.0, 0.0, 8.0, 0.1);
    private final NumberValue<Double> cps = new NumberValue<>("CPS", 8.0, 1.0, 100.0, 1.0);
    private final NumberValue<Double> cpsRange = new NumberValue<>("Random Tick", 1.5, 0.1, 5.0, 0.1);
    private final NumberValue<Double> maxRotationSpeed = new NumberValue<>("MaxRotationSpeed", 60.0, 1.0, 180.0, 5.0);
    private final NumberValue<Double> minRotationSpeed = new NumberValue<>("MinRotationSpeed", 40.0, 1.0, 180.0, 5.0);
    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", false);
    private final NumberValue<Double> blockDelay = new NumberValue<>("BlockDelay", autoBlock::getValue, 2.0, 1.0, 10.0, 1.0);
    private final BooleanValue autoRod = new BooleanValue("AutoRod", false);
    private final BooleanValue invisibility = new BooleanValue("Invisibility", false);
    private final BooleanValue death = new BooleanValue("Death", false);
    private final BooleanValue captureMark = new BooleanValue("NurikZapen Capture Mark", true);
    private final TimerUtil timer = new TimerUtil();
    @Getter
    private EntityLivingBase target = null;
    private boolean blocking = false;
    private boolean fishingRodThrow = false;
    private int fishingRodSwitchOld = 0;
    private static final Random random = new Random();

    @Listener
    private void onUpdate(EventRender2D event) {
        try {
            if (mc.theWorld == null || mc.thePlayer == null) return;
            if (mc.theWorld.loadedEntityList.isEmpty()) return;
            if (mc.currentScreen != null) return;
            target = null;
            if (!autoBlock.getValue())
                blocking = false;

            val targetList = TargetManager.getTargets(searchRange.getValue());
            targetList.removeIf(entity -> !invisibility.getValue() && entity.isInvisible() || !death.getValue() && entity.isDead);
            if (!targetList.isEmpty()) target = (EntityLivingBase) targetList.get(0);

            double rotationSpeed = MathUtils.getRandom(maxRotationSpeed.getValue(), minRotationSpeed.getValue());

            // Rotations
            if (target != null) {
                float[] rotation = RotationsUtil.getRotationsToEntity(target, true);
                Vector2f rotationVec = new Vector2f(rotation[0], rotation[1]);

                rotationManager.setRotations(rotationVec, rotationSpeed);
                rotationManager.smooth();
            }

            // Attack & AutoRod
            if (target != null) {
                int cps = (int) generate(this.cps.getValue(), cpsRange.getValue());

                if (mc.thePlayer.ticksExisted % blockDelay.getValue().intValue() == 0) {
                    startBlock();
                } else {
                    stopBlock();
                }

                if (shouldAttack(cps)) {
                    stopBlock();
                    Natives.SendLeft(true);
                    Natives.SendLeft(false);
                    reset();
                }

                if (autoRod.getValue()) {
                    for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
                        if (i > 9) break;

                        ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];

                        if (itemStack != null && itemStack.getItem() instanceof ItemFishingRod) {
                            if (fishingRodThrow) {
                                Natives.SendRight(true);
                                Natives.SendRight(false);
                                mc.thePlayer.inventory.currentItem = fishingRodSwitchOld;
                                fishingRodThrow = false;
                            } else {
                                fishingRodSwitchOld = mc.thePlayer.inventory.currentItem;
                                mc.thePlayer.inventory.currentItem = i;
                                Natives.SendRight(true);
                                Natives.SendRight(false);
                                fishingRodThrow = true;
                            }
                            break;
                        }
                    }
                }
            } else {
                if (rotationManager.active)
                    rotationManager.stop();
                stopBlock();
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static double generateNoise(double min, double max) {
        double u1, u2, v1, v2, s;
        do {
            u1 = random.nextDouble() * 2 - 1;
            u2 = random.nextDouble() * 2 - 1;
            s = u1 * u1 + u2 * u2;
        } while (s >= 1 || s == 0);

        double multiplier = Math.sqrt(-2 * Math.log(s) / s);
        v1 = u1 * multiplier;
        v2 = u2 * multiplier;
        // 将生成的噪声值缩放到指定范围内
        return (v1 + v2) / 2 * (max - min) / 4 + (max + min) / 2;
    }

    public static double generate(double cps, double range) {
        double noise = cps;
        for (int j = 0; j < 10; j++) {
            double newNoise = generateNoise(0, cps * 2);
            if (Math.abs(noise - newNoise) < range)
                noise = (noise + newNoise) / 2;
            else j--;
        }
        return noise;
    }

    private void startBlock() {
        if (autoBlock.getValue() && !blocking) {
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                Natives.SendRight(true);
                blocking = true;
            }
        }
    }

    private void stopBlock() {
        if (autoBlock.getValue() && blocking) {
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                Natives.SendRight(false);
                blocking = false;
            }
        }
    }

    @Override
    protected void onEnable() {
        if (mc.theWorld == null || mc.thePlayer == null) {
            setEnabled(false);
            return;
        }
        if (!YolBi.instance.getModuleManager().getModule(StrafeFix.class).isEnabled())
            YolBi.instance.getNotificationManager().post(new Notification("StrafeFix is Disabled", 5000, NotificationType.WARNING));
    }

    @Override
    protected void onDisable() {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        stopBlock();
        if (rotationManager.active)
            rotationManager.stop();
        target = null;
    }

    @Listener
    private void onWorldLoad(EventLoadWorld e) {
        setEnabled(false);
    }

    private boolean shouldAttack(int cps) {
        return timer.hasTimePassed(1000 / cps);
    }

    private void reset() {
        timer.reset();
    }

    private final Animation auraESPAnim = new Animation(Easing.EASE_OUT_EXPO, 1000);

    private static int texture = -1;

    private static void updateTexture() {
        texture = GL11.glGenTextures();
        try {
            TextureUtil.uploadTextureImageAllocate(texture, ImageIO.read(Objects.requireNonNull(ResourceManager.resources.getStream("imgs/capture.png"))), false, false);
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    private float espValue = 1f, prevEspValue;
    private float espSpeed = 1f;
    private boolean flipSpeed;

    private EntityLivingBase lastTarget = null;

    @Listener
    public void onRender3D(EventRender3D event) {
        if (texture == -1) updateTexture();
        if (!captureMark.getValue()) return;
        if (target != null)
            lastTarget = target;
        if (lastTarget == null) return;
        float animate = (float) auraESPAnim.animate(target != null ? 1 : 0);
        float renderPartialTicks = Objects.requireNonNull(ReflectionManager.Minecraft$getTimer(mc)).renderPartialTicks;
        RenderUtil.renderESPImage(texture, lastTarget, 2 - animate, interpolate(prevEspValue, espValue, renderPartialTicks), Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, animate, renderPartialTicks);
    }

    public static float interpolate(float oldValue, float newValue, float interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    @Listener
    public void tick(EventTick e) {
        prevEspValue = espValue;
        espValue += espSpeed;
        if (espSpeed > 25) flipSpeed = true;
        if (espSpeed < -25) flipSpeed = false;
        espSpeed = flipSpeed ? espSpeed - 0.5f : espSpeed + 0.5f;
    }

    @Override
    public String getSuffix() {
        return searchRange.getValue() + " | " + (cps.getValue() - cpsRange.getValue()) + " ~ " + (cps.getValue() + cpsRange.getValue());
    }
}
