package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;

public class Killaura extends Module {
    private Boolean open = false;
    public Killaura() {
        super("Killaura",ModuleCategory.COMBAT,InputConstants.KEY_R);
        addValues(cpsValue, rangeValue,aimrange);
    }
    private  NumberValue<Double> aimrange = new NumberValue<Double>("aIMRANGE",4.5,3.1,7.1,0.1);
    private  NumberValue<Integer> cpsValue = new NumberValue<Integer>("CPS", 11, 1, 20, 1);
    private  NumberValue<Double> rangeValue = new NumberValue<Double>("Range", 3.1d, 2.0, 6.0, 0.01);
    private  NumberValue<Integer> player = new NumberValue<Integer>("Player",1,0,1,1);
    private static LivingEntity target;
    private List<LivingEntity> targets = new ArrayList<>();
    private boolean nowta;
    private double dealya = -1;
  //  GameRenderer gr = mc.gameRenderer;
    private float x=90,y=58;
    @Override
    protected void onEnable() {
        open = true;
        dealya = 1000 / generate(13, 5);
        this.targets.clear();
        target = findtarget();
        x = mc.player.getXRot();
        y = mc.player.getYRot();
    }
    @Override
    protected void onDisable() {
        open = false;
        this.targets.clear();
        target = null;
    }
  /*  public static double generate(double cps, double range) {
        double mean = 1000.0 / cps;
        double stddev = mean * range / cps;
        double noise;
        do {
            noise = mean + random.nextGaussian() * stddev;
        } while (noise <= 0);
        return Math.max(noise, 1);
    }
   */ @Listener
    public boolean startauc(EventRender2D e){
        float[] rotations;
        rotations = RotationUtils.getSimpleRotations(target);
        float pressPercentageValue = 17 / 100f;
        if(target!=null&&nowta&&mc.player!=null){
            //rattarget(rotations[0],rotations[1])
            if(rattarget(rotations[0],rotations[1])&&mc.player.canAttack(target)&&jztargetrange(target)<=rangeValue.getValue()){
                mc.getConnection().send(ServerboundInteractPacket.createAttackPacket(target, true));
               // mc.player.attack(target);
                mc.player.swing(InteractionHand.MAIN_HAND);
                try{
                    TimeUnit.MILLISECONDS.sleep((long) (1000 / dealya * pressPercentageValue));
                    return true;
                }catch (Exception ev){
                    return false;
                }

            }

        }

    return false;
    }

    public boolean rattarget(double roY,double roX){
       if(jztargetrange(target)<=rangeValue.getValue()&&target!=null){
           mc.gui.getChat().addMessage(new TextComponent("Rat t"));
           if(mc.player==null){
               mc.gui.getChat().addMessage(new TextComponent("Rat PL f"));
               return false;
           }
           if(Math.abs(roY-mc.player.getYRot())<=20f){
               mc.gui.getChat().addMessage(new TextComponent("Rat ok"));
               return true;
           }

       }else{
           mc.gui.getChat().addMessage(new TextComponent("Rat f"));
       }
        mc.gui.getChat().addMessage(new TextComponent("Rat exit"));
       return false;
    }

    public final boolean check(LivingEntity a){
        return !a.isDeadOrDying()&&!a.isInvisible()&&a!=mc.player;
    }


    public  LivingEntity findtarget(){
       targets.clear();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if(target==null){
                    if(livingEntity!=mc.player&&!livingEntity.isInvisible()){
                      //  mc.gui.getChat().addMessage(new TextComponent(livingEntity.getName().toString()));
                        return livingEntity;
                    }
                }
                if(unjztargetrange(livingEntity)<aimrange.getValue()){
                    if(target!=null){
                        if (check(livingEntity)&&unjztargetrange(livingEntity)<unjztargetrange(target)) {
                           // mc.gui.getChat().addMessage(new TextComponent("A3"));
                            return livingEntity;
                        }
                    }else{
                        if (check(livingEntity)) {
                          //  mc.gui.getChat().addMessage(new TextComponent("A4"));
                            return livingEntity;
                        }
                    }

                }
            }
        }
        return null;
    }
    public LivingEntity ta;

    @Listener
    public void oner(EventRender2D event) {
        target = ta;
        nowta = false;
        if (target != null) {
            if(unjztargetrange(target)<=aimrange.getValue()){
                float[] rotations = RotationUtils.getSimpleRotations(target);
                float tr = (float) jztargetrange(target);
                if(tr>=16){
                    tr = 12.9f;
                }
                if(Math.abs(rotations[0]-mc.player.getYRot())<=16f-tr){
                    rotations[0] = mc.player.getYRot();
                }if(Math.abs(rotations[0]-mc.player.getYRot())<=16f-tr){
                    rotations[1] = mc.player.getXRot();
                }
                if((int)((Math.random()*4) + -3)==1){
                    rotations[0]+= (float) ((Math.random()*0.7) + -0.7);
                }
                if((int)((Math.random()*2) + -1)==1&&jztargetrange(target)<=0.2){
                    mc.player.setOnGround(false);
                    mc.player.setSprinting(true);
                    Vec3 a = target.position();
                    Vec3 ab = new Vec3(0,0,0);
                    ab.add(a.x,mc.player.getY(),a.z);
                    mc.player.moveTo(ab);
                    mc.player.setSprinting(false);
                }
              //  mc.player.setYHeadRot(rotations[0]);
                mc.player.setYBodyRot(-rotations[0]);
                mc.player.setYRot(rotations[0]);
                mc.player.setXRot(rotations[1]);
              //  mc.player.setXRot();
                nowta = true;
                //mc.gui.getChat().addMessage(new TextComponent(target.getName().toString()));
            }else if(!mc.player.isOnGround()){
             //   mc.player.setOnGround(true);
            //    mc.player.setSprinting(true);

                if(mc.player.getYHeadRot()!=0){
                    mc.player.setYBodyRot(-mc.player.getYHeadRot());
                }else{
                    mc.player.setYBodyRot(-90);
                }
            }else if(mc.player.isOnGround()){
                return;
            }
         //   mc.gui.getChat().addMessage(new TextComponent(target.getName().toString()+" SP"));
        }else{
          //  mc.gui.getChat().addMessage(new TextComponent("A"));
        }
    }
           // mc.getConnection().send(ServerboundInteractPacket.createAttackPacket(target, mc.player.isShiftKeyDown()));
          //  mc.player.swing(InteractionHand.MAIN_HAND);
public final double jztargetrange(LivingEntity a){
    if (mc.player != null) {
        return Math.abs(a.getX()-mc.player.getX()) + Math.abs(a.getZ()-mc.player.getZ()) + Math.abs(a.getY()-mc.player.getY());
    }
    return -1;
}
public final double unjztargetrange(LivingEntity a){
    if (mc.player != null) {
        return Math.abs(a.getX()-mc.player.getX()) + Math.abs(a.getZ()-mc.player.getZ()) ;
    }
    return -1;
}
}
