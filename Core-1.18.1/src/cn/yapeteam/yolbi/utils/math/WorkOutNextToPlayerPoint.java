package cn.yapeteam.yolbi.utils.math;

public class WorkOutNextToPlayerPoint {

    /**
     * 获取目标实体的碰撞箱离玩家最近的点
     *
     * @param playerPosition 玩家的位置
     * @param boundingBox    目标实体的碰撞箱
     * @return               离玩家最近的点
     */
    public static Vec3d getClosestPointToPlayer(Vec3d playerPosition, AxisAlignedBB boundingBox) {
        // 获取碰撞箱的最小和最大坐标
        double xMin = boundingBox.minX;
        double yMin = boundingBox.minY;
        double zMin = boundingBox.minZ;
        double xMax = boundingBox.maxX;
        double yMax = boundingBox.maxY;
        double zMax = boundingBox.maxZ;

        // 计算玩家位置在每个轴上的最近点
        double closestX = clamp(playerPosition.x, xMin, xMax);
        double closestY = clamp(playerPosition.y, yMin, yMax);
        double closestZ = clamp(playerPosition.z, zMin, zMax);

        // 返回最近的点
        return new Vec3d(closestX, closestY, closestZ);
    }

    /**
     * 将一个值限制在给定的最小值和最大值之间
     *
     * @param value  要限制的值
     * @param min    最小值
     * @param max    最大值
     * @return       限制后的值
     */
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // 一个简单的3D坐标类（Vec3d），用来表示玩家或碰撞箱的点
    public static class Vec3d {
        public final double x, y, z;

        public Vec3d(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "Vec3d(" + x + ", " + y + ", " + z + ")";
        }
    }

    // 一个简单的3D矩形碰撞框类（AxisAlignedBB）
    public static class AxisAlignedBB {
        public final double minX, minY, minZ, maxX, maxY, maxZ;

        public AxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }
    }
}
