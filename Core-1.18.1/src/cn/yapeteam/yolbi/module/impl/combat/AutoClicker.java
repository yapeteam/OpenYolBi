package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import net.minecraft.client.Minecraft;

import java.util.Random;

public class AutoClicker extends Module {
    private final NumberValue<Double> minCPS = new NumberValue<>("MinCPS", 12.0, 1.0, 20.0, 0.5);
    private final NumberValue<Double> maxCPS = new NumberValue<>("MaxCPS", 16.0, 1.0, 20.0, 0.5);
    private final BooleanValue leftClick = new BooleanValue("LeftClick", true);
    private final BooleanValue rightClick = new BooleanValue("RightClick", false);
    
    private long lastClickTime = 0;
    private long currentDelay = 0;
    private final Random random = new Random();
    protected final Minecraft mc = Minecraft.getInstance();
    private boolean isLeftPressed = false;
    private boolean isRightPressed = false;
    
    public AutoClicker() {
        super("AutoClicker", ModuleCategory.COMBAT);
        addValues(minCPS, maxCPS, leftClick, rightClick);
    }

    @Override
    protected void onEnable() {
        lastClickTime = System.currentTimeMillis();
        updateClickDelay();
        isLeftPressed = false;
        isRightPressed = false;
    }

    @Override
    protected void onDisable() {
        // 确保释放所有按键
        if(isLeftPressed) {
            Natives.SendLeft(false);
            isLeftPressed = false;
        }
        if(isRightPressed) {
            Natives.SendRight(false);
            isRightPressed = false;
        }
    }

    private boolean shouldClick() {
        // 如果打开了UI界面,不点击
        if (mc.screen != null) return false;
        
        // 如果正在挖掘方块,不点击
        if (mc.gameMode != null && mc.gameMode.isDestroying()) return false;
        
        // 检查是否有任何一个按键需要点击
        boolean shouldLeft = leftClick.getValue() && mc.options.keyAttack.isDown();
        boolean shouldRight = rightClick.getValue() && mc.options.keyUse.isDown();
        
        return shouldLeft || shouldRight;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (!isEnabled() || !shouldClick()) {
            // 如果不应该点击，确保释放按键
            if(isLeftPressed) {
                Natives.SendLeft(false);
                isLeftPressed = false;
            }
            if(isRightPressed) {
                Natives.SendRight(false);
                isRightPressed = false;
            }
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= currentDelay) {
            // 执行点击
            if(leftClick.getValue() && mc.options.keyAttack.isDown()) {
                Natives.SendLeft(true);
                isLeftPressed = true;
            }
            if(rightClick.getValue() && mc.options.keyUse.isDown()) {
                Natives.SendRight(true);
                isRightPressed = true;
            }
            
            // 短暂延迟后释放按键
            try {
                Thread.sleep(25); // 模拟真实点击时长
            } catch (InterruptedException ignored) {}
            
            if(isLeftPressed) {
                Natives.SendLeft(false);
                isLeftPressed = false;
            }
            if(isRightPressed) {
                Natives.SendRight(false);
                isRightPressed = false;
            }
            
            // 更新时间和延迟
            lastClickTime = currentTime;
            updateClickDelay();
        }
    }

    private void updateClickDelay() {
        double minCpsValue = minCPS.getValue();
        double maxCpsValue = maxCPS.getValue();
        
        // 确保CPS在有效范围内
        minCpsValue = Math.max(1.0, Math.min(20.0, minCpsValue));
        maxCpsValue = Math.max(minCpsValue, Math.min(20.0, maxCpsValue));
        
        // 生成随机CPS
        double cps = minCpsValue + random.nextDouble() * (maxCpsValue - minCpsValue);
        currentDelay = (long)(1000.0 / cps);
    }

    @Override
    public String getSuffix() {
        return String.format("%.1f ~ %.1f", minCPS.getValue(), maxCPS.getValue());
    }
}
