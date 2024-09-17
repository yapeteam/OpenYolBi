package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import lombok.Getter;
import net.minecraft.item.ItemFood;
import net.minecraft.util.MovingObjectPosition;

import java.util.Random;

public class AutoClicker extends Module {
    private final NumberValue<Integer> cps = new NumberValue<>("cps", 17, 1, 100, 1);
    private final NumberValue<Double> range = new NumberValue<>("cps range", 1.5, 0.1d, 2.5d, 0.1);
    private final NumberValue<Integer> pressPercentage = new NumberValue<>("Press Percentage", 20, 0, 100, 1);
    private final BooleanValue leftClick = new BooleanValue("leftClick", true),
            rightClick = new BooleanValue("rightClick", false);

    private final BooleanValue noeat = new BooleanValue("No Click When Eating", true);

    private final BooleanValue nomine = new BooleanValue("No Click When Mining", true);
    private final ModeValue<String> clickprio = new ModeValue<>("Click Priority", "Left", "Left", "Right");
    private double delay = 1;

    @Override
    public void onEnable() {
        delay = generate(cps.getValue(), range.getValue());
        clickThread = new Thread(() -> {
            while (isEnabled()) {
                delay = generate(cps.getValue(), range.getValue());
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
        addValues(cps, range, pressPercentage, leftClick, rightClick, noeat, nomine, clickprio);
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
        return noise;
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
        if (!isEnabled() || mc.currentScreen != null || mc.thePlayer == null) return;

        boolean left = leftClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON);
        if (nomine.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            left = false;
        boolean right = rightClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_RBUTTON);
        if ((mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) && noeat.getValue())
            right = false;

        if (left && right) {
            if (clickprio.getValue().equals("Left")) {
                leftClickRunnable.run();
                Thread.sleep((long) (1000 / delay)); // Delay between consecutive clicks
                rightClickRunnable.run();
            } else {
                rightClickRunnable.run();
                Thread.sleep((long) (1000 / delay)); // Delay between consecutive clicks
                leftClickRunnable.run();
            }
        } else {
            if (clickprio.getValue().equals("Left") && left) {
                leftClickRunnable.run();
                Thread.sleep((long) (1000 / delay)); // Ensure delay after each click
            } else if (right) {
                rightClickRunnable.run();
                return;
            }
            if (clickprio.getValue().equals("Right") && right) {
                rightClickRunnable.run();
                Thread.sleep((long) (1000 / delay)); // Ensure delay after each click
            } else if (left) {
                leftClickRunnable.run();
            }
        }
    }

    @Override
    public String getSuffix() {
        return (cps.getValue() - range.getValue()) + " ~ " + (cps.getValue() + range.getValue());
    }
}