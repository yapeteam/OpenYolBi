package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.EntityUtils;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import cn.yapeteam.yolbi.utils.*;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Killaura extends Module {

    public Killaura() {
        super("Killaura",ModuleCategory.COMBAT,InputConstants.KEY_R);
        addValues(cpsValue, rangeValue);
    }
    private  NumberValue<Double> aimrange = new NumberValue<Double>("aIMRANGE",4.5,3.1,7.1,0.1);
    private  NumberValue<Integer> cpsValue = new NumberValue<Integer>("CPS", 11, 1, 20, 1);
    private  NumberValue<Double> rangeValue = new NumberValue<Double>("Range", 2.65, 2.0, 6.0, 0.01);
    private  NumberValue<Integer> player = new NumberValue<Integer>("Player",1,0,1,1);
    private static LivingEntity target;
    List<LivingEntity> targets = new ArrayList<>();

    @Override
    protected void onEnable() {
        this.targets.clear();
        target = null;
    }

    @Override
    protected void onDisable() {
        this.targets.clear();
        target = null;
    }
    public final boolean cheak(LivingEntity a){
        return !a.isDeadOrDying()&&!a.isInvisible()&&a!=mc.player;
    }
    public final LivingEntity findtarget(){
       targets.clear();
        //  mc.gui.getChat().addMessage(new TextComponent("AAA3A"));
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (cheak(livingEntity)) {
                    return livingEntity;
                }
            }
        }
        return null;
    }
    @Listener
    public void oner(EventRender2D event) {


        target = null;
        if(targets.isEmpty()||target==null||target!=null&&unjztargetrange(target)>=rangeValue.getValue()){
            target = findtarget();
            if(target==null){
                return;
            }
        }
        if (!targets.isEmpty()) {
            //mc.gui.getChat().addMessage(new TextComponent("AA32A"));
            target = targets.get(0);
        }

        if (target != null) {
          // mc.gui.getChat().addMessage(new TextComponent("AAA2A"));
            float[] rotations = RotationUtils.getSimpleRotations(target);
        //    mc.gui.getChat().addMessage(new TextComponent(String.valueOf(rotations[0])));
         //   mc.gui.getChat().addMessage(new TextComponent(String.valueOf(rotations[1])));
          //  mc.gui.getChat().addMessage(new TextComponent("AAA2A"));
         //   mc.gui.getChat().addMessage(new TextComponent(target.getName().toString()));
            mc.player.setYRot(rotations[0]);
            mc.player.setXRot(rotations[1]);
        }
    }

           // mc.getConnection().send(ServerboundInteractPacket.createAttackPacket(target, mc.player.isShiftKeyDown()));
          //  mc.player.swing(InteractionHand.MAIN_HAND);
public final double jztargetrange(LivingEntity a){return Math.abs(a.getX()-mc.player.getX()) + Math.abs(a.getZ()-mc.player.getZ()) + Math.abs(a.getY()-mc.player.getY());}
public final double unjztargetrange(LivingEntity a){return Math.abs(a.getX()-mc.player.getX()) + Math.abs(a.getZ()-mc.player.getZ()) ;}
}
