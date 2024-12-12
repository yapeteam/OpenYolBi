package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.network.PacketUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class AntiKb extends Module {
    public AntiKb (){
        super("AntiKB", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(x,Z);
    }
    public float h ;
    private NumberValue<Integer> x = new NumberValue<Integer>("X",10,0,100,1);
    private int unReduceTimes = 0;
    private NumberValue<Integer> Z = new NumberValue<Integer>("Z",10,0,100,1);
    @Override
    protected void onEnable() {
        if(mc.player==null)return;
        h = mc.player.getHealth();
    }
    @Override
    protected void onDisable() {
        h= -100;
    }
    @Listener
    private void onUp(EventRender2D e){
        if(mc.player==null)return;
        if(mc.player.hurtTime > 0){
            if (unReduceTimes > 0 ) {
                    mc.player.setSprinting(true);
                doReduce();
                unReduceTimes--;
            } else {
                unReduceTimes = 0;
            }
            h = mc.player.getHealth();
        }else{
            h = mc.player.getHealth();
        }
    }
    private void doReduce() {
        for (int i = 0; i < 10; i++) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x *0.6,mc.player.getDeltaMovement().y,mc.player.getDeltaMovement().z*0.6);
        }
    }

}

