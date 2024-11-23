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
        float yaw = mc.player.getYRot();
        Vec3 velocity = mc.player.getDeltaMovement();
        float forwardDirection = (float) Math.cos(Math.toRadians(yaw));
        float sidewaysDirection = (float) Math.sin(Math.toRadians(yaw));
        double dotProduct = velocity.x * forwardDirection + velocity.z * sidewaysDirection;
        //为正前
        if (dotProduct > 0) {
            mc.player.setSprinting(true);
        }
        //为负后
        else if (dotProduct < 0) {
            mc.player.setSprinting(false);
        } else {
            Natives.SetKeyBoard(InputConstants.KEY_W,true);
            mc.player.setSprinting(true);
            Natives.SetKeyBoard(InputConstants.KEY_W,true);
            Natives.SetKeyBoard(InputConstants.KEY_S,true);
            Natives.SetKeyBoard(InputConstants.KEY_W,false);
        }
        mc.options.fov = 140;
    }
}
