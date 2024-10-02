package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.impl.combat.VanillaAura;
import cn.yapeteam.yolbi.utils.player.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import pisi.unitedmeows.minecraft.Settings;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Shadow
    private float prevEquippedProgress;
    @Shadow
    private float equippedProgress;
    @Shadow
    private Minecraft mc;
    @Shadow
    private ItemStack itemToRender;

    @Shadow
    private void rotateArroundXAndY(float angle, float angleY) {
    }

    @Shadow
    private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
    }

    @Shadow
    private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
    }

    @Shadow
    private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress) {
    }

    @Shadow
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
    }

    @Shadow
    private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress) {
    }

    @Shadow
    private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks) {
    }

    @Shadow
    private void doBlockTransformations() {
    }

    @Shadow
    private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer) {
    }

    @Shadow
    private void doItemUsedTransformations(float swingProgress) {
    }

    @Shadow
    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
    }

    @Overwrite(method = "renderItemInFirstPerson", desc = "(F)V")
    public void renderItemInFirstPerson(float partialTicks) {
        if (!(ReflectionManager.hasOptifine && (Config.isShaders() || Shaders.isSkipRenderHand()))) {
            float equipProgress = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
            AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
            float swingProgress = abstractclientplayer.getSwingProgress(partialTicks);
            float pitch = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
            float yaw = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
            this.rotateArroundXAndY(pitch, yaw);
            this.setLightMapFromPlayer(abstractclientplayer);
            this.rotateWithPlayerRotations((EntityPlayerSP) abstractclientplayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            if (this.itemToRender != null) {
                if (this.itemToRender.getItem() instanceof ItemMap) {
                    this.renderItemMap(abstractclientplayer, pitch, equipProgress, swingProgress);
                } else if (abstractclientplayer.getItemInUseCount() > 0 || (YolBi.instance.getModuleManager().getModule(VanillaAura.class) != null && YolBi.instance.getModuleManager().getModule(VanillaAura.class).isBlock && InventoryUtils.getHeldItem() instanceof ItemSword)) {
                    boolean oldAnimations = Settings.OLD_ANIMATIONS;
                    EnumAction enumaction = this.itemToRender.getItemUseAction();
                    switch (enumaction) {
                        case NONE:
                            this.transformFirstPersonItem(equipProgress, 0.0F);
                            break;
                        case EAT:
                        case DRINK:
                            this.performDrinking(abstractclientplayer, partialTicks);
                            this.transformFirstPersonItem(equipProgress, 0.0F);
                            break;
                        case BLOCK:
                            float oldAnimationProgress = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
                            this.transformFirstPersonItem(equipProgress, 0.0F);
                            this.doBlockTransformations();
                            if (oldAnimations) {
                                GlStateManager.translate(-0.05F, -0.0F, 0.3F);
                                GlStateManager.rotate(-oldAnimationProgress * 20.0F / 2.0F, -15.0F, -0.0F, 20.0F);
                                GlStateManager.rotate(-oldAnimationProgress * 40.0F, 1.0F, -0.4F, 2.0F);
                            }

                            GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                            if (this.mc.thePlayer.isSneaking()) {
                                GL11.glTranslatef(0.1F, -0.05F, -0.05F);
                            } else {
                                GL11.glTranslatef(0.1F, -0.05F, 0.1F);
                            }
                            break;
                        case BOW:
                            this.transformFirstPersonItem(equipProgress, 0.0F);
                            this.doBowTransformations(partialTicks, abstractclientplayer);
                    }
                } else {
                    this.doItemUsedTransformations(swingProgress);
                    this.transformFirstPersonItem(equipProgress, swingProgress);
                }

                this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            } else if (!abstractclientplayer.isInvisible()) {
                this.renderPlayerArm(abstractclientplayer, equipProgress, swingProgress);
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }
}
