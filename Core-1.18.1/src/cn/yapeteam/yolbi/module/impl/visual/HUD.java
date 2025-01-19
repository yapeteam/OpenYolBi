package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.VersionInfo;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
    }
    
    public AbstractFontRenderer ERor = YolBi.instance.getFontManager().getMINE18();
    public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    public String text = "";
    public int x = 2, y = 10;

    @Listener
    private void renderArrayList(EventRender2D e) {
        PoseStack poseStack = e.getPoseStack();
        text = "";

        ERor.drawStringWithShadow(poseStack, "Yolbi " + VersionInfo.version, 2, 2, ColorUtils.rainbow(10,1).getRGB());

        List<Module> enabledModules = YolBi.instance.getModuleManager().getModules().stream()
            .filter(Module::isEnabled)
            .sorted(Comparator.comparingInt(m -> (int)-font.getStringWidth(m.getName()))) // 强制转换为int
            .collect(Collectors.toList());

        y = 10;

        for (Module module : enabledModules) {
            font.drawStringWithShadow(poseStack, module.getName(), x, y, -1);
            y += (int) font.getFontHeight("A");
        }
    }
}
