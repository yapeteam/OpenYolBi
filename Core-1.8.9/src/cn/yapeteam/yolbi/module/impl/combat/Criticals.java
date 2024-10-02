package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.managers.PacketManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Packet", "Packet", "LessPacket", "HvH", "LowHop", "Jump");
    private final ModeValue<hvhModeE> hvhMode = new ModeValue<>("HvH Mode", hvhModeE.SmartHop, hvhModeE.values());

    private enum hvhModeE {
        SmartHop, SmartHop2
    }

    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
        addValues(mode);
    }

    private boolean canCritical() {
        return mc.thePlayer != null && mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && !mc.thePlayer.isRiding();
    }

    @Listener
    private void onAttack(EventAttack event) {
        // critical check
        if (!canCritical()) return;

        if (event.getTargetEntity() == null || !(event.getTargetEntity() instanceof EntityLivingBase)) return;

        String curMode = mode.getValue(); // get mode
        double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ; // i'm lazy
        int hurttime = ((EntityLivingBase) event.getTargetEntity()).hurtTime;

        switch (curMode) {
            case "Packet":
                PacketManager.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.011, z, true));
                PacketManager.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                break;
            case "LessPacket":
                PacketManager.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0360000017684527, z, false));
                break;
            case "HvH":
                switch (hvhMode.getValue()) {
                    case SmartHop:
                        if (mc.thePlayer.getDistanceToEntity(event.getTargetEntity()) > 6.0) {
                            mc.thePlayer.setPosition(x, y + 0.3, z);
                        } else {
                            if (MathUtils.isInRange(hurttime, 0, 4)) {
                                mc.thePlayer.motionY = 0.11000000003776;
                                mc.thePlayer.fallDistance = 0.11000000003776f;
                            } else if (MathUtils.isInRange(mc.thePlayer.hurtTime, 0, 4)) {
                                mc.thePlayer.motionY = 0.11000000003776;
                                mc.thePlayer.fallDistance = 0.11000000003776f;
                            }
                        }
                        break;
                    case SmartHop2:
                        if (mc.thePlayer.getDistanceToEntity(event.getTargetEntity()) > 6.0) {
                            mc.thePlayer.setPosition(x, y + 0.3, z);
                        } else {
                            if (MathUtils.isInRange(hurttime, 0, 4)) {
                                mc.thePlayer.motionY = 0.1600000027769;
                                mc.thePlayer.fallDistance = 0.1600000027769f;
                            } else if (MathUtils.isInRange(mc.thePlayer.hurtTime, 0, 4)) {
                                mc.thePlayer.motionY = 0.1600000027769;
                                mc.thePlayer.fallDistance = 0.1600000027769f;
                            }
                        }
                        break;
                }
                break;
            case "LowHop":
                mc.thePlayer.motionY = 0.0787000002337;
                mc.thePlayer.fallDistance = 0.0787000002337f;
                break;
            case "Jump":
                mc.thePlayer.jump(); // may be legit?
                break;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}
