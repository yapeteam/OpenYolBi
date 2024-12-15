package cn.yapeteam.yolbi.utils.Cam;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class RenderBox {
    public static void drawTransparentRectangle(PoseStack poseStack,int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(poseStack,x1, y1, x2, y2, color);
    }
}
