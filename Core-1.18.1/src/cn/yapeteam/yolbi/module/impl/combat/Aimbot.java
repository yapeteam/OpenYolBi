package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

public class Aimbot extends Module {
    private NumberValue<Double> rangeValue = new NumberValue<>("range", 3.1d, 2.0, 6.0, 0.01);

    private long lastHitTime = 0;
    private Vec3 lastTargetPos = null;
    private double lastTargetSpeed = 0;
    private long lastTargetSwitch = 0;
    private boolean b = false;
    private boolean nowta;
    public static LivingEntity target;
    private LivingEntity lockedTarget = null;
    private List<LivingEntity> targets = new ArrayList<>();

    public Aimbot() {
        super("Aimbot", ModuleCategory.COMBAT);
        addValues(rangeValue);
    }

    @Override
    protected void onEnable() {
        targets.clear();
        target = findTarget();
    }

    @Override
    protected void onDisable() {
        targets.clear();
        target = null;
        lockedTarget = null;
        b = false;
    }

    public void setfalse() {
        if (!isEnabled()) return;
        b = false;
    }

    public void settr() {
        if (!isEnabled()) return;
        b = true;
    }

    public boolean getB() {
        return isEnabled() && b;
    }

    public LivingEntity getTarget2() {
        return target;
    }

    @Listener
    public void oner(EventRender2D event) {
        if (!Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON)) {
            resetTarget();
            return;
        }

        updateTarget();
        if (target != null && unjztargetrange(target) <= rangeValue.getValue()) {
            handleRotations();
        }
    }

    private void resetTarget() {
        if (target != null) {
            target = null;
            lockedTarget = null;
        }
    }

    private void updateTarget() {
        if (target == null) {
            target = findTarget();
        } else if (isTargetInvalid(target)) {
            resetTarget();
        }
    }

    private boolean isTargetInvalid(LivingEntity entity) {
        return entity.isDeadOrDying() || entity.isInvisible() || unjztargetrange(entity) > rangeValue.getValue();
    }

    private void handleRotations() {
        float[] targetRotations = RotationUtils.getSimpleRotations(target);
        float distance = (float)jztargetrange(target);
        
        applyRandomization(targetRotations);
        float smoothSpeed = calculateSmoothSpeed(distance);
        
        if (!target.isDeadOrDying() && !target.isInvisible()) {
            applyRotations(targetRotations, smoothSpeed);
        }
    }

    private void applyRandomization(float[] rotations) {
        if (Math.random() < 0.3) {
            rotations[0] += (float)(Math.random() * 8 - 4);
            rotations[1] += (float)(Math.random() * 6 - 3);
        }
        if (Math.random() < 0.05) {
            rotations[0] += 20;
            rotations[1] += 15;
        }
    }

    private float calculateSmoothSpeed(float distance) {
        float baseSmoothSpeed;
        if (distance < 2.0) baseSmoothSpeed = 0.4f;
        else if (distance < 3.0) baseSmoothSpeed = 0.3f;
        else if (distance < 4.0) baseSmoothSpeed = 0.2f;
        else baseSmoothSpeed = 0.15f;

        baseSmoothSpeed = adjustSmoothSpeed(baseSmoothSpeed);
        return Math.min(0.6f, baseSmoothSpeed);
    }

    private float adjustSmoothSpeed(float speed) {
        if (mc.player.fallDistance > 0) speed *= 1.2f;
        if (target.fallDistance > 0) speed *= 1.1f;
        if (mc.player.hurtTime > 0) speed *= 1.2f;
        return speed;
    }

    private void applyRotations(float[] targetRotations, float smoothSpeed) {
        float[] smoothedRotations = calculateSmoothedRotations(targetRotations, smoothSpeed);
        
        mc.player.setYHeadRot(smoothedRotations[0]);
        mc.player.setYRot(smoothedRotations[0]);
        mc.player.setXRot(smoothedRotations[1]);
        
        updateAimStatus(smoothedRotations, targetRotations);
    }

    private float[] calculateSmoothedRotations(float[] target, float speed) {
        float[] smoothed = new float[2];
        
        float yawChange = wrapAngleTo180(target[0] - mc.player.getYRot());
        float pitchChange = target[1] - mc.player.getXRot();
        
        smoothed[0] = mc.player.getYRot() + (float)(Math.signum(yawChange) * Math.pow(Math.abs(yawChange) * speed, 0.8));
        smoothed[1] = mc.player.getXRot() + (float)(Math.signum(pitchChange) * Math.pow(Math.abs(pitchChange) * speed, 0.8));
        
        smoothed[1] = Math.max(-90, Math.min(90, smoothed[1]));
        return smoothed;
    }

    private void updateAimStatus(float[] current, float[] target) {
        float yawDiff = Math.abs(wrapAngleTo180(current[0] - target[0]));
        float pitchDiff = Math.abs(current[1] - target[1]);
        nowta = yawDiff < 20 && pitchDiff < 20;
    }

    private float wrapAngleTo180(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    public final double jztargetrange(LivingEntity a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ()) + Math.abs(a.getY() - mc.player.getY());
        }
        return -1;
    }

    public final double unjztargetrange(LivingEntity a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ());
        }
        return -1;
    }

    @Listener
    public void onUpdate(EventMotion event) {
        if (mc.level != null) {
            updateTargetSpeed();
        }
    }

    private void updateTargetSpeed() {
        if (target != null && lastTargetPos != null) {
            double dx = target.getX() - lastTargetPos.x;
            double dz = target.getZ() - lastTargetPos.z;
            lastTargetSpeed = Math.sqrt(dx * dx + dz * dz);
            lastTargetPos = target.position();
        }
    }

    private LivingEntity findTarget() {
        if (isLockedTargetValid()) {
            return lockedTarget;
        }

        targets.clear();
        double bestScore = Double.MAX_VALUE;
        Entity bestTarget = null;

        if (System.currentTimeMillis() - lastTargetSwitch < 200) {
            return target;
        }

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (isValidTarget(livingEntity)) {
                    double score = calculateTargetScore(livingEntity);
                    if (score < bestScore) {
                        bestScore = score;
                        bestTarget = entity;
                    }
                }
            }
        }

        if (bestTarget != null) {
            lastTargetSwitch = System.currentTimeMillis();
            lockedTarget = (LivingEntity) bestTarget;
            return lockedTarget;
        }

        return null;
    }

    private boolean isLockedTargetValid() {
        return lockedTarget != null && 
               !lockedTarget.isDeadOrDying() && 
               !lockedTarget.isInvisible() && 
               unjztargetrange(lockedTarget) <= rangeValue.getValue();
    }

    private boolean isValidTarget(LivingEntity entity) {
        return unjztargetrange(entity) < rangeValue.getValue() && check(entity);
    }

    private double calculateTargetScore(LivingEntity entity) {
        return jztargetrange(entity);
    }

    public final boolean check(LivingEntity a) {
        double verticalDist = Math.abs(a.getY() - mc.player.getY());
        if (verticalDist > 3.0) return false;
        return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player;
    }
}