package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.client.EventClientShutdown;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

@Getter
public class ClickUI extends Module {
    private final BooleanValue pauseGame = new BooleanValue("PauseGame", true);
    private final BooleanValue blur = new BooleanValue("Blur background", () -> !mc.gameSettings.ofFastRender, false);
    private final BooleanValue rainbow = new BooleanValue("RainBow", false);
    private final NumberValue<Integer> blurRadius = new NumberValue<>("blurRadius", blur::getValue, 3, 0, 50, 1);

    public ClickUI() {
        super("ClickGUI", ModuleCategory.VISUAL, Keyboard.KEY_RCONTROL);
        if (ReflectionManager.hasOptifine)
            blur.setCallback((oldV, newV) -> !mc.gameSettings.ofFastRender && newV);
        else blur.setVisibility(() -> true);
        addValues(pauseGame, blur, rainbow, blurRadius);
    }

    @Getter
    private ImplScreen screen = null;

    @Override
    protected void onEnable() {
        if (ReflectionManager.hasOptifine && mc.gameSettings.ofFastRender)
            blur.setValue(false);
        if (screen == null) screen = new ImplScreen();
        mc.displayGuiScreen(screen);
    }

    @Listener
    private void onShutdown(EventClientShutdown e) {
        setEnabled(false);
    }
}
