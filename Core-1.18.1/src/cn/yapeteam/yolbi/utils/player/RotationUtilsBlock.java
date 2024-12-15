package cn.yapeteam.yolbi.utils.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


public class RotationUtilsBlock implements Wrapper {
   public static double getDistanceToEntityBox(Entity target) {
      Vec3 eyes = mc.player.getEyePosition(1.0F);
      Vec3 pos = getNearestPointBB(eyes, target.getBoundingBox());
      double xDist = Math.abs(pos.x - eyes.x);
      double yDist = Math.abs(pos.y - eyes.y);
      double zDist = Math.abs(pos.z - eyes.z);
      return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
   }

   private static Vec3 getNearestPointBB(Vec3 point, AABB box) {
      double x = Math.max(box.minX, Math.min(point.x, box.maxX));
      double y = Math.max(box.minY, Math.min(point.y, box.maxY));
      double z = Math.max(box.minZ, Math.min(point.z, box.maxZ));
      return new Vec3(x, y, z);
   }

   public static float[] getRotationFromEyeToPoint(Vector3d point3d) {
      return getRotation(new Vector3d(mc.player.getX(), mc.player.getBoundingBox().minY + (double)mc.player.getEyeHeight(), mc.player.getZ()), point3d);
   }

   public static float[] getRotation(Vector3d from, Vector3d to) {
      double x = to.getX() - from.getX();
      double y = to.getY() - from.getY();
      double z = to.getZ() - from.getZ();
      double sqrt = Math.sqrt(x * x + z * z);
      float yaw = (float)Math.toDegrees(Math.atan2(z, x)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(y, sqrt)));
      return new float[]{yaw, Math.min(Math.max(pitch, -90.0F), 90.0F)};
   }

   public static float[] getSimpleRotations(double x,double y,double z) {
      double yDist = y - mc.player.getY();
      Vector3d targetPos;
      if (yDist >= 1.547) {
         targetPos = new Vector3d(x, y, z);
      } else if (yDist <= -1.547) {
         targetPos = new Vector3d(x, y , z);
      } else {
         targetPos = new Vector3d(x, y, z);
      }

      return getRotationFromEyeToPoint(targetPos);
   }
}