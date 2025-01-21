package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.Random;

public class AutoClicker extends Module {
    private final NumberValue<Double> minCPS = new NumberValue<>("MinCPS", 8.0, 1.0, 20.0, 0.5);
    private final NumberValue<Double> maxCPS = new NumberValue<>("MaxCPS", 12.0, 1.0, 20.0, 0.5);
    private final BooleanValue smartMode = new BooleanValue("Smart", true);
    private final BooleanValue leftClick = new BooleanValue("LeftClick", true);
    private final BooleanValue rightClick = new BooleanValue("RightClick", false);
    private final ModeValue<String> clickprio = new ModeValue<>("Priority", "Left", "Left", "Right", "Both");
    
    private long lastClickTime = 0;
    private long currentDelay = 0;
    private final Random random = new Random();
    protected final Minecraft mc = Minecraft.getInstance();
    
    public AutoClicker() {
        super("AutoClicker", ModuleCategory.COMBAT);
        addValues(minCPS, maxCPS, smartMode, leftClick, rightClick, clickprio);
    }

    @Override
    public void onEnable() {
        currentDelay = calculateInitialDelay();
    }

    private long calculateInitialDelay() {
        return (long)(1000.0 / generate(minCPS.getValue(), maxCPS.getValue()));
    }

    private double generate(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private boolean isBreakingBlock() {
        return mc.gameMode != null && mc.gameMode.isDestroying();
    }

    @Listener
    public void onUpdate(EventMotion event) {
        if (!isEnabled() || mc.player == null || mc.screen != null) return;
        
        if (isBreakingBlock()) return;

        if (leftClick.getValue() && !Natives.IsKeyDown(InputConstants.MOUSE_BUTTON_LEFT)) return;
        if (rightClick.getValue() && !Natives.IsKeyDown(InputConstants.MOUSE_BUTTON_RIGHT) && clickprio.getValue().equals("Right")) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= currentDelay) {
            sendClick();
            lastClickTime = currentTime;
            updateClickingStrategy();
        }
    }

    private void sendClick() {
        float pressPercentage = calculatePressPercentage();
        
        if (clickprio.getValue().equals("Left")) {
            if (leftClick.getValue()) {
                Natives.SendLeft(true);
                try {
                    Thread.sleep((long) (currentDelay * pressPercentage));
                } catch (InterruptedException ignored) {}
                Natives.SendLeft(false);
            }
            if (rightClick.getValue()) {
                Natives.SendRight(true);
                try {
                    Thread.sleep((long) (currentDelay * pressPercentage));
                } catch (InterruptedException ignored) {}
                Natives.SendRight(false);
            }
        } else if (clickprio.getValue().equals("Right")) {
            if (rightClick.getValue()) {
                Natives.SendRight(true);
                try {
                    Thread.sleep((long) (currentDelay * pressPercentage));
                } catch (InterruptedException ignored) {}
                Natives.SendRight(false);
            }
            if (leftClick.getValue()) {
                Natives.SendLeft(true);
                try {
                    Thread.sleep((long) (currentDelay * pressPercentage));
                } catch (InterruptedException ignored) {}
                Natives.SendLeft(false);
            }
        } else {
            if (leftClick.getValue()) {
                Natives.SendLeft(true);
                try {
                    Thread.sleep((long) (currentDelay * pressPercentage));
                } catch (InterruptedException ignored) {}
                Natives.SendLeft(false);
            }
            if (rightClick.getValue()) {
                Natives.SendRight(true);
                try {
                    Thread.sleep((long) (currentDelay * pressPercentage));
                } catch (InterruptedException ignored) {}
                Natives.SendRight(false);
            }
        }
    }

    private float calculatePressPercentage() {
        float base = 0.5f;
        if (smartMode.getValue()) {
            base = 0.5f + (float) (random.nextGaussian() * 0.25);
        }
        return Math.min(Math.max(base, 0.05f), 0.95f);
    }

    private void updateClickingStrategy() {
        if (smartMode.getValue()) {
            currentDelay = (long)(1000.0 / generate(minCPS.getValue() + 2, maxCPS.getValue() + 2));
        } else {
            currentDelay = calculateInitialDelay();
        }
    }

    @Override
    public String getSuffix() {
        return String.format("%.1f ~ %.1f", minCPS.getValue(), maxCPS.getValue());
    }
}
