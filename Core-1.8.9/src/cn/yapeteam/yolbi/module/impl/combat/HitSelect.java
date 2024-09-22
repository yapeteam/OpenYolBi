package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import net.minecraft.entity.Entity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class HitSelect extends Module {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static final ModeValue<String> mode = new ModeValue<>("Mode", "Instant", "Delayed", "Instant");
    public static final NumberValue<Integer> maxDelay = new NumberValue<>("Max Delay", () -> mode.is("Delayed"), 20, 10, 200, 10);
    public static final NumberValue<Integer> minDelay = new NumberValue<>("Min Delay", () -> mode.is("Delayed"), 10, 10, 200, 10);
    public static final NumberValue<Integer> chance = new NumberValue<>("Chance", 80, 0, 100, 1);

    public HitSelect() {
        super("HitSelect", ModuleCategory.COMBAT);
        addValues(mode, maxDelay, minDelay, chance);
    }

    public static boolean canAttack = false;

    public static Entity tar = null;

    public static boolean attacked = false;

    @Listener
    public void onAttack(EventAttack event) {
        if (!canAttack) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getSuffix() {
        return ("can attack: " + canAttack + " attacked: " + attacked);
    }

    @Listener
    public void onPreUpdate(EventMotion event) {
        // if nobody in 3 block range then we can attack
        if (TargetManager.getTargets(3).isEmpty()) {
            canAttack = true;
            attacked = false;
        } else {
            if (tar != null && TargetManager.getTargets(3).get(0) != tar) {
                attacked = false;
                tar = TargetManager.getTargets(3).get(0);
            }
            canAttack = attacked;
        }
        // if we are hit
        if (mc.thePlayer.hurtTime > 0) {
            if (mode.is("Instant")) {
                Natives.SendLeft(true);
                Natives.SendLeft(false);
            } else {
                scheduler.schedule(() -> {
                    Natives.SendLeft(true);
                    Natives.SendLeft(false);
                }, (long) MathUtils.getRandom(minDelay.getValue().doubleValue(), maxDelay.getValue().doubleValue()), java.util.concurrent.TimeUnit.MILLISECONDS);
            }
            attacked = true;
        }
    }
}
