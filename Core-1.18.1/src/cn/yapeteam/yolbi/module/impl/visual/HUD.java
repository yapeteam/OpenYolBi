package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.VersionInfo;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.Cam.RenderBox;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.stream.Collectors;

public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
        addValues(fov);
    }
    public AbstractFontRenderer ERor = YolBi.instance.getFontManager().getMINE18();
    public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    private NumberValue<Integer> fov = new NumberValue<Integer>("Fov",140,10,200,2);
    public String text = "";
    public int x = 2, y = 10;

    @Listener
    private void renderArrayList(EventRender2D e) {

        mc.options.fov = fov.getValue();
        PoseStack poseStack = e.getPoseStack();

        text = "";
        ERor.drawStringWithShadow(poseStack, "Yolbi " + VersionInfo.version, 2, 2, ColorUtils.rainbow(10,1).getRGB());
        for (Module module : YolBi.instance.getModuleManager().getModules().stream().filter(Module::isEnabled).collect(Collectors.toList())) {
            font.drawStringWithShadow(poseStack, module.getName(), x, y, -1);
           // RenderBox.drawTransparentRectangle(poseStack,x,y,x + (int)font.getStringWidth(module.getName()),y + (int)font.getFontHeight("A"),0x80CCCCCC);
            y += (int) font.getFontHeight("A");
        }


    }

}
