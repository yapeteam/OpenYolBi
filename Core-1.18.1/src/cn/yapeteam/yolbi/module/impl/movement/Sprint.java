package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventLook;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventMove;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec3;

public class Sprint extends Module {
    public Sprint(){
        super("Sprint", ModuleCategory.MOVEMENT, InputConstants.KEY_R);
        addValues();
    }
    @Listener
    public void onSlowDown(EventLook e){
        e.setRotation(new Vector2f(12F,12F));
        mc.gui.getChat().addMessage(new TextComponent("Run"));
    }
    @Listener
    public void onUpdate(EventRender2D e){
        mc.player.setSprinting(true);
    }
}
