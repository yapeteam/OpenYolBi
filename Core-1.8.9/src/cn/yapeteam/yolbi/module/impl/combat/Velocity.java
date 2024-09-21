package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class Velocity extends Module {
    enum Mode {
        Normal,
        Cancel,
        Modify,
        CancelS32,
        CancelC0F,
        Matrix,
        Vulcan,
        Hypixel,
        GrimNoXZ
    }

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.Normal, Mode.values());
    private final NumberValue<Float> Horizontal = new NumberValue<>("Horizontal", () -> mode.is(Mode.Modify), 0f, -100f, 100f, 1f);
    private final NumberValue<Float> Vertical = new NumberValue<>("Vertical", () -> mode.is(Mode.Modify), 0f, -100f, 100f, 1f);
    private final NumberValue<Float>
            yawLegitModify = new NumberValue<>("Legit Yaw Modify", () -> mode.is(Mode.GrimNoXZ), 1f, 0f, 2f, 0.1f),
            offset = new NumberValue<>("Angle Offset", () -> mode.is(Mode.GrimNoXZ), 0f, 180f, -180f, 5f);

    public Velocity() {
        super("Velocity", ModuleCategory.COMBAT);
        addValues(mode, Horizontal, Vertical, yawLegitModify, offset);
    }

    @Listener
    private void onReceive(EventPacket e) {
        Mode mode = this.mode.getValue();
        Packet<?> packet = e.getPacket();
        boolean isS12 = packet instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId();
        if (isS12) {
            S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) packet;
            switch (mode) {
                case Normal:
                    ReflectionManager.S12PacketEntityVelocity$setMotionX(s12PacketEntityVelocity, 0);
                    ReflectionManager.S12PacketEntityVelocity$setMotionY(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionY() * 0.99905));
                    ReflectionManager.S12PacketEntityVelocity$setMotionZ(s12PacketEntityVelocity, 0);
                    break;
                case Vulcan:
                case Cancel:
                    e.setCancelled(true);
                    break;
                case Modify:
                    ReflectionManager.S12PacketEntityVelocity$setMotionX(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionX() * this.Horizontal.getValue().doubleValue() / 100.0));
                    ReflectionManager.S12PacketEntityVelocity$setMotionY(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionY() * this.Vertical.getValue().doubleValue() / 100.0));
                    ReflectionManager.S12PacketEntityVelocity$setMotionZ(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionZ() * this.Horizontal.getValue().doubleValue() / 100.0));
                    break;
            }
        }
        switch (mode) {
            case CancelS32:
                if (packet instanceof S32PacketConfirmTransaction)
                    e.setCancelled(true);
                if (isS12)
                    e.setCancelled(true);
                if (packet instanceof S27PacketExplosion)
                    e.setCancelled(true);
                break;
            case CancelC0F:
                if (packet instanceof C0FPacketConfirmTransaction)
                    e.setCancelled(true);
                if (isS12)
                    e.setCancelled(true);
                if (packet instanceof S27PacketExplosion)
                    e.setCancelled(true);
                break;
            case Matrix:
                if (isS12) {
                    S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) packet;
                    ReflectionManager.S12PacketEntityVelocity$setMotionX(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionX() * 0.36));
                    ReflectionManager.S12PacketEntityVelocity$setMotionZ(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionZ() * 0.36));
                    if (mc.thePlayer.onGround) {
                        ReflectionManager.S12PacketEntityVelocity$setMotionX(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionX() * 0.9));
                        ReflectionManager.S12PacketEntityVelocity$setMotionZ(s12PacketEntityVelocity, (int) ((double) s12PacketEntityVelocity.getMotionZ() * 0.9));
                    }
                }
                break;
            case Hypixel:
                if (isS12) {
                    S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) packet;
                    if (mc.thePlayer.onGround) {
                        ReflectionManager.S12PacketEntityVelocity$setMotionX(s12PacketEntityVelocity, 0);
                        ReflectionManager.S12PacketEntityVelocity$setMotionZ(s12PacketEntityVelocity, 0);
                    } else e.setCancelled(true);
                }
                break;
            case GrimNoXZ:
                float yaw;
                if (isS12) {
                    S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) packet;
                    yaw = -(mc.thePlayer.rotationYaw + offset.getValue() + 180);
                    double velocity = Math.sqrt(s12PacketEntityVelocity.getMotionX() * s12PacketEntityVelocity.getMotionX() + s12PacketEntityVelocity.getMotionZ() * s12PacketEntityVelocity.getMotionZ()) * yawLegitModify.getValue();
                    ReflectionManager.S12PacketEntityVelocity$setMotionX(s12PacketEntityVelocity, (int) (velocity * Math.sin(yaw / 180 * Math.PI)));
                    ReflectionManager.S12PacketEntityVelocity$setMotionZ(s12PacketEntityVelocity, (int) (velocity * Math.cos(yaw / 180 * Math.PI)));
                }
                break;
        }
    }
}
