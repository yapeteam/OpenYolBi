package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
public class ESP extends Module {
    public ESP() {
        super("ESP",ModuleCategory.VISUAL, InputConstants.KEY_L);
    }
    @Listener
    public void onRender2D(EventRender2D event) {
            renderPlayerOnScreen(event.poseStack());
    }
    private void renderPlayerOnScreen(PoseStack poseStack) {
        if (mc.player == null) return;
        double playerX = mc.player.getX();
        double playerY = mc.player.getY();
        double playerZ = mc.player.getZ();
        int screenWidth = mc.getWindow().getWidth();
        int screenHeight = mc.getWindow().getHeight();
        double scaleX = screenWidth / 2;
        double scaleY = screenHeight / 2;
        double screenPosX = scaleX + (playerX - mc.player.getX()) * scaleX;
        double screenPosY = scaleY - (playerY - mc.player.getY()) * scaleY;
        // 设置颜色和透明度
        RenderSystem.setShaderColor(ColorUtils.rainbow(10,1).getRed(),ColorUtils.rainbow(10,1).getGreen(),ColorUtils.rainbow(10,1).getBlue(),ColorUtils.rainbow(10,1).getAlpha());
        drawPlayerShape(poseStack, screenPosX, screenPosY);
    }
    private void drawPlayerShape(PoseStack poseStack, double screenPosX, double screenPosY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int radius = 10;
        GuiComponent.fill(poseStack, (int) (screenPosX - radius), (int) (screenPosY - radius),
                (int) (screenPosX + radius), (int) (screenPosY + radius),ColorUtils.rainbow(10,1).getRGB());
    }

}