package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
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
    private final BooleanValue leftClick = new BooleanValue("leftClick", true),
            rightClick = new BooleanValue("rightClick", false);
    private final ModeValue<String> clickprio = new ModeValue<>("Click Priority", "Left", "Left", "Right");
    private double delay = 1;

    @Override
    public void onEnable() {
        delay = 1000 / generate(cps.getValue(), range.getValue());
        clickThread = new Thread(() -> {
            while (isEnabled()) {
                delay = 1000 / generate(cps.getValue(), range.getValue());
                try {
                    sendClick();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        clickThread.start();
    }

    @Getter
    private Thread clickThread = null;

    public AutoClicker() {
        super("AutoClicker", ModuleCategory.COMBAT);
        addValues(cps, range, pressPercentage, leftClick, rightClick, clickprio);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Natives.SendLeft(false);
            Natives.SendRight(false);
        }));
    }

    private static final Random random = new Random();

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
            float pressPercentageValue = pressPercentage.getValue() / 100f;
            Natives.SendLeft(true);
            Thread.sleep((long) (1000 / delay * pressPercentageValue));
            Natives.SendLeft(false);
            Thread.sleep((long) (1000 / delay * (1 - pressPercentageValue)));
        } catch (InterruptedException ignored) {
        }
    };

    private final Runnable rightClickRunnable = () -> {
        try {
            float pressPercentageValue = pressPercentage.getValue() / 100f;
            Natives.SendRight(true);
            Thread.sleep((long) (1000 / delay * pressPercentageValue));
            Natives.SendRight(false);
            Thread.sleep((long) (1000 / delay * (1 - pressPercentageValue)));
        } catch (InterruptedException ignored) {
        }
    };

    public void sendClick() throws InterruptedException {
        if (!isEnabled() || mc.screen != null || mc.player == null) return;
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
        return (cps.getValue() - range.getValue()) + " ~ " + (cps.getValue() + range.getValue());
    }
}
