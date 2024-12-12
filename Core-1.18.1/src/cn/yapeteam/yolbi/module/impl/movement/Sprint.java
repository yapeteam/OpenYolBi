package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.phys.Vec3;

public class Sprint extends Module {
    public Sprint(){
        super("Sprint", ModuleCategory.MOVEMENT, InputConstants.KEY_R);
        addValues();
    }
    @Listener
    public void onUpdate(EventRender2D e){
        if(mc.player!=null)return;
        mc.player.setSprinting(true);
    }
}
