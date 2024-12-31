package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import java.awt.event.KeyEvent;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.loading.EarlyLoaderGUI;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;

public class Killaura extends Module {
    private Boolean open = false;
    public Killaura() {
        super("Killaura", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(cpsValue, rangeValue, aimrange,math,s);
    }
    private NumberValue<Double> aimrange = new NumberValue<Double>("aimrange", 4.5, 3.1, 7.1, 0.1);
    private NumberValue<Integer> cpsValue = new NumberValue<Integer>("cps", 11, 1, 20, 1);
    private NumberValue<Integer> math = new NumberValue<Integer>("probability",10,1,100,1);
    private NumberValue<Double> rangeValue = new NumberValue<Double>("range", 3.1d, 2.0, 6.0, 0.01);
    private NumberValue<Integer> player = new NumberValue<Integer>("Player", 1, 0, 1, 1);
    private NumberValue<Integer> s = new NumberValue<Integer>("Team", 1, 0, 1, 1);
    public static LivingEntity target;
    private List<LivingEntity> targets = new ArrayList<>();
    private boolean nowta;
    private int oper = 1;
    private double dealya = -1;
    private float x = 90, y = 58;

    @Override
    protected void onEnable() {
        open = true;
        dealya = 1000 / generate(13, 5);
        if(targets!=null){
            this.targets.clear();
        }
        target = findtarget();
        if (mc.player != null) {
            x = mc.player.getXRot();
            y = mc.player.getYRot();
        }
    }
    public  boolean isKeyPressed() {
        oper += 1;
        return GLFW.glfwGetKey(mc.getWindow().getWindow(), InputConstants.KEY_LEFT) == GLFW.GLFW_PRESS;
    }
    @Override
    protected void onDisable() {
        open = false;
        if(targets!=null){
            this.targets.clear();
        }
        target = null;
    }
    @Listener
    public boolean startauc(EventRender2D e) {
        if(isKeyPressed()){
            if (target == null) return false;
            float[] rotations;
            rotations = RotationUtils.getSimpleRotations(target);
            mc.player.setYBodyRot(rotations[0]);
            mc.player.setSprinting(true);
            if ( mc.player != null) {
                    double distance = jztargetrange(target);
                        if (distance < 3.0) {
                            mc.options.keyDown.setDown(true);
                        } else if(distance == 3.0){
                            mc.options.keyDown.setDown(false);
                            mc.options.keyUp.setDown(false);
                        }else if (distance >= 3.0 ) {
                            mc.options.keyDown.setDown(false);
                            mc.options.keyUp.setDown(true);
                        }
            }
            return false;
        }else {
            mc.options.keyDown.setDown(false);
            mc.options.keyUp.setDown(false);
        }
        return false;
    }

    public final boolean check(LivingEntity a) {
        if(s.getValue() == 1){
            return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player;
        }else if(s.getValue() == 0){
            Player player2 = null;
            if(a instanceof  Player){
                if(mc.player != (Player)a){
                    player2 = (Player) a;
                }else{
                    return false;
                }
            }else {
                return false;
            }
            if(mc.player.getInventory() == null || player2.getInventory() == null){
                return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player;
            }else{
            }
            if(mc.player.getInventory().armor.get(0).equals(player2.getInventory().armor.get(0))&&player2.getInventory().armor!=null &&mc.player.getInventory().armor!=null){
                YolBi.information(mc.player.getInventory().armor.get(mc.player.getInventory().armor.size()).toString() + "  and  " + player2.getInventory().armor.get(player2.getInventory().armor.size()).toString());
                return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player;
            }else {
                return false;
            }

        }else {
            return false;
        }
    }
    public LivingEntity findtarget() {
        targets.clear();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (target == null) {
                    if (livingEntity != mc.player && !livingEntity.isInvisible()) {
                        return livingEntity;
                    }
                }
                if (unjztargetrange(livingEntity) < aimrange.getValue()) {
                    if (target != null) {
                        if (check(livingEntity) && jztargetrange(livingEntity) < jztargetrange(target)&&jztargetrange(livingEntity)<= aimrange.getValue().doubleValue()) {
                            return livingEntity;
                        }
                    } else {
                        if (check(livingEntity) && jztargetrange(livingEntity)<=aimrange.getValue().doubleValue()) {
                            return livingEntity;
                        }
                    }
                }
            }
        }
        return null;
    }
    @Listener
    public void oner(EventMotion event) {
        if(isKeyPressed()){
                target = findtarget();
                nowta = false;
                if (target != null ) {
                    float[] smoothedRotations = RotationUtils.getSimpleRotations(target);
                    if (jztargetrange(target) <= aimrange.getValue()) {
                        if(xj(smoothedRotations[0],mc.player.getYRot())<= 16 - jztargetrange(target)){
                            mc.player.setYRot((float) (smoothedRotations[0] + Math.random()*1.3));
                        }if(xj(smoothedRotations[0],mc.player.getYRot())<= 16 - jztargetrange(target)){
                            mc.player.setXRot((float) (smoothedRotations[1] + Math.random()*1.3));
                        }
                        mc.player.setYRot(smoothedRotations[0]);
                        mc.player.setXRot(smoothedRotations[1]);
                        nowta = true;
                    }
                }
            }
    }

    public final float xj(float a,float b){
        return Math.abs(a - b);
    }
    public final double jztargetrange(LivingEntity a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ()) + Math.abs(a.getY() - mc.player.getY());
        }
        return -1;
    }

    public final double unjztargetrange(LivingEntity a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ());
        }
        return -1;
    }
}
