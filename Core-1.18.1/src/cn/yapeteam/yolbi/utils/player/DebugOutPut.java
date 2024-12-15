package cn.yapeteam.yolbi.utils.player;

import net.minecraft.network.chat.TextComponent;

import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

public class DebugOutPut {
    public static void informationDebug(String information){
        if(mc.player==null){
            return;
        }else {
            mc.gui.getChat().addMessage(new TextComponent("[YolbiDebug]:" + information));
        }
    }
}
