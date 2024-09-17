package cn.yapeteam.yolbi.module.impl.movement;


import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventJump;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.math.MathUtils;

public class StrafeFix extends Module {
    public StrafeFix() {
        super("StrafeFix", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onStrafe(EventStrafe event) {

        if (YolBi.instance.getRotationManager().isActive() && (event.forward * event.forward + event.strafe * event.strafe) != 0) {
            EventStrafe e = yawToStrafe(YolBi.instance.getRotationManager().getRation().y, direction(event.yaw));
            event.strafe = e.strafe * 0.98f;
            event.forward = e.forward * 0.98f;
            event.yaw = YolBi.instance.getRotationManager().getRation().y;
        }
    }

    @Listener
    public void onJump(EventJump event) {
        event.setYaw(movementYaw());
    }

    public float movementYaw() {
        float y1 = mc.player.getYRot() - YolBi.instance.getRotationManager().getRation().y;
        int time = Math.round(y1 / 45.0f);
        y1 = time * 45;
        StrafeFix strafeFix = YolBi.instance.getModuleManager().getModule(StrafeFix.class);
        return strafeFix.isEnabled() ? YolBi.instance.getRotationManager().getRation().y + y1 : mc.player.getYRot();
    }

    public float direction(float yaw) {
        float rotationYaw = yaw;

        if (mc.player.input.forwardImpulse < 0) {//moveForward
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.player.input.forwardImpulse < 0) {
            forward = -0.5F;
        } else if (mc.player.input.forwardImpulse > 0) {
            forward = 0.5F;
        }

        if (mc.player.input.leftImpulse > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.player.input.leftImpulse < 0) {
            rotationYaw += 70 * forward;
        }

        return rotationYaw;
    }

    public EventStrafe yawToStrafe(float playerYaw, float moveYaw) {
        int angleDiff = (int) ((MathUtils.wrapAngleTo180_double(moveYaw - playerYaw - 22.5f - 135.0f) + 180.0d) / (45.0d));
        EventStrafe event = new EventStrafe();
        switch (angleDiff) {
            case 0:
                event.forward = 1;
                event.strafe = 0;
                break;
            case 1:
                event.forward = 1;
                event.strafe = -1;
                break;
            case 2:
                event.forward = 0;
                event.strafe = -1;
                break;
            case 3:
                event.forward = -1;
                event.strafe = -1;
                break;
            case 4:
                event.forward = -1;
                event.strafe = 0;
                break;
            case 5:
                event.forward = -1;
                event.strafe = 1;
                break;
            case 6:
                event.forward = 0;
                event.strafe = 1;
                break;
            case 7:
                event.forward = 1;
                event.strafe = 1;
                break;

        }
        if (mc.player.input.shiftKeyDown) event.slow(0.3d);
        return event;
    }
}
