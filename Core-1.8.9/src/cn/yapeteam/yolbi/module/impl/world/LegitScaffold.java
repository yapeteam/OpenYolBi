package cn.yapeteam.yolbi.module.impl.world;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.player.SlotHandler;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import cn.yapeteam.yolbi.utils.render.GradientBlur;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class LegitScaffold extends Module {
    private final NumberValue<Integer> minDelay = new NumberValue<>("Min Delay", 100, 0, 500, 1);
    private final NumberValue<Integer> maxDelay = new NumberValue<>("Max Delay", 200, 0, 500, 1);
    private final BooleanValue pitchCheck = new BooleanValue("Pitch check", true);
    private final NumberValue<Integer> pitch = new NumberValue<>("Pitch", pitchCheck::getValue, 45, 0, 90, 5);
    private final BooleanValue onlySPressed = new BooleanValue("Only S pressed", false);
    private final BooleanValue onlySneak = new BooleanValue("Only sneak", false);
    private final BooleanValue showBlockCount = new BooleanValue("Show block count", false);
    private long lastSneakTime = -1;

    public LegitScaffold() {
        super("LegitScaffold", ModuleCategory.WORLD);
        NumberValue.setBound(minDelay, maxDelay);
        addValues(minDelay, maxDelay, pitchCheck, pitch, onlySPressed, onlySneak, showBlockCount);
    }

    @Listener
    private void onRender(EventRender2D e) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null) return;

        ItemStack item = SlotHandler.getHeldItem();
        if (showBlockCount.getValue()) {
            if (item != null && item.getItem() instanceof ItemBlock) {
                renderStackSize(String.valueOf(item.stackSize), e.getScaledResolution(), e.getPartialTicks());
            } else {
                renderStackSize("0", e.getScaledResolution(), e.getPartialTicks());
            }
        }

        if ((onlySPressed.getValue() && !mc.gameSettings.keyBindBack.isKeyDown())
                || (pitchCheck.getValue() && mc.thePlayer.rotationPitch < pitch.getValue())
                || (onlySneak.getValue() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()))
        ) {
            setSneak(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
            return;
        }

        final long currentTime = System.currentTimeMillis();
        if (PlayerUtil.overAir() || PlayerUtil.onEdge()) {
            setSneak(true);
            lastSneakTime = currentTime;
        } else if (lastSneakTime != -1
                && currentTime - lastSneakTime > Math.random() * (maxDelay.getValue() - minDelay.getValue()) + minDelay.getValue()) {
            setSneak(false);
            lastSneakTime = -1;
        }
    }

    private final GradientBlur blur = new GradientBlur(GradientBlur.Type.LR);

    private void renderStackSize(String text, ScaledResolution sr, float partialTicks) {
        AbstractFontRenderer fontRenderer = YolBi.instance.getFontManager().getPingFang14();
        float width = (float) (fontRenderer.getStringWidth(text) + 10f);
        float height = 15f;
        float x = sr.getScaledWidth() / 2f - width / 2f;
        float y = sr.getScaledHeight() / 2f + 10;
        blur.update(x, y, width, height);
        RenderUtil.drawBloomShadow(x, y, width, height, 5, 5, new Color(0, 0, 0).getRGB(), false);
        blur.render(x, y, width, height, partialTicks, 1);
        fontRenderer.drawString(text, x + 5, y + (height - fontRenderer.getStringHeight("A")) / 2f, -1);
    }

    private void setSneak(boolean sneak) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), sneak);
        ReflectionManager.SetPressed(mc.gameSettings.keyBindSneak, sneak);
    }
}
