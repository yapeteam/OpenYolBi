package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Killaura extends Module {

    public Killaura() {
        super("Killaura",ModuleCategory.COMBAT,InputConstants.KEY_R);
        addValues(cps, range, aimrange,team,padaren);
    }
    public NumberValue<Double> range = new NumberValue<Double>("range",3.4d,2.5,6.1d,0.1d);
    public NumberValue<Double> aimrange = new NumberValue<Double>("preaim",4.5d,2.5d,10.2d,0.1d);
    public NumberValue<Integer> cps = new NumberValue<Integer>("cps",7,1,20,1);
    public BooleanValue team = new BooleanValue("Team",false);
    public BooleanValue padaren = new BooleanValue("发包打人",true);
    public LivingEntity findtarget(){
        List<LivingEntity> targets = null;
        for (Entity entity : mc.level.entitiesForRendering()) {
            mc.gui.getChat().addMessage(new TextComponent("at"));
            if (entity instanceof LivingEntity) {
                mc.gui.getChat().addMessage(new TextComponent("it1"));
                LivingEntity livingEntity = (LivingEntity)entity;
                if (true) {
                    mc.gui.getChat().addMessage(new TextComponent("it2"));
                    targets.add(livingEntity);
                }
            }
        }
       if(targets!=null){
           mc.gui.getChat().addMessage(new TextComponent("5at"));
           return targets.get(0);
       }
       else{
           mc.gui.getChat().addMessage(new TextComponent("a4t"));
           return null;
       }
    }
    public LivingEntity target;
    @Override
    protected void onEnable() {
        target = findtarget();
    }

    @Override
    protected void onDisable() {
    }
    @Listener
    private void onUpdate(EventRender2D event) {
        try {
            if(mc.player==null)return;
            target = findtarget();
            mc.gui.getChat().addMessage(new TextComponent("a3"));
            if(targetrange(target)<=aimrange.getValue()||target.isDeadOrDying()){
                target = findtarget();
                mc.gui.getChat().addMessage(new TextComponent("a4"));
            }
            if (target != null) {
                mc.gui.getChat().addMessage(new TextComponent("A"));
                float[] rotations = RotationUtils.getSimpleRotations(target);
                mc.player.setYRot(rotations[0]);
                mc.player.setXRot(rotations[1]);
            }else{
                mc.gui.getChat().addMessage(new TextComponent("目标为空"));
            }
            mc.gui.getChat().addMessage(new TextComponent("A"));
        }catch (Exception e){
            mc.gui.getChat().addMessage(new TextComponent("a2"));
        }

    }
    public boolean targetcheak(LivingEntity target){
        return target.isDeadOrDying() || target.isInvisible();
    }
    public double targetrange(LivingEntity target){
        if(mc.player==null)return -1;
        return Math.abs(mc.player.getX() - target.getX())+Math.abs(mc.player.getY() - target.getY())+Math.abs(mc.player.getZ() - target.getZ());
    }
    public long delay = (long) (1000 / generate(cps.getValue(), range.getValue()));
    public void gjtar(LivingEntity target){
        if(target!=null&&mc.player!=null){
            try{
                if(targetrange(target)<=range.getValue()&&padaren.getValue()){
                    mc.player.attack(target);
                }else if(targetrange(target)<=range.getValue()&&!padaren.getValue()){

                }
                TimeUnit.MILLISECONDS.sleep(delay);
            }catch (Exception e){
                mc.gui.getChat().addMessage(new TextComponent(e.getMessage()));
            }
        }
    }
    private static final Random random = new Random();

    public static double generate(double cps, double range) {
        double mean = 1000.0 / cps;
        double stddev = mean * range / cps;
        double noise;
        do {
            noise = mean + random.nextGaussian() * stddev;
        } while (noise <= 0);
        return Math.max(noise, 1);
    }
}
