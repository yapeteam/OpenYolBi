package cn.yapeteam.yolbi.utils.Cam;

import cn.yapeteam.loader.ResourceManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Map;


public class RenderSkin {

    public static ResourceLocation getPlayerSkin(GameProfile profile) {
        Minecraft mc = Minecraft.getInstance();
        TextureManager textureManager = mc.getTextureManager();

        // 从 Mojang 服务获取玩家的皮肤
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures =
                mc.getSkinManager().getInsecureSkinInformation(profile);

        if (textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            MinecraftProfileTexture skin = textures.get(MinecraftProfileTexture.Type.SKIN);
            return mc.getSkinManager().registerTexture(skin, MinecraftProfileTexture.Type.SKIN);
        }

        // 如果玩家没有皮肤，使用默认皮肤
        return new ResourceLocation(Arrays.toString(ResourceManager.resources.get("imgs/steve.png")));
    }

    /**
     * 绘制头像
     */
    public static void renderAvatar(PoseStack ps, ResourceLocation skinTexture, int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getInstance();

        // 绑定皮肤纹理
        mc.getTextureManager().bindForSetup(skinTexture);

        // 使用 Gui 类绘制纹理到屏幕
        Gui.blit(ps,x, y, width, height, 8, 8, 8, 8, 64, 64);
    }
}
