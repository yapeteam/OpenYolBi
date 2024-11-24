package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.utils.render.ColorUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ESP extends Module {
    public ESP() {
        super("ESP",ModuleCategory.VISUAL, InputConstants.KEY_L);
    }
    public MobEffect glowingEffect = MobEffects.GLOWING;
    public ModuleManager mm = YolBi.instance.getModuleManager();
    public MobEffectInstance glowingEffectInstance = new MobEffectInstance(glowingEffect, 1000000, 0, false, false);
    @Listener
    public void onRender2D(EventRender2D event) {
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity && entity!=mc.player) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.addEffect(glowingEffectInstance);
                mm.font.drawStringWithShadow(event.getPoseStack(),"Glow",mc.screen.width/2,mc.screen.height/2, ColorUtils.rainbow(10,1).getRGB());
            }
        }
    }

}