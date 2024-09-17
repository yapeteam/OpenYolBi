package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.KeyCodeMapper;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WTap extends Module {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final NumberValue<Integer> tapMinDelay = new NumberValue<>("Tap Min Delay(ms)", 50, 0, 1000, 1);
    private final NumberValue<Integer> tapMaxDelay = new NumberValue<>("Tap Max Delay(ms)", 200, 0, 1000, 1);
    private final NumberValue<Integer> tapMinHold = new NumberValue<>("Tap Min Hold(ms)", 50, 0, 1000, 1);
    private final NumberValue<Integer> tapMaxHold = new NumberValue<>("Tap Max Hold(ms)", 200, 0, 1000, 1);
    private final NumberValue<Double> tapAccuracy = new NumberValue<>("Tap Accuracy(ms)", 85.0, 0.0, 100.0, 1.0);

    private boolean isTapping = false;

    public WTap() {
        super("WTap", ModuleCategory.COMBAT);
        addValues(tapMinDelay, tapMaxDelay, tapMinHold, tapMaxHold, tapAccuracy);
    }

    @Listener
    private void onAttack(EventAttack event) {
        if (mc.thePlayer.isSprinting() || Natives.IsKeyDown(VirtualKeyBoard.VK_LCONTROL)) {
            int delay;
            if(!(Math.random() * 100 < tapAccuracy.getValue())){
                delay = 0;
            }else{
                // delay based on the min and max delay
                delay = (int) (Math.random() * (tapMaxDelay.getValue() - tapMinDelay.getValue()) + tapMinDelay.getValue());
            }
            // hold based on the min and max hold
            int hold = (int) (Math.random() * (tapMaxHold.getValue() - tapMinHold.getValue()) + tapMinHold.getValue());
            // set tapping to true
            scheduler.schedule(() -> {
                isTapping = true;
                scheduler.schedule(() -> isTapping = false, hold, TimeUnit.MILLISECONDS); // Press 'W' again after hold
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    @Listener
    private void onPreMotion(EventMotion event) {
        if(isTapping){
            Natives.SetKeyBoard(KeyCodeMapper.mapLWJGLKeyCode(mc.gameSettings.keyBindForward.getKeyCode()), false);
        }
    }
}