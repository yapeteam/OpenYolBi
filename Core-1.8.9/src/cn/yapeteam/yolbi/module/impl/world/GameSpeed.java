package cn.yapeteam.yolbi.module.impl.world;

import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;

import java.util.Objects;

public class GameSpeed extends Module {
    private final NumberValue<Float> timer = new NumberValue<>("Timer", 1f, 0.1f, 10f, 0.1f);

    private float lastTimer = 1;

    public GameSpeed() {
        super("GameSpeed", ModuleCategory.WORLD);
        addValues(timer);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        lastTimer = Objects.requireNonNull(ReflectionManager.Minecraft$getTimer(mc)).timerSpeed;
        Objects.requireNonNull(ReflectionManager.Minecraft$getTimer(mc)).timerSpeed = timer.getValue();
    }

    @Override
    protected void onDisable() {
        Objects.requireNonNull(ReflectionManager.Minecraft$getTimer(mc)).timerSpeed = lastTimer;
        super.onDisable();
    }

    @Override
    public String getSuffix() {
        return timer.getValue().toString();
    }
}
