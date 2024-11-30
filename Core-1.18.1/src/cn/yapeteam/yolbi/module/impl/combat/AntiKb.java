package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.concurrent.TimeUnit;


public class AntiKb extends Module {
    public AntiKb (){
        super("AntiKB", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(x,Z,lon);
    }
    public float h ;
    private NumberValue<Integer> x = new NumberValue<Integer>("X",10,0,100,1);
    private NumberValue<Integer> Z = new NumberValue<Integer>("Z",10,0,100,1);
    private NumberValue<Integer> lon = new NumberValue<Integer>("delay",150,20,10000,20);
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
        try{
            TimeUnit.MILLISECONDS.sleep((long)lon.getValue());
        }catch (Exception e){
            mc.gui.getChat().addMessage(new TextComponent("[YolBi|Debug]:Delay Error"));
        }

        double x2 = mc.player.getDeltaMovement().x;
        double z2 = mc.player.getDeltaMovement().z;
        double y2 = mc.player.getDeltaMovement().y;
        if(mc.player.isOnGround()){
            mc.player.setDeltaMovement(0.0, mc.player.getDeltaMovement().y+0.42, 0.0);
        }else{
            if(x2 - x.getValue()/100 <0){
                x2 = 0;
            }else{
                x2 = x2 - x.getValue()/150;
            }
            if(z2 - Z.getValue()/100 <0){
                z2 = 0;
            }else{
                z2 = z2 - Z.getValue()/150;
            }
            y2 = mc.player.getDeltaMovement().y  + 0.42/(y2*0.7);
            mc.player.setDeltaMovement(x2  , y2, z2);
        }

    }
}

