package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.item.Items;


public class GrimRunEat extends Module {
    public GrimRunEat(){
        super("GrimRunEat", ModuleCategory.MOVEMENT, InputConstants.KEY_F);
    }
    @Listener
    public void onUpdate(EventPacketSend e){
        if(mc.player == null){
            return;
        }
        if(!mc.player.isUsingItem() && mc.player.getMainHandItem().getItem().equals(Items.GOLDEN_APPLE)){
            mc.options.keyUse.setDown(true);
            mc.options.keyDrop.setDown(true);
            mc.player.setSprinting(true);
        }
    }
}
