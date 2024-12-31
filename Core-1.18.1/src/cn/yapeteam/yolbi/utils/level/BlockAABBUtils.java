package cn.yapeteam.yolbi.utils.level;

import cn.yapeteam.yolbi.YolBi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

public class BlockAABBUtils {
    public static Vec3 FindPosOnWallOfTheMiddle(BlockPos blockPos) {
        if(mc.player == null){
            return YolBi.EV3;
        }
        LivingEntity player = mc.player;
        Vec3 playerPos = player.position();
        BlockState blockState = mc.level.getBlockState(blockPos);
        AABB blockAABB = blockState.getShape(player.level, blockPos).bounds().move(blockPos);
        Vec3[] faceCenters = getFaceCenters(blockAABB);
        return findNearestFace(faceCenters, playerPos);
    }
    private static Vec3[] getFaceCenters(AABB blockAABB) {
        return new Vec3[]{
                new Vec3((blockAABB.minX + blockAABB.maxX) / 2 , blockAABB.minY + 0.4, (blockAABB.minZ + blockAABB.maxZ) / 2), // 下
                new Vec3((blockAABB.minX + blockAABB.maxX) / 2, blockAABB.maxY + 0.4, (blockAABB.minZ + blockAABB.maxZ) / 2), // 上
                new Vec3(blockAABB.minX, (blockAABB.minY + blockAABB.maxY) / 2 + 0.4, (blockAABB.minZ + blockAABB.maxZ) / 2), // 西
                new Vec3(blockAABB.maxX, (blockAABB.minY + blockAABB.maxY) / 2 + 0.4, (blockAABB.minZ + blockAABB.maxZ) / 2), // 东
                new Vec3((blockAABB.minX + blockAABB.maxX) / 2, (blockAABB.minY + blockAABB.maxY) / 2 + 0.4, blockAABB.minZ), // 北
                new Vec3((blockAABB.minX + blockAABB.maxX) / 2, (blockAABB.minY + blockAABB.maxY) / 2 + 0.4, blockAABB.maxZ)  // 南
        };
    }

    private static Vec3 findNearestFace(Vec3[] faceCenters, Vec3 playerPos) {
        Vec3 nearestFace = null;
        double minDistance = Double.MAX_VALUE;
        for (Vec3 faceCenter : faceCenters) {
            double distance = faceCenter.distanceTo(playerPos);
            if (distance < minDistance) {
                minDistance = distance;
                nearestFace = faceCenter;
            }
        }
        return nearestFace;
    }
}
