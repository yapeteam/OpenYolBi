package cn.yapeteam.yolbi.module.impl.combat;


import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.world.Scaffold;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.Rotation;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;


public class KillAura extends Module {
    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(rotationSpeed, rotationSpeed2, slientMove, keepSprint, CPS);
    }

    public Vector2f targetRotation = new Vector2f(0, 0);
    public Vector2f currentRotation = new Vector2f(0, 0);
    public NumberValue<Integer> rotationSpeed = new NumberValue<>("RotationSpeed", 90, 0, 180, 1);
    public NumberValue<Integer> rotationSpeed2 = new NumberValue<>("RotationSpeed2", 90, 0, 180, 1);

    public BooleanValue slientMove = new BooleanValue("SlientMove", true);
    public BooleanValue keepSprint = new BooleanValue("KeepSprint", false);
    public NumberValue<Integer> CPS = new NumberValue<Integer>("CPS", 12, 0, 20, 1);

    @Listener
    public void onUpdate(EventUpdate event) {
        Target targetModule = YolBi.instance.getModuleManager().getModule(Target.class);
        Scaffold scaffold = YolBi.instance.getModuleManager().getModule(Scaffold.class);
        if (scaffold.isEnabled()) return;
        Entity target = targetModule.target;
        double speed = rotationSpeed.getValue() + (rotationSpeed2.getValue() - rotationSpeed.getValue()) * Math.random();
        if (target != null) {
            targetRotation = calculate(mc.player.getEyePosition(), target.getEyePosition());
        } else {
            targetRotation = new Vector2f(mc.player.getXRot(), mc.player.getYRot());
            speed = 180;
        }
        currentRotation = Rotation.limitAngleChange(new Rotation(currentRotation), new Rotation(targetRotation), (float) speed).toVec2f();

        YolBi.instance.getRotationManager().setRation(currentRotation);
        if (mc.hitResult instanceof EntityHitResult && Math.random() < CPS.getValue() / 20d) {
            mc.gameMode.attack(mc.player, ((EntityHitResult) mc.hitResult).getEntity());
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
        if (!keepSprint.getValue()) mc.player.setSprinting(false);
    }

    @Listener
    public void onStrafe(EventStrafe event) {
        Scaffold scaffold = YolBi.instance.getModuleManager().getModule(Scaffold.class);
        if (scaffold.isEnabled()) return;
        if (!slientMove.getValue()) {
            event.yaw = YolBi.instance.getRotationManager().getRation().y;
            event.strafe = mc.player.input.leftImpulse * 0.98f;
            event.forward = mc.player.input.forwardImpulse * 0.98f;
        }
    }

    public static Vector2f calculate(Vec3 player, Vec3 target) {
        double x = target.x - player.x;
        double z = target.z - player.z;
        double xx = x * x;
        double zz = z * z;
        double xz = Math.sqrt(xx + zz);

        return new Vector2f(-(float) (Math.atan2(target.y - player.y, xz) / (Math.PI / 180)),
                -(float) (Math.atan2(target.x - player.x, target.z - player.z) / (Math.PI / 180)));
    }
}
