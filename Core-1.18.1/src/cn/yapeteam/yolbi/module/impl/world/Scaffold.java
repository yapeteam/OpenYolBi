package cn.yapeteam.yolbi.module.impl.world;


import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.Rotation;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", ModuleCategory.WORLD, InputConstants.KEY_G);
        addValues(sameY, searchRotXDiff, searchRotYDiff, rotationSpeed, rotationSpeed2, keepSprint, telly, rayCast, rayCastMode);
    }

    public BooleanValue sameY = new BooleanValue("SameY", false);
    public NumberValue<Double> searchRotYDiff = new NumberValue<Double>("SearchRotYDiff", 90d, 0d, 180d, 0.1);
    public NumberValue<Double> searchRotXDiff = new NumberValue<Double>("SearchRotXDiff", 1d, 0d, 5d, 0.1);

    public NumberValue<Double> rotationSpeed = new NumberValue<Double>("RotationSpeed", 90d, 0d, 180d, 0.1);
    public NumberValue<Double> rotationSpeed2 = new NumberValue<Double>("RotationSpeed2", 90d, 0d, 180d, 0.1);
    public BooleanValue keepSprint = new BooleanValue("KeepSprint", false);
    public BooleanValue telly = new BooleanValue("Telly(Don't use it,may not bypass)", false);
    public BooleanValue rayCast = new BooleanValue("Ray Cast", false);
    public ModeValue<String> rayCastMode = new ModeValue<>("RayCastMode", "Strict", "Strict", "Static");
    public PlaceInfo placeInfo;
    public Vector2f targetRotation = new Vector2f(0, 0);
    public Vector2f currentRotation = new Vector2f(0, 0);

    public static boolean isScaffoldBlock(ItemStack itemStack) {
        if (itemStack == null)
            return false;

        if (itemStack.getCount() <= 0)
            return false;

        if (!(itemStack.getItem() instanceof BlockItem))
            return false;

        BlockItem itemBlock = (BlockItem) itemStack.getItem();

        // whitelist
        if (itemBlock.getBlock() == Blocks.GLASS)
            return true;

        // only fullblock
        return true;
    }

    public Direction getSideHit(BlockPos currentPos, BlockPos sideBlock) {
        int xDiff = sideBlock.getX() - currentPos.getX();
        int yDiff = sideBlock.getY() - currentPos.getY();
        int zDiff = sideBlock.getZ() - currentPos.getZ();
        if (sameY.getValue()) yDiff = 0;
        Direction facing = yDiff <= -0.5 ? Direction.UP : xDiff <= -1 ? Direction.EAST : xDiff >= 1 ? Direction.WEST : zDiff <= -1 ? Direction.SOUTH : zDiff >= 1 ? Direction.NORTH : yDiff >= 0.5 ? Direction.DOWN : null;
        if (xDiff == 0 && yDiff == 0 && zDiff == 0) {
            return null;
        }
        return facing;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (!keepSprint.getValue()) mc.player.setSprinting(false);

        placeInfo = finnPlaceInfo();

        double speed = rotationSpeed.getValue() + (rotationSpeed2.getValue() - rotationSpeed.getValue()) * Math.random();

        if (placeInfo != null) {
            targetRotation = placeInfo.rotation;
        }
        if (telly.getValue() && !mc.player.isOnGround())
            targetRotation = new Vector2f(mc.player.getXRot(), mc.player.getYRot());
        currentRotation = Rotation.limitAngleChange(new Rotation(currentRotation), new Rotation(targetRotation), (float) speed).toVec2f();
        YolBi.instance.getRotationManager().setRation(currentRotation);


        int currentItem = getSlot();
        mc.player.getInventory().selected = currentItem;
        HitResult result = ray(currentRotation, 5);
        if (placeInfo == null || rayCast.getValue() && !(result instanceof BlockHitResult) || rayCast.getValue() && result instanceof BlockHitResult && !((BlockHitResult) result).getBlockPos().equals(placeInfo.hitResult.getBlockPos()) || rayCast.getValue() && rayCastMode.is("Strict")
                && result instanceof BlockHitResult && ((BlockHitResult) result).getDirection() != placeInfo.hitResult.getDirection())
            return;
        if (placeInfo != null && isScaffoldBlock(mc.player.getInventory().getSelected())) {
            InteractionResult interactionResult = mc.gameMode.useItemOn(mc.player, mc.level, InteractionHand.MAIN_HAND, placeInfo.hitResult);
            if (interactionResult.shouldSwing()) {
                mc.player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) startY = (int) (Math.floor(mc.player.getY()) + 1);
        //FunGhostClient.registerManager.vModuleManager.notification.post(new Notification("Plz Put Blocks In MainHand", Notification.Type.WHITE));
    }

    public boolean cannotPut(BlockPos blockPos) {
        if (blockPos == null) {
            return false;
        }
        return getBlock(blockPos) != Blocks.AIR && !(getBlock(blockPos) instanceof LiquidBlock);
    }

    public int startY = 0;

    public PlaceInfo finnPlaceInfo() {
        BlockPos player_pos = getPos(mc.player.getPosition(1));
        BlockPos hitPos = null;
        double offset = 0.4;
        Direction facing = getSideHit(player_pos.offset(0, sameY.getValue() ?
                startY - player_pos.getY() : -offset, 0), getSideBlock(player_pos.offset(0, sameY.getValue() ? startY - player_pos.getY() :
                -offset, 0)));

        hitPos = getSideBlock(player_pos.offset(0, sameY.getValue() ? startY - player_pos.getY() : -offset, 0));
        if (hitPos == null || facing == null) return null;
        float yaw = mc.player.getYRot();
        ArrayList<PlaceInfo> placeInfos = new ArrayList<>();
        for (int possibleYaw = (int) ((int) (yaw - 180)); possibleYaw < yaw + 180; possibleYaw += (Double) searchRotYDiff.getValue()) {
            for (float possiblePitch = 90; possiblePitch > 60; possiblePitch -= (Double) searchRotXDiff.getValue()) {
                Vector2f rotation = new Vector2f(possiblePitch, possibleYaw);
                HitResult result = ray(rotation, 5);
                if (result instanceof BlockHitResult) {
                    if (((BlockHitResult) result).getDirection() == facing && ((BlockHitResult) result).getBlockPos().equals(hitPos)) {
                        placeInfos.add(new PlaceInfo(((BlockHitResult) result), rotation));
                    }
                }
            }
        }

        float minDiff = Float.MAX_VALUE;
        PlaceInfo current = null;
        for (PlaceInfo info : placeInfos) {
            float diff = (float) Math.hypot(Rotation.getAngleDifference(info.rotation.x, YolBi.instance.getRotationManager().getRation().x),
                    Rotation.getAngleDifference(info.rotation.y, YolBi.instance.getRotationManager().getRation().y));

            if (diff < minDiff) {
                minDiff = diff;
                current = info;
            }
        }
        return current;
    }

    public HitResult ray(Vector2f v, double blockReachDistance) {
        Vec3 v31 = calculateViewVector(v.x, v.y);
        Vec3 v1 = mc.player.getEyePosition();
        Vec3 v2 = v1.add(v31.x * blockReachDistance, v31.y * blockReachDistance, v31.z * blockReachDistance);

        return mc.level.clip(new ClipContext(v1, v2, net.minecraft.world.level.ClipContext.Block.OUTLINE, net.minecraft.world.level.ClipContext.Fluid.NONE, mc.player));
    }

    public Vec3 calculateViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * 0.017453292F;
        float f1 = -p_20173_ * 0.017453292F;
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
    }

    public BlockPos getPos(Vec3 v) {
        return new BlockPos(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
    }

    public BlockPos getSideBlock(BlockPos currentPos) {
        BlockPos pos = currentPos;
        if (getBlock(currentPos.offset(0, -1, 0)) != Blocks.AIR && !(getBlock(currentPos.offset(0, -1, 0)) instanceof LiquidBlock))
            return currentPos.offset(0, -1, 0);
        double reach_max = 5;
        double reach_min = 0;
        int floor_reach = (int) (reach_max + 0.5);
        double dist = 20;
        for (int x = -floor_reach; x <= floor_reach; x++) {
            for (int y = -floor_reach; y <= floor_reach; y++) {
                for (int z = -floor_reach; z <= floor_reach; z++) {
                    BlockPos newPos = currentPos.offset(x, y, z);
                    double newDist = Math.sqrt(x * x + y * y + z * z);
                    if (getBlock(newPos) != Blocks.AIR && !(getBlock(newPos) instanceof LiquidBlock)
                            && mc.level.getBlockState(newPos).getMaterial().isSolid() && newDist <= dist && newDist < reach_max && newDist > reach_min) {
                        pos = currentPos.offset(x, y, z);
                        dist = newDist;
                    }
                }
            }
        }
        return pos;
    }

    public Block getBlock(BlockPos pos) {
        //MCP
        return mc.level.getBlockState(pos).getBlock();
    }

    public static class PlaceInfo {
        public BlockHitResult hitResult;

        public Vector2f rotation;

        public PlaceInfo(BlockHitResult result, Vector2f rotation) {
            this.hitResult = result;
            this.rotation = rotation;
        }
    }

    public int getSlot() {
        for (int i = 0; i <= 8; i++) {
            if (isScaffoldBlock(mc.player.getInventory().items.get(i))) {
                return i;
            }
        }
        return mc.player.getInventory().selected;
    }
}
