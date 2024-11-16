package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.EntityUtils;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import cn.yapeteam.yolbi.utils.render.RenderUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ESP extends Module {


    public ESP() {
        super("ESP",ModuleCategory.VISUAL, InputConstants.KEY_L);
    }
    @Listener
    public void onRender2D(EventRender2D event) {
        if (mc.player != null && mc.level != null) {
            PoseStack poseStack = event.poseStack();
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (EntityUtils.isSelected(entity, true, true, true, false, true, true)) {
                        RenderUtils.renderEntityBoundingBox(poseStack, 0, livingEntity, ColorUtils.rainbow(10, 1).getRGB(), true);
                    }
                }
            }
        }
    }

}