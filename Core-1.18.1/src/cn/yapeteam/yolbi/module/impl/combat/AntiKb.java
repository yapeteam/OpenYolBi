package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import com.mojang.blaze3d.platform.InputConstants;

public class AntiKb extends Module {
    public AntiKb (){
        super("AntiKB", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(x,Z);
    }
    public float h ;
    private NumberValue<Integer> x = new NumberValue<Integer>("X",10,0,100,1);
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
        if(mc.player.getHealth()<h){
            clearKnockback();
            h = mc.player.getHealth();
        }else{
            h = mc.player.getHealth();
        }
    }

    private void clearKnockback() {
        double y2 = mc.player.getDeltaMovement().y;
        if(mc.player.isOnGround()){
            mc.player.setDeltaMovement(0.0, mc.player.getDeltaMovement().y+0.42, 0.0);
        }else{
            y2 = mc.player.getDeltaMovement().y  + 0.42/(y2*0.7);
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x*(1 - x.getValue().doubleValue()/100)  , mc.player.getDeltaMovement().y + y2*1.1, mc.player.getDeltaMovement().z*(1 - Z.getValue().doubleValue()/100));
        }

    }
}

