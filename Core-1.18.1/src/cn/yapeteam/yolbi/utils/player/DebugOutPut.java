package cn.yapeteam.yolbi.utils.player;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.font.FontManager;
import cn.yapeteam.yolbi.utils.math.WorkOutNextToPlayerPoint;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec3;


import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

public class DebugOutPut {
    static FontManager fm = YolBi.instance.getFontManager();
    private static AbstractFontRenderer r5 = fm.getRENDER5();
    private static AbstractFontRenderer r10 = fm.getRENDER10();
    private static AbstractFontRenderer r20 = fm.getRENDER20();
    public static void renderPredictedPosition(Vec3 target,PoseStack ps) {
        Vec3 projectedPos = mc.gameRenderer.getMainCamera().getPosition();
        if (mc.screen != null) {
            double screenX = (target.x - projectedPos.x) * mc.screen.width / 2 + mc.screen.width / 2;
            double screenY = (target.y - projectedPos.y) * mc.screen.height / 2 + mc.screen.height / 2;
            r20.drawStringWithShadow(ps,"2",screenX,screenY, ColorUtils.color(255,0,0,10));
        }

    }
    public static boolean renderInformationBox(int x1, int y1, int width1, int height1, int rgba, PoseStack ps,int big,String box) {
       if(ps==null){
           return false;
       }
        String text = "";
        for(int i = 0;i<=width1*height1;i++) {
            if (i % width1 == 0) {
                text = text + "\n";
            } else {
                text = text + box;
            }
        }
        if(big == 5){
            r5.drawStringWithShadow(ps,text,x1,y1,rgba);
        }else if(big == 10){
            r10.drawStringWithShadow(ps,text,x1,y1,rgba);
        }else if(big == 20){
            r20.drawStringWithShadow(ps,text,x1,y1,rgba);
        }else {
            return false;
        }
        return true;
    }




    public static void informationDebug(String information){
        if(mc.player==null){
            return;
        }else {
            mc.gui.getChat().addMessage(new TextComponent("[YolbiDebug]:" + information));
        }
    }
}
