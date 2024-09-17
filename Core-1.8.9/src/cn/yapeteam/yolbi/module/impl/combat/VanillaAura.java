package cn.yapeteam.yolbi.module.impl.combat;

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

            if (Math.sin(cps) + 1 > Math.random() || timer.hasTimePassed(cps) || Math.random() > 0.5) {
                timer.reset();
                cps = getCPS();

                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, target);

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
            PacketManager.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            isBlock = true;
        } else if (isBlock && autoblockMode.is("Always")) {
            PacketManager.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.
                    Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            isBlock = false;
        }
    }

    private void postBlock() {
        if (betterBlock.getValue()) {
            if (canBlock) {
                PacketManager.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
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
