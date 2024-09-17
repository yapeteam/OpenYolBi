package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.combat.KillAura;
import cn.yapeteam.yolbi.utils.animation.Animation;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.render.GradientBlur;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class TargetHud extends Module {
    public TargetHud() {
        super("TargetHUD", ModuleCategory.VISUAL);
    }

    @Listener
    public void onRender2D(EventRender2D event) {
        float x = event.getScaledresolution().getScaledWidth() / 2f;
        float y = event.getScaledresolution().getScaledHeight() / 2f;
        KillAura ka = YolBi.instance.getModuleManager().getModule(KillAura.class);
        EntityLivingBase entityLiving = ka.getTarget();
        if (entityLiving == null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase)
            entityLiving = (EntityLivingBase) mc.objectMouseOver.entityHit;
        this.render(x, y, entityLiving, event.getPartialTicks());
    }

    private final Animation animate = new Animation(Easing.EASE_OUT_QUINT, 500);
    private final Animation animateAlpha = new Animation(Easing.EASE_OUT_QUINT, 600);
    private final GradientBlur blur = new GradientBlur(GradientBlur.Type.LR);

    private EntityLivingBase lastTarget = null;

    public void render(float x, float y, EntityLivingBase target, float partialTicks) {
        AbstractFontRenderer font = YolBi.instance.getFontManager().getPingFang18();
        float alpha = (float) animateAlpha.animate(target == null ? 0 : 1);
        if (target != null) lastTarget = target;
        if (lastTarget == null || alpha == 0) return;
        String text = lastTarget.getDisplayName().getFormattedText();
        float height = 40;
        float width = (float) ((lastTarget instanceof AbstractClientPlayer ? height + 5 : 0) + 5 + font.getStringWidth(text) + 5);
        width = Math.max(120, width);
        blur.update(x, y, width, height);
        float animatedHealthBar = (float) animate.animate(lastTarget.getHealth());
        RenderUtil.drawBloomShadow(x, y, width, height, 8, 6, new Color(0, 0, 0, alpha).darker().getRGB(), false);
        blur.render(x, y, width, height, partialTicks, alpha);
        RenderUtil.drawRect2(x, y, Math.min(width * animatedHealthBar / lastTarget.getMaxHealth(), width), height, new Color(0, 0, 0, 80 / 255f * alpha).getRGB());
        if (lastTarget instanceof AbstractClientPlayer) {
            float headSize = height - 5 * 2;
            drawBigHead(x + (height - headSize) / 2f, y + (height - headSize) / 2f, headSize, headSize, alpha, (AbstractClientPlayer) lastTarget);
            font.drawString(text, x + height + 5, y + (height - font.getStringHeight("A")) / 2f, new Color(1, 1, 1, alpha));
        } else {
            font.drawString(text, x + 5, y + (height - font.getStringHeight("A")) / 2f, new Color(1, 1, 1, alpha));
        }
    }

    protected void drawBigHead(float x, float y, float width, float height, float alpha, AbstractClientPlayer player) {
        double offset = -(player.hurtTime * 23);
        RenderUtil.glColor(new Color(255, (int) (255 + offset), (int) (255 + offset), (int) (alpha * 255)).getRGB());
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        RenderUtil.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }
}
