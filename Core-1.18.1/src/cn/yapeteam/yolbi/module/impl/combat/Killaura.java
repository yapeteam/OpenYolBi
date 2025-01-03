package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Thread.Thread_KillauraPacket;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.math.WorkOutNextToPlayerPoint;
import cn.yapeteam.yolbi.utils.player.RotationUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static cn.yapeteam.yolbi.module.impl.combat.AutoClicker.generate;

public class Killaura extends Module {
    public Killaura() {
        super("Killaura", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(cpsValue,ab, rangeValue, aimrange,math,team,packet,legit);
    }
    private NumberValue<Double> aimrange = new NumberValue<Double>("aimrange", 4.5, 3.1, 7.1, 0.1);
    private NumberValue<Integer> cpsValue = new NumberValue<Integer>("cps", 11, 1, 20, 1);
    private NumberValue<Integer> ab = new NumberValue<Integer>("cps range",5,1,10,1);
    private NumberValue<Integer> math = new NumberValue<Integer>("probability",10,1,100,1);
    private NumberValue<Double> rangeValue = new NumberValue<Double>("range", 3.1d, 2.0, 6.0, 0.01);
    private NumberValue<Integer> player = new NumberValue<Integer>("Player", 1, 0, 1, 1);
    private BooleanValue team = new BooleanValue("Team","BJDTEAM or False",true);
    private BooleanValue packet = new BooleanValue("Packet",true);
    private BooleanValue legit = new BooleanValue("Legit",false);
    public static LivingEntity target;
    private List<LivingEntity> targets = new ArrayList<>();
    private int oper = 1;
    private double dealya = -1;
    private float x = 90, y = 58;

    @Override
    protected void onEnable() {
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
    @Override
    protected void onDisable() {
        if(targets!=null){
            this.targets.clear();
        }
        target = null;
        if(!Natives.IsKeyDown(InputConstants.KEY_S)&&mc.options.keyDown.isDown()){
            mc.options.keyDown.setDown(false);
        }
        if(!Natives.IsKeyDown(InputConstants.KEY_W)&&mc.options.keyUp.isDown()){
            mc.options.keyUp.setDown(false);
        }
    }
    @Listener
    public boolean startauc(EventRender2D e) {
        if(this.isEnabled()&&Natives.IsKeyDown(this.getKey())){
            if (target == null || mc.player == null){
                YolBi.information("ii");
                return false;
            }
            if ( mc.player != null) {
                    double distance = jztargetrange(target);
                        if (distance < 3.0) {
                            mc.options.keyDown.setDown(true);
                        } else if(distance == 3.0){
                            mc.options.keyDown.setDown(true);
                        }else if (distance >= 3.0 &&distance<= aimrange.getValue()) {
                            mc.options.keyUp.setDown(true);
                            mc.options.keyDown.setDown(false);
                        }else if(distance> aimrange.getValue()){
                            if(!Natives.IsKeyDown(InputConstants.KEY_S)&&mc.options.keyDown.isDown()){
                                mc.options.keyDown.setDown(false);
                            }
                            if(!Natives.IsKeyDown(InputConstants.KEY_W)&&mc.options.keyUp.isDown()){
                                mc.options.keyUp.setDown(false);
                            }
                        }
            }
            return false;
        }else {
            if(!Natives.IsKeyDown(InputConstants.KEY_S)&&mc.options.keyDown.isDown()){
                mc.options.keyDown.setDown(false);
            }
            if(!Natives.IsKeyDown(InputConstants.KEY_W)&&mc.options.keyUp.isDown()){
                mc.options.keyUp.setDown(false);
            }
        }
        return false;
    }

    public final boolean check(LivingEntity a) {
        if(!team.getValue()){
            return !a.isDeadOrDying() && !a.isInvisible() && a != mc.player;
        }else if(team.getValue()){
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
            if (mc.player.getInventory().armor != null&&player2.getInventory().armor!=null) {
                if(mc.player.getInventory().armor.get(3).getItem() == player2.getInventory().armor.get(3).getItem()){
                    YolBi.information("Join");
                    return false;
                }
            }
            return true;
        }else {
            return false;
        }
    }
    private Vec3 PreMoveForTarget(LivingEntity livingEntity){
        double posX = livingEntity.getX();
        double posY = livingEntity.getY();
        double posZ = livingEntity.getZ();
        double motionX = livingEntity.getDeltaMovement().x;
        double motionY = livingEntity.getDeltaMovement().y;
        double motionZ = livingEntity.getDeltaMovement().z;
        double nextPosX = posX + motionX;
        double nextPosY = posY + motionY;
        double nextPosZ = posZ + motionZ;
        return new Vec3(nextPosX,nextPosY,nextPosZ);
    }
    public static Vec3 getClosestPointToPlayer(LivingEntity target) {
        WorkOutNextToPlayerPoint.Vec3d playerPosition = new WorkOutNextToPlayerPoint.Vec3d(mc.player.getX(),mc.player.getY()-0.1f,mc.player.getZ());
        WorkOutNextToPlayerPoint.AxisAlignedBB boundingBox = new WorkOutNextToPlayerPoint.AxisAlignedBB(target.getBoundingBox().minX, target.getBoundingBox().minY, target.getBoundingBox().minZ, target.getBoundingBox().maxX, target.getBoundingBox().maxY, target.getBoundingBox().maxZ);
        WorkOutNextToPlayerPoint.Vec3d closestPoint = WorkOutNextToPlayerPoint.getClosestPointToPlayer(playerPosition, boundingBox);
       return new Vec3(closestPoint.x,closestPoint.y,closestPoint.z);

    }
    public static void renderAndDecorateFakeItem(PoseStack matrixStack, ItemStack itemStack, int x, int y, float rotation) {
        ItemRenderer itemRenderer = mc.getItemRenderer();
        itemRenderer.renderAndDecorateFakeItem(itemStack, x, y);
        matrixStack.pushPose();
        matrixStack.translate(x + 8, y + 8, 0);
        matrixStack.translate(-(x + 8), -(y + 8), 0);
        itemRenderer.renderAndDecorateFakeItem(itemStack, x, y);
        matrixStack.popPose();
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
    private  ItemStack handItem = mc.player.getMainHandItem();
    @Listener
    public void oner(EventRender2D event) {
        if(this.isEnabled()&& Natives.IsKeyDown(this.getKey())){
            YolBi.information("open");
                target = findtarget();
                if (target != null ) {

                    Thread_KillauraPacket.OpenThread(false);
                    YolBi.information(":");

                    float[] smoothedRotations;
                    if (jztargetrange(target) <= aimrange.getValue()) {
                        Vec3 PRe = PreMoveForTarget(target);
                        if(target.getDeltaMovement().x + target.getDeltaMovement().z + target.getDeltaMovement().y >5.0){
                            YolBi.information("PRE");
                            smoothedRotations = RotationUtils.getSimpleRotations2(PRe.add(0,1,0),target);
                        }else {
                            YolBi.information("NExtto");
                            Vec3 next = getClosestPointToPlayer(target);
                            smoothedRotations = RotationUtils.getSimpleRotations2(next.add(0,0,0),target);
                        }
                        smoothedRotations[1] -= 4.5f;
                        mc.player.setYRot(smoothedRotations[0]);
                        mc.player.setXRot(smoothedRotations[1]);
                        if(legit.getValue()&&packet.getValue()){
                            legit.setValue(false);
                        }
                        if(cpsValue.getValue() < ab.getValue()){
                            ab.setValue(cpsValue.getValue() -1);
                        }
                        Thread_KillauraPacket.setTarget(target,cpsValue.getValue(),ab.getValue());
                        Thread_KillauraPacket.OpenThread(true);
                    }
                }
        }else {
            Thread_KillauraPacket.OpenThread(false);
        }
        renderAndDecorateFakeItem(event.getPoseStack(),handItem,mc.screen.width,10,12f);
    }

    public final float xj(float a,float b){
        return Math.abs(a - b);
    }
    public static double jztargetrange(LivingEntity a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ()) + Math.abs(a.getY() - mc.player.getY());
        }
        return -1;
    }

    public static double unjztargetrange(LivingEntity a) {
        if (mc.player != null) {
            return Math.abs(a.getX() - mc.player.getX()) + Math.abs(a.getZ() - mc.player.getZ());
        }
        return -1;
    }
}
