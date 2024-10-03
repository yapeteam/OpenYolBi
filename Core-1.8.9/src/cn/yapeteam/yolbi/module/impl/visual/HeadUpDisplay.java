package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HeadUpDisplay extends Module {
    private ClientTheme theme = null;
    private final BooleanValue waterMark = new BooleanValue("Watermark", true);
    private final BooleanValue moduleList = new BooleanValue("Module List", true);
    private final BooleanValue backgroundValue = new BooleanValue("Module List Background", moduleList::getValue, true);
    @Getter
    private final BooleanValue notification = new BooleanValue("Notification", true);
    private final ModeValue<String> font = new ModeValue<>("Font", "PingFang", "Jello", "PingFang", "default");

    public static HeadUpDisplay instance = new HeadUpDisplay();

    public HeadUpDisplay() {
        super("HUD", ModuleCategory.VISUAL);
        addValues(waterMark, moduleList, backgroundValue, notification, font);
    }

    @Listener
    private void onRender(EventRender2D e) {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        ScaledResolution sr = e.getScaledResolution();

        if (theme == null)
            theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);

        val font = getFontRenderer();

        if (waterMark.getValue())
            font.drawString(YolBi.name + " " + YolBi.version, 2, 2, -1);

        if (moduleList.getValue()) {
            final List<Module> modules = new CopyOnWriteArrayList<>();

            for (Module module : YolBi.instance.getModuleManager().getModules()) {
                if (module.isEnabled()) {
                    modules.add(module);
                }
            }

            if (modules.isEmpty()) return;

            modules.sort((o1, o2) -> (int) (getFontRenderer().getStringWidth(o2.getName() + o2.getSuffix()) - getFontRenderer().getStringWidth(o1.getName() + o1.getSuffix())));

            float x;
            float y = 2;
            for (int i = 0; i < modules.size(); ++i) {
                int color = ClientTheme.instance.getColor(i * 10);
                final String suffix = modules.get(i).getSuffix() != null ? " " + modules.get(i).getSuffix() : "";
                final String name = modules.get(i).getName();
                x = sr.getScaledWidth() - (getFontRenderer().getStringWidth(name + suffix)) - 2;

                if (backgroundValue.getValue())
                    RenderUtil.drawRect(x - 2f, y - 3, x + getFontRenderer().getStringWidth(name + suffix) + 3, y + 8f, new Color(0, 0, 0, 150).getRGB());

                getFontRenderer().drawString(name + EnumChatFormatting.GRAY + suffix, x, y, color, true);

                y += 11;
            }
        }
    }

    private AbstractFontRenderer getFontRenderer() {
        switch (font.getValue()) {
            case "Jello":
                return YolBi.instance.getFontManager().getJelloRegular18();
            case "PingFang":
                return YolBi.instance.getFontManager().getPingFang18();
            case "default":
                return YolBi.instance.getFontManager().getDefault18();
        }
        return YolBi.instance.getFontManager().getDefault18();
    }
}
