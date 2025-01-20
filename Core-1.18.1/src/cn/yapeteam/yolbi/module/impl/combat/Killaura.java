package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Killaura extends Module {
    private NumberValue<Double> rangeValue = new NumberValue<>("Range", 3.1d, 2.0, 6.0, 0.01);
    private NumberValue<Double> minCPS = new NumberValue<>("MinCPS", 8.0, 1.0, 20.0, 0.5);
    private NumberValue<Double> maxCPS = new NumberValue<>("MaxCPS", 12.0, 1.0, 20.0, 0.5);
    private BooleanValue smartCPS = new BooleanValue("SmartCPS", true);

    private long lastHitTime = 0;
    private Vec3 lastTargetPos = null;
    private double lastTargetSpeed = 0;
    private long lastTargetSwitch = 0;
    private boolean b = false;
    private boolean nowta;
    public static Player target;
    private Player lockedTarget = null;
    private List<Player> targets = new ArrayList<>();
    private long lastClickTime = 0;
    private long currentDelay = 0;
    private Random random = new Random();

    public Killaura() {
        super("Killaura", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(rangeValue, minCPS, maxCPS, smartCPS);
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
        if (mc.player != null) {
            YolBi.instance.getRotationManager().setRation(new Vector2f(mc.player.getXRot(), mc.player.getYRot()));
        }
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

    public Player getTarget2() {
        return target;
    }

    @Listener
    public void onUpdate(EventMotion event) {
        if (mc.level == null) return;
        
        updateTargetSpeed();
        updateTarget();
        
        if (target == null && mc.player != null) {
            YolBi.instance.getRotationManager().setRation(new Vector2f(mc.player.getXRot(), mc.player.getYRot()));
            return;
        }
        
        if (target != null && unjztargetrange(target) <= rangeValue.getValue()) {
            handleRotations();
            if (nowta) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime >= currentDelay && mc.player.getAttackStrengthScale(0.0f) >= 0.9f) {
                    // 先执行攻击
                    mc.gameMode.attack(mc.player, target);
                    // 然后播放挥刀动画
                    mc.player.swing(InteractionHand.MAIN_HAND);
                    lastClickTime = currentTime;
                    updateClickDelay();
                }
            }
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

    private boolean isTargetInvalid(Player entity) {
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
        applyRotations(smoothedRotations);
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

    private void applyRotations(float[] smoothedRotations) {
        YolBi.instance.getRotationManager().setRation(new Vector2f(smoothedRotations[0], smoothedRotations[1]));
    }

    private void updateAimStatus(float[] current, float[] target) {
        // 计算水平和垂直角度差
        float yawDiff = Math.abs(wrapAngleTo180(current[0] - target[0]));
        float pitchDiff = Math.abs(current[1] - target[1]);
        
        // 检查是否在180度范围内
        boolean inRange = yawDiff <= 180 && pitchDiff <= 180;
        
        // 计算瞄准精度百分比
        float yawAccuracy = (180 - yawDiff) / 180 * 100;
        float pitchAccuracy = (180 - pitchDiff) / 180 * 100;
        
        // 只有当瞄准精度超过95%时才允许攻击
        boolean highAccuracy = yawAccuracy > 95 && pitchAccuracy > 95;
        
        // 更新瞄准状态
        nowta = inRange && highAccuracy;
    }

    private float wrapAngleTo180(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    public final double jztargetrange(Player a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ()) + Math.abs(a.getY() - mc.player.getY());
        }
        return -1;
    }

    public final double unjztargetrange(Player a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ());
        }
        return -1;
    }

    private void updateTargetSpeed() {
        if (target != null && lastTargetPos != null) {
            double dx = target.getX() - lastTargetPos.x;
            double dz = target.getZ() - lastTargetPos.z;
            lastTargetSpeed = Math.sqrt(dx * dx + dz * dz);
            lastTargetPos = target.position();
        }
    }

    private Player findTarget() {
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
            if (entity instanceof Player && entity != mc.player) {
                Player player = (Player) entity;
                if (isValidTarget(player)) {
                    double score = calculateTargetScore(player);
                    if (score < bestScore) {
                        bestScore = score;
                        bestTarget = entity;
                    }
                }
            }
        }

        if (bestTarget != null) {
            lastTargetSwitch = System.currentTimeMillis();
            lockedTarget = (Player) bestTarget;
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

    private boolean isValidTarget(Player player) {
        return unjztargetrange(player) < rangeValue.getValue() && check(player);
    }

    private double calculateTargetScore(Player player) {
        return jztargetrange(player);
    }

    public final boolean check(Player player) {
        double verticalDist = Math.abs(player.getY() - mc.player.getY());
        if (verticalDist > 3.0) return false;
        return !player.isDeadOrDying() && !player.isInvisible() && player != mc.player;
    }

    private void updateClickDelay() {
        double minCpsValue = minCPS.getValue();
        double maxCpsValue = maxCPS.getValue();
        
        if (smartCPS.getValue()) {
            if (target != null) {
                float attackStrength = mc.player.getAttackStrengthScale(0.0f);
                if (attackStrength < 0.9f) {
                    minCpsValue *= 0.5;
                    maxCpsValue *= 0.5;
                }
                
                if (target.hurtTime > 0) {
                    minCpsValue *= 0.8;
                    maxCpsValue *= 0.8;
                }
                double distance = unjztargetrange(target);
                if (distance < 2.0) {
                    minCpsValue *= 1.2;
                    maxCpsValue *= 1.2;
                }
            }
        }
        
        minCpsValue = Math.max(1.0, Math.min(20.0, minCpsValue));
        maxCpsValue = Math.max(minCpsValue, Math.min(20.0, maxCpsValue));
        
        double cps = minCpsValue + random.nextDouble() * (maxCpsValue - minCpsValue);
        currentDelay = (long)(1000.0 / cps);
    }
}