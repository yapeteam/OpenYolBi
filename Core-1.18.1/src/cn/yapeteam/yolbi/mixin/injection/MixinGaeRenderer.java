package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class MixinGaeRenderer {
    @Inject(
            method = "render",
            desc = "(FJZ)V",
            target = @Target(
                    value = "INVOKEVIRTUAL",
                    target = "net/minecraft/client/gui/Gui.render(Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
                    shift = Target.Shift.AFTER
            )
    )
    private void onRender2D(@Local(source = "poseStack", index = 10) PoseStack poseStack) {
        YolBi.instance.getEventManager().post(new EventRender2D(poseStack));
    }

    @Inject(
            method = "renderLevel",
            desc = "(FJLcom/mojang/blaze3d/vertex/PoseStack;)V",
            target = @Target(
                    value = "INVOKEVIRTUAL",
                    target = "net/minecraft/client/renderer/LevelRenderer.renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
                    shift = Target.Shift.AFTER
            )
    )
    private void onRender3D(
            @Local(source = "poseStack", index = 7) PoseStack poseStack,
            @Local(source = "partialTicks", index = 1) float partialTicks
    ) {
        YolBi.instance.getEventManager().post(new EventRender3D(poseStack, partialTicks));
    }
}
