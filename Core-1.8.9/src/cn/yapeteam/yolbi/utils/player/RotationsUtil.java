package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;

import java.util.List;
import java.util.Objects;

public class RotationsUtil implements IMinecraft {

    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - mc.thePlayer.posX;
        double deltaY = y - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double deltaZ = z - mc.thePlayer.posZ;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));

        return new float[]{yaw, pitch};
    }

    public static final Timer timer = ReflectionManager.Minecraft$getTimer(mc);

    public static float[] getRotationsToEntity(EntityLivingBase entity, boolean usePartialTicks) {
        if (timer == null) return new float[]{0, 0};
        float partialTicks = timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks : entity.posX;
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks : entity.posY;
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks : entity.posZ;

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(entityX, finalEntityY, entityZ);
    }

    public static float[] getRotationsToBlockPos(BlockPos pos) {
        return getRotationsToPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static float getGCD() {
        return (float) (Math.pow(mc.gameSettings.mouseSensitivity * 0.6 + 0.2, 3) * 1.2);
    }

    public static double getRotationDifference(Vector2f curRotations, Vector2f targetRotations) {
        if (curRotations == null || targetRotations == null)
            return 0.0;

        return Math.hypot(getAngleDifference(curRotations.x, targetRotations.x), (curRotations.y - targetRotations.y));
    }

    public static double getRotationDifference(Vector2f rotation) {
        return YolBi.instance.getRotationManager().targetRotations == null ? 0.0 : getRotationDifference(rotation, YolBi.instance.getRotationManager().targetRotations);
    }

    public static float getAngleDifference(float f1, float f2) {
        return ((f1 - f2) % 360f + 540f) % 360f - 180f;
    }


    public static float[] getRotationsToPosition(double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(-Math.atan2(dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, horizontalDistance));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsToEntity(double x, double y, double z, EntityLivingBase entity, boolean usePartialTicks) {
        float partialTicks = Objects.requireNonNull(ReflectionManager.Minecraft$getTimer(mc)).renderPartialTicks;
        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks : entity.posX;
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks : entity.posY;
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks : entity.posZ;
        double yDiff = mc.thePlayer.posY - entityY;
        double finalEntityY = yDiff >= 0.0 ? entityY + (double) entity.getEyeHeight() : (-yDiff < (double) mc.thePlayer.getEyeHeight() ? y + (double) mc.thePlayer.getEyeHeight() : entityY);
        return getRotationsToPosition(x, y + (double) mc.thePlayer.getEyeHeight(), z, entityX, finalEntityY, entityZ);
    }

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static boolean isMouseOver(final float yaw, final float pitch, final Entity target, final float range) {
        final float partialTicks = Objects.requireNonNull(ReflectionManager.Minecraft$getTimer(mc)).renderPartialTicks;
        final Entity entity = mc.getRenderViewEntity();
        MovingObjectPosition objectMouseOver;
        Entity mcPointedEntity = null;

        if (entity != null && mc.theWorld != null) {

            mc.mcProfiler.startSection("pick");
            final double d0 = mc.playerController.getBlockReachDistance();
            objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d1 = d0;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);
            final boolean flag = d0 > (double) range;

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3 vec31 = getVectorForRotation(pitch, yaw);
            final Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > (double) range) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mcPointedEntity = pointedEntity;
                }
            }

            mc.mcProfiler.endSection();

            return mcPointedEntity == target;
        }

        return false;
    }
}