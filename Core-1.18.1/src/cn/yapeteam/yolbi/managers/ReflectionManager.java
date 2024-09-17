package cn.yapeteam.yolbi.managers;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.ymixin.utils.Mapper;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class ReflectionManager {
    private static Field Minecraft$instance, NativeImage$pixels;

    static {
        try {
            Minecraft$instance = Minecraft.class.getDeclaredField(Mapper.map("net/minecraft/client/Minecraft", "instance", null, Mapper.Type.Field));
            Minecraft$instance.setAccessible(true);
        } catch (Throwable throwable) {
            Logger.exception(throwable);
        }
        try {
            NativeImage$pixels = NativeImage.class.getDeclaredField(Mapper.map("com.mojang.blaze3d.platform.NativeImage", "pixels", null, Mapper.Type.Field));
            NativeImage$pixels.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
    }

    public static Minecraft Minecraft$getInstance() {
        try {
            return (Minecraft) Minecraft$instance.get(null);
        } catch (IllegalAccessException e) {
            Logger.exception(e);
            return null;
        }
    }

    public static long NativeImage$pixels(NativeImage obj) {
        try {
            return (long) NativeImage$pixels.get(obj);
        } catch (IllegalAccessException e) {
            Logger.exception(e);
        }
        return 0;
    }
}
