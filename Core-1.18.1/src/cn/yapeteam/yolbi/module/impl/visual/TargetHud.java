package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.combat.Killaura;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class TargetHud extends Module {
    public TargetHud() {
        super("TargetHUD", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues();
    }
    public Killaura ka = YolBi.instance.getka();
    public final AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    @Listener
    public void on2D(EventRender2D e){
        if(ka.ta!=null){
            String text = "";
            text = text +  String.valueOf(ka.ta.getHealth())+"\n"+ka.ta.getName().getString();
            font.drawStringWithShadow(e.poseStack(),text,mc.screen.width/2,mc.screen.height/2, ColorUtils.rainbow(5,1).getRGB());
        }
    }
}
