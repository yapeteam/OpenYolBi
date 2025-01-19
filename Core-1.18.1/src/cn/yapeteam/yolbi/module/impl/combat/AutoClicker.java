package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import lombok.Getter;

import java.util.Random;

public class AutoClicker extends Module {
    private final NumberValue<Integer> cps = new NumberValue<>("cps", 17, 1, 100, 1);
    private final NumberValue<Double> range = new NumberValue<>("cps range", 1.5, 0.1d, 2.5d, 0.1);
    private final NumberValue<Integer> pressPercentage = new NumberValue<>("Press Percentage", 20, 0, 100, 1);
    private final BooleanValue leftClick = new BooleanValue("leftClick", true);
    private final BooleanValue rightClick = new BooleanValue("rightClick", false);
    private final BooleanValue smartMode = new BooleanValue("Smart Mode", true);
    private final ModeValue<String> clickprio = new ModeValue<>("Click Priority", "Left", "Left", "Right");
    
    private double delay = -1;
    private long lastClickTime = 0;
    private boolean isComboMode = false;
    private int comboHits = 0;
    private static final Random random = new Random();

    public double getDelay() {
        if(delay > 0) {
            return delay;
        } else {
            return -1.1;
        }
    }

    @Override
    public void onEnable() {
        delay = calculateInitialDelay();
        clickThread = new Thread(() -> {
            while (isEnabled()) {
                try {
                    updateClickingStrategy();
                    sendClick();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        clickThread.start();
    }

    private double calculateInitialDelay() {
        return 1000 / generate(cps.getValue(), range.getValue());
    }

    private void updateClickingStrategy() {
        Killaura ka = YolBi.instance.getka();
        if (ka != null && ka.getTarget2() != null && smartMode.getValue()) {
            if (ka.getTarget2().hurtTime > 0) {
                comboHits++;
                if (comboHits >= 2) {
                    isComboMode = true;
                    delay = 1000 / generate(cps.getValue() + range.getValue(), range.getValue() * 0.5);
                }
            } else {
                if (isComboMode) {
                    delay = 1000 / generate(cps.getValue() - range.getValue(), range.getValue() * 0.5);
                }
                comboHits = 0;
                isComboMode = false;
            }
        } else {
            delay = calculateInitialDelay();
            isComboMode = false;
            comboHits = 0;
        }
    }

    @Getter
    private Thread clickThread = null;

    public AutoClicker() {
        super("AC", ModuleCategory.COMBAT);
        addValues(cps, range, pressPercentage, leftClick, rightClick, smartMode, clickprio);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Natives.SendLeft(false);
            Natives.SendRight(false);
        }));
    }

    public static double generate(double cps, double range) {
        double mean = 1000.0 / cps;
        double stddev = mean * range / cps;
        double noise;
        do {
            noise = mean + random.nextGaussian() * stddev;
        } while (noise <= 0);
        return Math.max(noise, 1);
    }

    private final Runnable leftClickRunnable = () -> {
        try {
            float pressPercentageValue = calculatePressPercentage();
            Natives.SendLeft(true);
            Thread.sleep((long) (delay * pressPercentageValue));
            Natives.SendLeft(false);
            Thread.sleep((long) (delay * (1 - pressPercentageValue)));
        } catch (InterruptedException ignored) {
        }
    };

    private final Runnable rightClickRunnable = () -> {
        try {
            float pressPercentageValue = calculatePressPercentage();
            Natives.SendRight(true);
            Thread.sleep((long) (delay * pressPercentageValue));
            Natives.SendRight(false);
            Thread.sleep((long) (delay * (1 - pressPercentageValue)));
        } catch (InterruptedException ignored) {
        }
    };

    private float calculatePressPercentage() {
        float base = pressPercentage.getValue() / 100f;
        if (isComboMode) {
            base *= 1.2f;
        }
        return Math.min(base, 0.95f);
    }

    private boolean isBreakingBlock() {
        return mc.gameMode != null && mc.gameMode.isDestroying();
    }

    public void sendClick() throws InterruptedException {
        if (!isEnabled() || mc.screen != null || mc.player == null) return;

        if (isBreakingBlock()) return;

        if (smartMode.getValue()) {
            Killaura ka = YolBi.instance.getka();
            if (ka != null && ka.getTarget2() != null && ka.getTarget2().hurtTime > 0 && !isComboMode) {
                Thread.sleep(50 + random.nextInt(30));
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < delay) {
            return;
        }
        lastClickTime = currentTime;

        boolean left = leftClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON);
        boolean right = rightClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_RBUTTON);
        
        if (left && right) {
            if (clickprio.getValue().equals("Left")) {
                leftClickRunnable.run();
                rightClickRunnable.run();
            } else {
                rightClickRunnable.run();
                leftClickRunnable.run();
            }
        } else {
            if (left) {
                leftClickRunnable.run();
            } else if (right) {
                rightClickRunnable.run();
            }
        }
    }

    @Override
    public String getSuffix() {
        return String.format("%.1f ~ %.1f", cps.getValue() - range.getValue(), cps.getValue() + range.getValue());
    }
}
