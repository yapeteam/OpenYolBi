package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventChat;
import cn.yapeteam.yolbi.event.impl.player.Render2DEvent;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class cgui extends Module {
    public cgui() {
        super("cgui", ModuleCategory.VISUAL, InputConstants.KEY_RSHIFT);
    }
 /*    }
    AbstractFontRenderer font = YolBi.instance.getFontManager().getMINE14();
    public ModuleManager mm = YolBi.instance.getModuleManager();
   @Listener
    protected void shadowmodule(Render2DEvent e){
        int k = mm.mos.size();
        PoseStack ps;
        String text;
        text = "ClickGui\n";
        ps =  e.getPoseStack();
        for (int i=0;i<k;i++){
            if(mm.mos.get(i)!=null){
                text += mm.mos.get(i).name + " ";
            }
        }
        font.drawStringWithShadow(ps,text,10,10, ColorUtils.rainbow(3,2).getRGB());
    }
    public int findmo(String s,int k){
        String t = " ";
        for(int j=1;j<=k;j++){
            if(s.indent(j).equals(" ") || s.indent(j).isEmpty()){
                break;
            }else{
                t += s.indent(j);
            }
        }
        if(t.indent(0).equals(" ")){
            return -1;
        }
        for(int i=0;i<=mm.mos.size();i++){
            if(mm.mos.get(i).name.equals(t)){
                return i;
            }
        }
        return -1;
    }
    public int findmov(String s,int k){
        String t = " ";
        for(int j=1;j<=k;j++){
            if(s.indent(j).equals(" ") || s.indent(j).isEmpty()){
                return j;
            }else{
                t += s.indent(j);
            }
        }

        return -1;
    }
    public int findmon(String s,int l){
        String t = " ";
        int k = s.length();
        for(int j=l;j<=k;j++){
            if(s.indent(j).equals(" ") || s.indent(j).isEmpty()){
               break;
            }else{
                t += s.indent(j);
            }
        }
        if(t.indent(0).equals(" ")){
            return -1;
        }else{
            int num =0;
            try {
                num = (int) Integer.valueOf(t).doubleValue();
            }catch (Exception e){
                mc.gui.getChat().addMessage(new TextComponent("Eror:In findmon fun"));
            }
        }
        return -1;
    }
    @Listener
    protected void onchat(EventChat ec){
        String s = ec.getMessage();
        if(s.indent(0)=="%"){
            int k = s.length();
            int i = findmo(s,k);
            int v = findmov(s,k);
            if(i == -1){
                mc.gui.getChat().addMessage(new TextComponent("ERor:此数值为空 请检查大小写和拼写"));
                return;
            }

        }
    }*/
}
