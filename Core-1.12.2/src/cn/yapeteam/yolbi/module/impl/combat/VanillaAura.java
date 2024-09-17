package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class VanillaAura extends Module {
    private final NumberValue<Integer>
            maxCps = new NumberValue<>("Max CPS", 12, 1, 20, 1),
            minCPS = new NumberValue<>("Min CPS", 9, 1, 20, 1);
    private final NumberValue<Double> range = new NumberValue<>("Range", 8d, 1d, 10d, 1d);
    private final BooleanValue autoblock = new BooleanValue("AutoBlock", true);
    private final ModeValue<String> autoblockMode = new ModeValue<>("AutoBlock Mode", autoblock::getValue, "Always", "Always", "Test");
    private final BooleanValue betterBlock = new BooleanValue("Better Block", autoblock::getValue, true);

    private EntityLivingBase target;
    private List<Entity> targets = new ArrayList<>();
    private boolean canAttack, canBlock;
    public boolean isBlock;
    private int cps;

    private final TimerUtil timer = new TimerUtil();

    public VanillaAura() {
        super("VanillaAura", ModuleCategory.COMBAT);
        addValues(maxCps, minCPS, range, autoblock, autoblockMode, betterBlock);
    }

    @Override
    protected void onDisable() {
        target = null;
        targets.clear();
        canAttack = canBlock = false;
        if (isBlock) {
            PacketUtil.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
        isBlock = false;
        cps = 0;
        super.onDisable();
    }

    @Listener
    private void onMotionPreUpdate(EventMotion event) {
        getTargets();

        canAttack = !targets.isEmpty();
        canBlock = autoblock.getValue() && canAttack && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword;

        if (canAttack) {
            // 因为狗屎TargetManager所以只能强制转换
            target = (EntityLivingBase) targets.get(0);

            if (Math.sin(cps) + 1 > Math.random() || timer.hasTimePassed(cps) || Math.random() > 0.5) {
                timer.reset();
                cps = getCPS();

                // 1.8.9 != 1.9+
                mc.playerController.attackEntity(mc.player, target);
                mc.player.swingArm(EnumHand.MAIN_HAND);

                // attackEntity已经call了event
//                EventAttack attack = new EventAttack(target);
//                YolBi.instance.getEventManager().post(attack);
//
//                if (!attack.isCancelled()) {
//                    mc.thePlayer.swingItem();
//                    mc.playerController.attackEntity(mc.thePlayer, target);
//                }
            }
        } else {
            target = null;
        }

        preBlock();
    }

    @Listener
    private void onMotionPostUpdate(EventPostMotion event) {
        postBlock();
    }

    private void preBlock() {
        if (canBlock) {
            PacketUtil.sendPacket(new CPacketPlayerTryUseItemOnBlock());
            isBlock = true;
        } else if (isBlock && autoblockMode.is("Always")) {
            PacketUtil.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.
                    Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            isBlock = false;
        }
    }

    private void postBlock() {
        if (betterBlock.getValue()) {
            if (canBlock) {
                PacketUtil.sendPacket(new CPacketPlayerTryUseItemOnBlock());
                isBlock = true;
            }
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
