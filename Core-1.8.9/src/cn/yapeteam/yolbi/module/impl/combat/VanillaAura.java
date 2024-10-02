package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.managers.PacketManager;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.animation.Animation;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VanillaAura extends Module {
    private final NumberValue<Integer>
            maxCps = new NumberValue<>("Max CPS", 12, 1, 20, 1),
            minCPS = new NumberValue<>("Min CPS", 9, 1, 20, 1);
    private final NumberValue<Double> range = new NumberValue<>("Range", 8d, 1d, 10d, 1d);
    private final BooleanValue rotation = new BooleanValue("Rotation", false);
    private final BooleanValue smooth = new BooleanValue("Smooth Rotation", rotation::getValue, false);
    private final NumberValue<Integer> rotationSpeed = new NumberValue<>("Rotation Speed", rotation::getValue, 10, 1, 10, 1);
    private final BooleanValue autoblock = new BooleanValue("AutoBlock", true);
    private final ModeValue<String> autoblockMode = new ModeValue<>("AutoBlock Mode", autoblock::getValue, "Always", "Always", "Packet");
    private final ModeValue<BlockTiming> blockTiming = new ModeValue<>("AutoBlock Timing", autoblock::getValue, BlockTiming.Pre, BlockTiming.values());

    private enum BlockTiming {
        Pre, Post
    }

    private final BooleanValue swing = new BooleanValue("Swing", true);
    private final BooleanValue rayCast = new BooleanValue("RayCast", false);
    private final BooleanValue esp = new BooleanValue("ESP", true);

    private EntityLivingBase target;
    private List<Entity> targets = new ArrayList<>();
    private boolean canAttack, canBlock;
    public boolean isBlock;
    private int cps;
    private static int texture = -1;
    private float espValue = 1f, prevEspValue;
    private float espSpeed = 1f;
    private boolean flipSpeed;
    private EntityLivingBase lastTarget = null;

    private final TimerUtil timer = new TimerUtil();
    private final Animation auraESPAnim = new Animation(Easing.EASE_OUT_EXPO, 1000);
    public static VanillaAura instance;

    public VanillaAura() {
        super("VanillaAura", ModuleCategory.COMBAT);
        minCPS.setCallback((oldV, newV) -> newV > maxCps.getValue() ? oldV : newV);
        maxCps.setCallback((oldV, newV) -> newV < minCPS.getValue() ? oldV : newV);
        addValues(maxCps, minCPS, range, rotation, smooth, rotationSpeed, autoblock, autoblockMode, blockTiming, swing, rayCast, esp);
        instance = this;
    }

    @Override
    protected void onDisable() {
        target = null;
        targets.clear();
        canAttack = canBlock = false;
        if (isBlock) {
            PacketManager.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
        isBlock = false;
        cps = 0;
        super.onDisable();
    }

    @Listener
    private void onMotionPreUpdate(EventMotion event) {
        getTargets();

        canAttack = !targets.isEmpty();
        canBlock = autoblock.getValue() && canAttack && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;

        if (canAttack) {
            // 因为狗屎TargetManager所以只能强制转换
            target = (EntityLivingBase) targets.get(0);

            // 转头
            if (rotation.getValue()) {
                float[] rotations;
                Vector2f vecRotations = new Vector2f(0, 0);

                // TODO More Rotation
                rotations = RotationsUtil.getRotationsToEntity(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, target, false);

                // 将旋转角度赋值给vecRotations
                vecRotations.x = rotations[0];
                vecRotations.y = rotations[1];

                // 设置旋转角度
                YolBi.instance.getRotationManager().setRotations(vecRotations, rotationSpeed.getValue());
                // 平滑转头
                if (smooth.getValue()) YolBi.instance.getRotationManager().smooth();
            }

            if (rayCast.getValue() && !RotationsUtil.isMouseOver(event.getYaw(), event.getPitch(), target, range.getValue().floatValue()))
                return;

            // 如果满足条件，则进行攻击
            if (Math.sin(cps) + 1 > Math.random() || timer.hasTimePassed(cps) || Math.random() > 0.5) {
                timer.reset();
                cps = getCPS();

                if (swing.getValue()) mc.thePlayer.swingItem();
                // 攻击目标
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
        } else {
            // 如果没有目标，则将目标置为null
            target = null;
        }

        // 格挡
        if (blockTiming.is(BlockTiming.Pre)) runBlock();
    }

    @Listener
    private void onMotionPostUpdate(EventPostMotion event) {
        if (rayCast.getValue() && !RotationsUtil.isMouseOver(event.getYaw(), event.getPitch(), target, range.getValue().floatValue()))
            return;

        if (blockTiming.is(BlockTiming.Post)) runBlock();
    }

    @Listener
    public void tick(EventTick e) {
        // 保存当前速度
        prevEspValue = espValue;
        // 更新速度
        espValue += espSpeed;

        // 如果速度大于25，则翻转速度
        if (espSpeed > 25) flipSpeed = true;

        // 如果速度小于-25，则翻转速度
        if (espSpeed < -25) flipSpeed = false;

        // 根据翻转速度更新速度
        espSpeed = flipSpeed ? espSpeed - 0.5f : espSpeed + 0.5f;
    }

    @Listener
    public void onRender3D(EventRender3D event) {
        // 如果ESP未开启，则返回
        if (!esp.getValue()) return;

        // 如果纹理为-1，则更新纹理
        if (texture == -1) updateTexture();

        // 如果目标不为空，则将目标赋值给lastTarget
        if (target != null) lastTarget = target;

        // 如果lastTarget为空，则返回
        if (lastTarget == null) return;

        // 计算动画值
        float animate = (float) auraESPAnim.animate(target != null ? 1 : 0);
        // 获取渲染部分时间
        float renderPartialTicks = Objects.requireNonNull(ReflectionManager.Minecraft$getTimer(mc)).renderPartialTicks;
        // 渲染ESP图像
        RenderUtil.renderESPImage(texture, lastTarget, 2 - animate, interpolate(prevEspValue, espValue, renderPartialTicks), Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, animate, renderPartialTicks);
    }

    private void runBlock() {
        if (canBlock) {
            if (autoblockMode.is("Always")) {
                PacketManager.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                isBlock = true;
            } else if (autoblockMode.is("Packet")) {
                if (isBlock) {
                    PacketManager.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlock = false;
                } else {
                    PacketManager.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    isBlock = true;
                }
            }
        } else if (isBlock && autoblockMode.is("Always")) {
            PacketManager.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.
                    Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            isBlock = false;
        }
    }

    private int getCPS() {
        // 获取最大值
        int maxValue = (minCPS.getMax() - maxCps.getValue()) * 20;
        // 获取最小值
        int minValue = (minCPS.getMax() - minCPS.getValue()) * 20;
        // 返回一个在最小值和最大值之间的随机数
        return (int) ((Math.random() * (maxValue - minValue)) + minValue);
    }

    private void getTargets() {
        targets.clear();
        targets = TargetManager.getTargets(range.getValue());
    }

    // 更新纹理
    private void updateTexture() {
        // 生成纹理
        texture = GL11.glGenTextures();
        try {
            // 上传纹理图像
            TextureUtil.uploadTextureImageAllocate(texture, ImageIO.read(Objects.requireNonNull(ResourceManager.resources.getStream("imgs/capture.png"))), false, false);
        } catch (IOException e) {
            // 异常处理
            Logger.exception(e);
        }
    }

    // 根据旧值、新值和插值值计算插值
    private float interpolate(float oldValue, float newValue, float interpolationValue) {
        // 返回旧值加上新值减去旧值乘以插值值
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }
}
