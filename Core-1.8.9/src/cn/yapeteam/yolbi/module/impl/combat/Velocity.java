package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.player.EventLivingUpdate;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.managers.PacketManager;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2d;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.MovingObjectPosition;

public class Velocity extends Module {
    enum Mode {
        Cancel,
        Modify,
        CancelS32,
        CancelC0F,
        Matrix,
        Vulcan,
        Hypixel,
        GrimC02,
        BetterJump,
        JumpReset
    }

    int needToUse = 0;
    int start = 0;

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.Cancel, Mode.values());
    private final NumberValue<Float> Horizontal = new NumberValue<>("Horizontal", () -> mode.is(Mode.Modify), 0f, -100f, 100f, 1f);
    private final NumberValue<Float> Vertical = new NumberValue<>("Vertical", () -> mode.is(Mode.Modify), 0f, -100f, 100f, 1f);
    private final NumberValue<Integer> attackCountValue = new NumberValue<>("Packets", () -> mode.is(Mode.GrimC02), 6, 0, 16, 1);

    public Velocity() {
        super("Velocity", ModuleCategory.COMBAT);
        addValues(mode, Horizontal, Vertical, attackCountValue);
    }

    @Listener
    private void onReceive(EventPacket e) {
        Mode mode = this.mode.getValue();
        Packet<?> packet = e.getPacket();
        boolean isS12 = packet instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId();
        if (isS12) {
            S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) packet;
            switch (mode) {
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
            case GrimC02:
                if (!isS12) return;

                S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) packet;
                double horizontalStrength = new Vector2d(s12PacketEntityVelocity.getMotionX(), s12PacketEntityVelocity.getMotionZ()).length();

                if (horizontalStrength <= 1000) return;

                MovingObjectPosition mouse = mc.objectMouseOver;
                Entity entity = null;

                if (mouse.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mouse.entityHit instanceof EntityLivingBase && PlayerUtil.getClosestDistanceToEntity(mouse.entityHit) <= 3) {
                    entity = mouse.entityHit;
                }

                boolean state = ReflectionManager.getEntityPlayerSP$serverSprintState(mc.thePlayer);

                if (entity != null) {
                    if (!state) {
                        PacketManager.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    }

                    YolBi.instance.getEventManager().post(new EventAttack(entity));

                    int count = attackCountValue.getValue();
                    for (int i = 1; i <= count; i++) {
                        mc.thePlayer.swingItem();
                        mc.playerController.attackEntity(mc.thePlayer, entity);
                    }

                    if (!state) {
                        PacketManager.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    }
                }
                break;
        }
    }

    @Listener
    private void onUpdate(EventLivingUpdate event) {
        if (mode.is(Mode.GrimC02)) {
            if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                mc.thePlayer.addVelocity(-1.3E-10, -1.3E-10, -1.3E-10);
                mc.thePlayer.setSprinting(false);
            }
        }
    }

    @Listener
    private void onUpdate(EventUpdate event) {
        if (event.isPre()) {
            if (needToUse == 1) {
                while (mc.thePlayer.hurtTime >= 8) {
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindJump, true);
                    break;
                }
                while (mc.thePlayer.hurtTime >= 7 && !mc.gameSettings.keyBindForward.isPressed()) {
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindForward, true);
                    start = 1;
                    break;
                }
                if (mc.thePlayer.hurtTime < 7 && mc.thePlayer.hurtTime > 0) {
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindJump, false);
                    if (start == 1) {
                        ReflectionManager.SetPressed(mc.gameSettings.keyBindForward, false);
                        start = 0;
                    }
                }
            }

            if (mode.is(Mode.JumpReset)) {
                if (mc.thePlayer.hurtTime >= 8) {
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindJump, true);
                }
                if (mc.thePlayer.hurtTime >= 7) {
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindForward, true);
                } else if (mc.thePlayer.hurtTime >= 4) {
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindJump, false);
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindForward, false);
                } else if (mc.thePlayer.hurtTime > 1) {
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindForward, GameSettings.isKeyDown(mc.gameSettings.keyBindForward));
                    ReflectionManager.SetPressed(mc.gameSettings.keyBindJump, GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
                }
            } else if (mode.is(Mode.BetterJump)) {
                needToUse = 1;
            }
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
