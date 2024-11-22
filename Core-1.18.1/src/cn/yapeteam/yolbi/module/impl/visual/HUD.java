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
import net.minecraft.ChatFormatting;

import java.util.stream.Collectors;

public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
    }
    public AbstractFontRenderer ERor = YolBi.instance.getFontManager().getMINE18();
    public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    public String text = "";
    public int x = 2, y = 10, c = -1;
    @Listener
    private void renderArrayList(EventRender2D e) {
        mc.options.fov = 140;
        PoseStack poseStack = e.getPoseStack();
        c += 1;
        if (c > 114514) {
            c = -1;
        }
        text = "";
        ERor.drawStringWithShadow(poseStack, "Yolbi " + VersionInfo.version, 2, 2, ColorUtils.rainbow(10,1).getRGB());
        for (Module module : YolBi.instance.getModuleManager().getModules().stream().filter(Module::isEnabled).collect(Collectors.toList())) {
            text = text + module.getName() + (module.getSuffix() != null ? (" " + ChatFormatting.GRAY + module.getSuffix()) : "");
            text += "\n";
        }
        font.drawStringWithShadow(poseStack, text, x, y, -1);

    }

}
