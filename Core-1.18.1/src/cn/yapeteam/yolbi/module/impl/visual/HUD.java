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
import cn.yapeteam.yolbi.config.ConfigManager;
import java.util.stream.Collectors;

public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.VISUAL, InputConstants.KEY_H);
    }
    public ConfigManager cm = YolBi.instance.getcfgmanager();
    public AbstractFontRenderer bt = YolBi.instance.getFontManager().getJelloRegular18();
    public AbstractFontRenderer ERor = YolBi.instance.getFontManager().getMINE18();
    public AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    public String text = "";
    public int x = 2,y=10,c = -1;

    @Listener
    private void renderArrayList(EventRender2D e) {
        PoseStack poseStack = e.getPoseStack();
        try{
            cm.load();
            ERor.drawStringWithShadow(poseStack,"CFG:OK",mc.screen.width/2,mc.screen.height/2,-1);
        }catch (Exception er){
            ERor.drawStringWithShadow(poseStack,"致命错误:CFG读取失败",mc.screen.width/2,mc.screen.height/2,4);
        }
        c+=1;
        if(c>114514){
            c=-1;
        }
        text ="";
        bt.drawStringWithShadow(poseStack,"Yolbi " + VersionInfo.version,2,2,c);
            for (Module module : YolBi.instance.getModuleManager().getModules().stream().filter(Module::isEnabled).collect(Collectors.toList())) {
                text = text + module.getName() + (module.getSuffix() != null ? (" " + ChatFormatting.GRAY + module.getSuffix()) : "");
                text += "\n";
            }
        font.drawStringWithShadow(poseStack,text,x,y,-1);
    }
}
