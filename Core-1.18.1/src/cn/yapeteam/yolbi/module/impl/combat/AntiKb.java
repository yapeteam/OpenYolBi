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
        super("反击退", ModuleCategory.COMBAT, InputConstants.KEY_R);
    }
    public float h ;
    private NumberValue<Integer> x = new NumberValue<Integer>("X轴击退减少",10,0,100,1);
    private NumberValue<Integer> Z = new NumberValue<Integer>("Z轴击退减少",10,0,100,1);
    private NumberValue<Integer> lon = new NumberValue<Integer>("延时",150,20,10000,20);
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
        if(mc.player.isOnGround()){
            mc.player.setDeltaMovement(0.0, mc.player.getDeltaMovement().y+0.42, 0.0);
        }else{
            if(x2 - x.getValue()/100 <0){
                x2 = 0;
            }
            if(z2 - Z.getValue()/100 <0){
                z2 = 0;
            }

            mc.player.setDeltaMovement(x2 , mc.player.getDeltaMovement().y, z2);
        }

    }
}

