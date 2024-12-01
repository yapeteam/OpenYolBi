package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventAttackReach;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.mixin.injection.CameraMixin;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.xml.stream.events.Attribute;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;

public class Killaura extends Module {
    private Boolean open = false;
    public Killaura() {
        super("Killaura", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(cpsValue, rangeValue, aimrange,math,s);
    }
    private Entity fakeEntity;
    private NumberValue<Double> aimrange = new NumberValue<Double>("aimrange", 4.5, 3.1, 7.1, 0.1);
    private NumberValue<Integer> cpsValue = new NumberValue<Integer>("cps", 11, 1, 20, 1);
    private NumberValue<Integer> math = new NumberValue<Integer>("probability",10,1,100,1);
    private NumberValue<Double> rangeValue = new NumberValue<Double>("range", 3.1d, 2.0, 6.0, 0.01);
    private NumberValue<Integer> player = new NumberValue<Integer>("Player", 1, 0, 1, 1);
    private NumberValue<Integer> s = new NumberValue<Integer>("Team", 1, 0, 1, 1);
    public static LivingEntity target;
    private List<LivingEntity> targets = new ArrayList<>();
    private boolean nowta;
    private double dealya = -1;
    private float x = 90, y = 58;

    @Override
    protected void onEnable() {
        open = true;
        dealya = 1000 / generate(13, 5);
        this.targets.clear();
        target = findtarget();
        x = mc.player.getXRot();
        y = mc.player.getYRot();
    }
    public LivingEntity getTarget2(){
        if(target!=null){
            return target;
        }else{
            return null;
        }
    }
    @Override
    protected void onDisable() {
        open = false;
        this.targets.clear();
        target = null;
    }
    private boolean b = false;
    public void setfalse(){
        b = false;
    }
    public void settr(){
        b = true;
    }
    @Listener
    public boolean startauc(EventRender2D e) {
        PoseStack ps = e.getPoseStack();
        if (target == null) return false;
        float[] rotations;
        rotations = RotationUtils.getSimpleRotations(target);
        mc.player.setYBodyRot(rotations[0]);
        float pressPercentageValue = 17 / 100f;
        if (target != null && nowta && mc.player != null&&Math.abs(rotations[0] - mc.player.getYRot()) <=16d - jztargetrange(target)) {
            if (jztargetrange(target) <= rangeValue.getValue()&&((Math.random() * 100) + 1)/100 <= math.getValue()/100) {
                mc.player.attack(target);
                mc.player.swing(InteractionHand.MAIN_HAND);

            }
        }
        return false;
    }

    public final boolean check(LivingEntity a) {
        if(s.getValue() == 1){
            return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player;
        }else if(s.getValue() == 0){
            mc.gui.getChat().addMessage(new TextComponent("0"));
            return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player && mc.player.getTeam() != a.getTeam();
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
    public void oner(EventRender2D event) {
        target = findtarget();
        nowta = false;
        if (target != null) {

            if (unjztargetrange(target) <= aimrange.getValue()) {
                float[] rotations = RotationUtils.getSimpleRotations(target);
                float tr = (float) jztargetrange(target);
                if (tr >= 16) {
                    tr = 12.9f;
                }
                if (Math.abs(rotations[0] - mc.player.getYRot()) <= 16f - tr-5f) {
                    rotations[0] = mc.player.getYRot();
                }
                if ((int) ((Math.random() * 4) + -3) == 1) {
                    rotations[0] += (float) ((Math.random() * 0.7) + -0.7);
                }
                YolBi.instance.setb(true);
                mc.player.setYHeadRot(rotations[0]);
                mc.player.setYRot(rotations[0]);
                mc.player.setXRot(rotations[1]);
                nowta = true;
            } else if (!mc.player.isOnGround()) {
               mc.player.setYBodyRot(mc.player.getYHeadRot()-180);
            } else if (mc.player.isOnGround()) {
               mc.player.setYBodyRot(mc.player.getYHeadRot());
            }
        } else {
        }
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
