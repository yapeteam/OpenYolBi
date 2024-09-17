package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.animation.Animation;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.render.GradientBlur;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HeadUpDisplay extends Module {
    private ClientTheme theme = null;
    private final BooleanValue waterMark = new BooleanValue("Watermark", true);
    private final BooleanValue moduleList = new BooleanValue("Module List", true);
    @Getter
    private final BooleanValue notification = new BooleanValue("Notification", true);
    private final ModeValue<String> font = new ModeValue<>("Font", "PingFang", "Jello", "PingFang", "default");

    private final List<ModuleNode> moduleNodes = new ArrayList<>();
    public static HeadUpDisplay instance;

    public HeadUpDisplay() {
        super("HUD", ModuleCategory.VISUAL);
        addValues(waterMark, moduleList, notification, font);
        instance = this;
    }

    private class ModuleNode {
        private final Module module;

        private ModuleNode(Module module) {
            this.module = module;
        }

        private final GradientBlur blur = new GradientBlur(GradientBlur.Type.TB);
        private final Animation animationY = new Animation(Easing.EASE_OUT_EXPO, 500);
        private final Animation animationX = new Animation(Easing.EASE_IN_OUT_QUAD, 500);
        private float deltaX, y, width, height;
        private int color;

        public void update(ScaledResolution sr, ClientTheme theme, float y, int index) {
            double[] rect = getRect(getText(module), index);
            deltaX = -(float) animationX.animate(module.isEnabled() ? rect[0] : 0);
            this.y = (float) animationY.animate(y);
            width = (float) rect[2];
            height = (float) rect[3];
            float x = sr.getScaledWidth() + deltaX;
            if (module.isEnabled())
                blur.update(x, this.y, width, height);
            color = theme.getColor((int) (this.y * 10));
        }

        public void renderShadow(ScaledResolution sr) {
            float x = sr.getScaledWidth() + deltaX;
            RenderUtil.drawBloomShadow(x, y, width, height, 12, 6, color, false);
        }

        public void render(ScaledResolution sr, float partialTicks) {
            AbstractFontRenderer font = getFontRenderer();
            float x = sr.getScaledWidth() + deltaX;
            GlStateManager.disableBlend();
            blur.render(x, y, width, height, partialTicks, 1);
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 66).getRGB());
            String text = getText(module);
            font.drawString(text, x + 2.5f, y + (height - font.getStringHeight()) / 2f + 0.5f, new Color(0, 0, 0).getRGB());
            font.drawString(text, x + 2, y + (height - font.getStringHeight()) / 2f, color, false);
        }
    }

    @Override
    protected void onEnable() {
        moduleNodes.clear();
        for (Module module : YolBi.instance.getModuleManager().getModules()) {
            ModuleNode node = new ModuleNode(module);
            moduleNodes.add(node);
        }
    }

    @Listener
    private void onRender(EventRender2D e) {
        ScaledResolution sr = e.getScaledresolution();
        if (theme == null)
            theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
        val font = getFontRenderer();
        if (waterMark.getValue())
            font.drawString(YolBi.name + " " + YolBi.version, 2, 2, -1);
        if (moduleList.getValue()) {
            moduleNodes.sort(Comparator.comparingInt(n -> (int) -font.getStringWidth(getText(n.module))));
            float y = 0;
            for (int i = 0; i < moduleNodes.size(); i++) {
                ModuleNode node = moduleNodes.get(i);
                node.update(sr, theme, y, i);
                if (node.module.isEnabled() || !node.animationX.isFinished())
                    y += node.height;
            }
            moduleNodes.stream().filter(node -> node.module.isEnabled()).forEach(node -> node.renderShadow(sr));
            moduleNodes.forEach(node -> node.render(sr, e.getPartialTicks()));
        }
    }

    private double[] getRect(String text, int index) {
        val font = getFontRenderer();
        double width = font.getStringWidth(text) + 4;
        float height = 12;
        double y = index * height;
        return new double[]{width, y, width, height};
    }

    private String getText(Module module) {
        return module.getName() + (module.getSuffix() != null ? " " + module.getSuffix() : "");
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
