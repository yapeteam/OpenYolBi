package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClickAssist extends Module {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static NumberValue<Integer> ExtraLeft = new NumberValue("Extra Left", 0, 0, 10, 1);
    public static NumberValue<Integer> ExtraRight = new NumberValue("Extra Right", 0, 0, 10, 1);
    public static NumberValue<Integer> MaxClickDelay = new NumberValue("Max Click Delay", 20, 0, 100, 1);
    public static NumberValue<Integer> MinClickDelay = new NumberValue("Min Click Delay", 10, 0, 100, 1);
    public static NumberValue<Integer> MaxClickHold = new NumberValue("Max Click Hold", 12, 0, 20, 1);
    public static NumberValue<Integer> MinClickHold = new NumberValue("Min Click Hold", 8, 0, 20, 1);

    public ClickAssist() {
        super("ClickAssist", ModuleCategory.COMBAT);
        addValues(ExtraLeft, ExtraRight, MaxClickDelay, MinClickDelay, MaxClickHold, MinClickHold);
    }

    public int leftClicks, rightClicks;
    private boolean leftClick, rightClick;

    @Listener
    public void onPreMotion(EventMotion event) {
        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (!leftClick) {
                leftClicks = ExtraLeft.getValue().intValue();
            }
            leftClick = true;
        } else {
            leftClick = false;
        }

        if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (!rightClick) {
                rightClicks = ExtraRight.getValue().intValue();
            }
            rightClick = true;
        } else {
            rightClick = false;
        }

        if (leftClicks > 0 && Math.random() > 0.2) {
            leftClicks--;
            if (!mc.thePlayer.isUsingItem()) {
                int hold = (int) (Math.random() * (MaxClickHold.getValue() - MinClickHold.getValue()) + MinClickHold.getValue());
                int delay = (int) (Math.random() * (MaxClickDelay.getValue() - MinClickDelay.getValue()) + MinClickDelay.getValue());
                scheduler.schedule(() -> {
                    Natives.SendLeft(true);
                    scheduler.schedule(() -> Natives.SendLeft(false), hold, TimeUnit.MILLISECONDS);
                }, delay, TimeUnit.MILLISECONDS);
            }
        } else if (rightClicks > 0 && Math.random() > 0.2) {
            rightClicks--;
            if (!mc.thePlayer.isUsingItem()) {
                int hold = (int) (Math.random() * (MaxClickHold.getValue() - MinClickHold.getValue()) + MinClickHold.getValue());
                int delay = (int) (Math.random() * (MaxClickDelay.getValue() - MinClickDelay.getValue()) + MinClickDelay.getValue());
                scheduler.schedule(() -> {
                    Natives.SendRight(true);
                    scheduler.schedule(() -> Natives.SendRight(false), hold, TimeUnit.MILLISECONDS);
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
    }
}