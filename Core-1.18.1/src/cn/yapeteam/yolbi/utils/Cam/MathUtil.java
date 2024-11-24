package cn.yapeteam.yolbi.utils.Cam;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

public class MathUtil {
    public static Vec3 worldToScreen(Vec3 worldPos) {
        if (mc.player == null) {
            return null;
        }
        LivingEntity player = mc.player;
        Vec3 eyePos = mc.player.getEyePosition();
        float yaw = MathHelper.wrapDegrees(player.getYRot());
        float pitch = MathHelper.wrapDegrees(player.getXRot());
        Vec3 relativePos = worldPos.subtract(eyePos);
        return getScreenCoordinates(relativePos, yaw, pitch);
    }
    public void setGlowing(LivingEntity e){
        MobEffect glowingEffect = MobEffects.GLOWING;
        MobEffectInstance glowingEffectInstance = new MobEffectInstance(glowingEffect, 1000000, 0, false, false);
        e.addEffect(glowingEffectInstance);
    }
    private static Vec3 getScreenCoordinates(Vec3 relativePos, float yaw, float pitch) {
        double cosYaw = Math.cos(Math.toRadians(yaw));
        double sinYaw = Math.sin(Math.toRadians(yaw));
        double cosPitch = Math.cos(Math.toRadians(pitch));
        double sinPitch = Math.sin(Math.toRadians(pitch));
        double x = relativePos.x * cosYaw + relativePos.z * sinYaw;
        double y = relativePos.y * cosPitch - relativePos.z * sinPitch;
        if (y == 0) {
            return null;
        }
        double screenX = (x / y) * 1000;
        double screenY = (y / y) * 1000;
        return new Vec3(screenX, screenY, 0);
    }
}
