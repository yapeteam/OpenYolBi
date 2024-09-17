package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.client.EventClientShutdown;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import lombok.Getter;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

@Getter
public class ClickUI extends Module {
    private final BooleanValue pauseGame = new BooleanValue("PauseGame", true);
    private final BooleanValue blur = new BooleanValue("Blur background", () -> !getOfFastRender(), true);
    private final BooleanValue rainbow = new BooleanValue("RainBow", false);
    private final NumberValue<Integer> blurRadius = new NumberValue<>("blurRadius", blur::getValue, 3, 0, 50, 1);
    private static Field ofFastRender = null;

    static {
        try {
            ofFastRender = GameSettings.class.getField("ofFastRender");
        } catch (NoSuchFieldException ignored) {
        }
    }

    private static boolean getOfFastRender() {
        if (ofFastRender == null) return false;
        try {
            return !(boolean) ofFastRender.get(mc.gameSettings);
        } catch (Throwable e) {
            Logger.exception(e);
        }
        return false;
    }

    public ClickUI() {
        super("ClickGUI", ModuleCategory.VISUAL, Keyboard.KEY_RCONTROL);
        if (ReflectUtil.hasOptifine)
            blur.setCallback((oldV, newV) -> !getOfFastRender() && newV);
        else blur.setVisibility(() -> true);
        addValues(pauseGame, blur, rainbow, blurRadius);
    }

    @Getter
    private final ImplScreen screen = new ImplScreen();

    @Override
    protected void onEnable() {
        if (ReflectUtil.hasOptifine && getOfFastRender())
            blur.setValue(false);
        mc.displayGuiScreen(screen);
    }

    @Listener
    private void onShutdown(EventClientShutdown e) {
        setEnabled(false);
    }
}
