package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.managers.PacketManager;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class VanillaAura extends Module {
    private final NumberValue<Integer>
            maxCps = new NumberValue<>("Max CPS", 12, 1, 20, 1),
            minCPS = new NumberValue<>("Min CPS", 9, 1, 20, 1);
    private final NumberValue<Double> range = new NumberValue<>("Range", 8d, 1d, 10d, 1d);
    private final BooleanValue rotation = new BooleanValue("Rotation", false);
    private final BooleanValue smooth = new BooleanValue("Smooth Rotation", rotation::getValue, false);
    private final NumberValue<Integer> rotationSpeed = new NumberValue<>("Rotation Speed", rotation::getValue, 10, 1, 10, 1);
    private final BooleanValue autoblock = new BooleanValue("AutoBlock", true);
    private final ModeValue<String> autoblockMode = new ModeValue<>("AutoBlock Mode", autoblock::getValue, "Always", "Always", "Test");
    private final ModeValue<BlockTiming> blockTiming = new ModeValue<>("AutoBlock Timing", autoblock::getValue, BlockTiming.Pre, BlockTiming.values());

    private enum BlockTiming {
        Pre, Post
    }

    private final BooleanValue swing = new BooleanValue("Swing", true);

    private EntityLivingBase target;
    private List<Entity> targets = new ArrayList<>();
    private boolean canAttack, canBlock;
    public boolean isBlock;
    private int cps;

    private final TimerUtil timer = new TimerUtil();

    public VanillaAura() {
        super("VanillaAura", ModuleCategory.COMBAT);
        addValues(maxCps, minCPS, range, rotation, smooth, rotationSpeed, autoblock, autoblockMode, blockTiming, swing);
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

            if (rotation.getValue()) {
                float[] rotations;
                Vector2f vecRotations = new Vector2f(0, 0);

                // TODO More Rotation
                rotations = RotationsUtil.getRotationsToEntity(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, target, false);

                vecRotations.x = rotations[0];
                vecRotations.y = rotations[1];

                YolBi.instance.getRotationManager().setRotations(vecRotations, rotationSpeed.getValue());
                if (smooth.getValue()) YolBi.instance.getRotationManager().smooth();
            }

            if (Math.sin(cps) + 1 > Math.random() || timer.hasTimePassed(cps) || Math.random() > 0.5) {
                timer.reset();
                cps = getCPS();

                if (swing.getValue()) mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
        } else {
            target = null;
        }

        if (blockTiming.is(BlockTiming.Pre)) runBlock();
    }

    @Listener
    private void onMotionPostUpdate(EventPostMotion event) {
        if (blockTiming.is(BlockTiming.Post)) runBlock();
    }

    private void runBlock() {
        if (canBlock) {
            PacketManager.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            isBlock = true;
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
}
