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
import cn.yapeteam.yolbi.utils.network.PacketUtils;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.stream.Collectors;

public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
        addValues(fov,color,alpha);
    }
    public NumberValue<Integer> color = new NumberValue<Integer>("Color",-1,-1,256,1);
    private NumberValue<Integer> alpha = new NumberValue<Integer>("Alpha",255,0,255,1);
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
        int rgba = (alpha.getValue() << 24) | (ColorUtils.rainbow(10,1).getRed() << 16) | (ColorUtils.rainbow(10,1).getGreen() << 8) | ColorUtils.rainbow(10,1).getBlue();
        ERor.drawStringWithShadow(poseStack, "Yolbi " + VersionInfo.version, 2, 2, ColorUtils.rainbow(10,1).getRGB());
        for (Module module : YolBi.instance.getModuleManager().getModules().stream().filter(Module::isEnabled).collect(Collectors.toList())) {
            y+=7;
            text = text + module.getName() + " " + module.getSuffix() + "\n";
        }
        font.drawStringWithShadow(poseStack,text,2,10,ColorUtils.color(255,255,255,100));

    }

}
