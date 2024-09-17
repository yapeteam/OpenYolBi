package cn.yapeteam.yolbi.module.impl.render;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import cn.yapeteam.yolbi.utils.render.RenderManager;
import cn.yapeteam.yolbi.utils.vector.Vector2d;
import cn.yapeteam.yolbi.wrappers.ScaledResolutionWrapper;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
        addValues(mixColor1, mixColor2);
    }

    public NumberValue<Integer> mixColor1 = new NumberValue<>("MixColor1", 0xffffff, 0x000000, 0xffffff, 1);
    public NumberValue<Integer> mixColor2 = new NumberValue<>("MixColor2", 0xffffff, 0x000000, 0xffffff, 1);

    @Listener
    public void onRender2D(EventRender2D e) {
        renderArrayList(e.getPoseStack());
    }

    private void renderArrayList(PoseStack poseStack) {
        AbstractFontRenderer font = YolBi.instance.getFontManager().getPingFang18();
        int yCount = 0;
        List<Module> mods = YolBi.instance.getModuleManager().getModules();
        ArrayList<Module> running = new ArrayList<>();

        for (Module m : mods) {
            if (m.isEnabled())
                running.add(m);
        }
        for (Module ignored : running)
            for (int i = 0, runningSize = running.size(); i < runningSize; i++) {
                Module m = running.get(i);
                if (i < runningSize - 1 && font.getStringWidth((m.getName())) < font.getStringWidth(running.get(i + 1).getName())) {
                    running.set(i, running.get(i + 1));
                    running.set(i + 1, m);
                }
            }

        for (Module m : running) {
            ScaledResolutionWrapper sr = new ScaledResolutionWrapper(mc);
            double offset = yCount * (font.getFontHeight("F") + 2);

            if (m.isEnabled()) {
                int color = ColorUtils.mixColors(new Color(mixColor1.getValue()), new Color(mixColor2.getValue()), ColorUtils.getBlendFactor(new Vector2d(sr.getScaledWidth() - font.getStringWidth(m.getName()) - 6, (int) offset))).getRGB();
                RenderManager.drawRoundedRect(poseStack, sr.getScaledWidth(), (int) (6 + offset), sr.getScaledWidth() + 2, (int) (2 + offset + font.getFontHeight("F")), 3, new Color(color).getRGB());
                RenderManager.drawRoundedRect(poseStack, (int) (sr.getScaledWidth() - font.getStringWidth(m.getName()) - 6), (int) offset + 6, sr.getScaledWidth(), (int) (font.getFontHeight("F") + offset + 4), 5, new Color(225, 242, 255, 105).getRGB());
                font.drawStringWithShadow(poseStack, m.getName(),
                        sr.getScaledWidth() - font.getStringWidth(m.getName()) - 4, offset + 8, color);
                yCount++;
            }
        }
    }
}
