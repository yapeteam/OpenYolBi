package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.VersionInfo;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;

import java.util.stream.Collectors;

public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
    }

    @Listener
    private void renderArrayList(EventRender2D e) {
        PoseStack poseStack = e.getPoseStack();
        AbstractFontRenderer font = YolBi.instance.getFontManager().getPingFang14();

        float y = 2, height = 8;
        font.drawStringWithShadow(poseStack, "YolBi " + VersionInfo.version, 2, 2, -1);
        y += height;
        for (Module module : YolBi.instance.getModuleManager().getModules().stream().filter(Module::isEnabled).collect(Collectors.toList())) {
            font.drawStringWithShadow(poseStack, module.getName() + (module.getSuffix() != null ? (" " + ChatFormatting.GRAY + module.getSuffix()) : ""), 2, y, -1);
            y += height;
        }
    }
}
