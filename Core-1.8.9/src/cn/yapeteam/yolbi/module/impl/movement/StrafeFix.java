package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMoveInput;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.player.MoveUtil;
import net.minecraft.util.MathHelper;

public class StrafeFix extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Fix Mode", "Math", "Math", "Rise");

    private final BooleanValue allowSprint = new BooleanValue("Allow Sprint", () -> mode.is("Math"), false);

    public StrafeFix() {
        super("StrafeFix", ModuleCategory.MOVEMENT);
        this.addValues(mode, allowSprint);
    }

    @Listener
    private void onMoveInput(EventMoveInput event) {
        if (!mode.is("Rise")) return;

        if (rotationManager.active && rotationManager.rotations != null) {
            final float yaw = rotationManager.rotations.x;
            MoveUtil.fixMovement(event, yaw);
        }
    }

    @Listener
    private void onStrafe(EventStrafe event) {
        if (!mode.is("Math")) return;

        if (!rotationManager.active) return;
        if (!allowSprint.getValue())
            mc.thePlayer.setSprinting(false);
        runStrafeFixLoop(event);
    }

    public void runStrafeFixLoop(EventStrafe event) {
        if (event.isCancelled())
            return;

        float yaw = rotationManager.rotations.x;
        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();
        float factor = strafe * strafe + forward * forward;

        if (factor >= 1.0E-4F) {
            factor = MathHelper.sqrt_float(factor);
            if (factor < 1.0F) {
                factor = 1.0F;
            }

            factor = friction / factor;
            strafe *= factor;
            forward *= factor;

            float yawSin = MathHelper.sin((yaw * (float) Math.PI / 180F));
            float yawCos = MathHelper.cos((yaw * (float) Math.PI / 180F));

            mc.thePlayer.motionX += strafe * yawCos - forward * yawSin;
            mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin;
        }

        event.setCancelled(true);
    }

}
