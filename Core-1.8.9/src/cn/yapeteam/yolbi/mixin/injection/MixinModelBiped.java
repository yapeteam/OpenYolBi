package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.managers.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@Mixin(ModelBiped.class)
public class MixinModelBiped {
    @Shadow
    public ModelRenderer bipedHead;

    @Inject(
            method = "setRotationAngles", desc = "(FFFFFFLnet/minecraft/entity/Entity;)V",
            target = @Target(
                    value = "INVOKESTATIC",
                    target = "net/minecraft/util/MathHelper.cos(F)F",
                    shift = Target.Shift.BEFORE
            )
    )
    public void setRotationAngles(@Local(source = "entityIn", index = 7) Entity entityIn) {
        if (entityIn == Minecraft.getMinecraft().thePlayer) {
            RotationManager rotationManager = YolBi.instance.getRotationManager();
            bipedHead.rotateAngleX = (rotationManager.prevRenderPitchHead + (rotationManager.renderPitchHead - rotationManager.prevRenderPitchHead) * 1) / (180.0F / (float) Math.PI);
        }
    }
}
