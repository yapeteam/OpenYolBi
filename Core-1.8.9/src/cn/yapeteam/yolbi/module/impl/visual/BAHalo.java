package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.animation.Animation;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author <a href="https://github.com/RinoRika">stars</a>
 * @link <a href="https://github.com/RinoRika/StarX/blob/master/src/minecraft/cn/stars/starx/module/impl/render/BAHalo.java">source</a>
 * @since StarX Client *  - A PVP Client with hack visual.
 * Copyright 2024 Starlight, All rights reserved.
 */
public class BAHalo extends Module {
    public BAHalo() {
        super("BAHalo", ModuleCategory.VISUAL);
        addValues(mode, showInFirstPerson);
    }

    private final ModeValue<String> mode = new ModeValue<>("Student", "Shiroko", "Shiroko", "Hoshino", "Aris", "Yuuka", "Natsu", "Reisa", "Shiroko*Terror");
    private final BooleanValue showInFirstPerson = new BooleanValue("First Person", true);
    Animation animation = new Animation(Easing.LINEAR, 2000);
    boolean isReversing = false;

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    @Listener
    public void onRender3D(EventRender3D event) {
        if (mc.gameSettings.thirdPersonView == 0 && !showInFirstPerson.getValue()) return;
        //if (mc.gameSettings.thirdPersonView != 0 && ModuleInstance.getModule(SmallPlayer.class).isEnabled()) {
        //    GlStateManager.scale(0.5f, 0.5f, 0.5f);
        //}
        switch (mode.getValue()) {
            case "Shiroko": {
                drawShirokoHalo();
                break;
            }
            case "Hoshino": {
                drawHoshinoHalo();
                break;
            }
            case "Aris": {
                drawArisHalo();
                break;
            }
            case "Yuuka": {
                drawYuukaHalo();
                break;
            }
            case "Natsu": {
                drawNatsuHalo();
                break;
            }
            case "Reisa": {
                drawReisaHalo();
                break;
            }
            case "Shiroko*Terror": {
                drawShiroko_TerrorHalo();
                break;
            }
        }
    }

    public void drawShirokoHalo() {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.thePlayer.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        Timer timer = ReflectionManager.Minecraft$getTimer(mc);
        if (timer == null) return;
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosX, mc.getRenderManager()),
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosY, mc.getRenderManager()) + height,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosZ, mc.getRenderManager())
        );
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(
                new Color(MathHelper.clamp_int((int) (animation.getValue() * 1800), 0, 255),
                        MathHelper.clamp_int((int) (230 + animation.getValue() * 200), 0, 255), 250, 220)
        );

        float yaw = mc.thePlayer.rotationYaw;

        // 使光环中心随玩家朝向旋转
        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        // 绘制内圈
        GL11.glLineWidth(2.5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.18f,
                    (float) Math.sin(Math.toRadians(i)) * 0.18f
            );
        }
        GL11.glEnd();

        // 绘制外圈
        GL11.glTranslated(0.0f, 0.0f, -0.02f);
        GL11.glLineWidth(3.7f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.3f;
            float y = (float) Math.sin(angle) * 0.3f;

            GL11.glVertex2f(x, y);

            // 在每个四等分点上添加突出效果
            if (i % 90 == 0) { // 四等分点
                float offset = 0.1f;
                float inwardOffset = 0.03f;

                // 向外突出顶点
                GL11.glVertex2f(x + (float) Math.cos(angle) * offset, y + (float) Math.sin(angle) * offset);
                // 回到原始顶点
                GL11.glVertex2f(x, y);
                // 向内突出顶点
                GL11.glVertex2f(x - (float) Math.cos(angle) * inwardOffset, y - (float) Math.sin(angle) * inwardOffset);
                // 回到原始顶点
                GL11.glVertex2f(x, y);
            }
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();

        GL11.glPopMatrix();
    }

    public void drawShiroko_TerrorHalo() {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.thePlayer.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        Timer timer = ReflectionManager.Minecraft$getTimer(mc);
        if (timer == null) return;
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosX, mc.getRenderManager()),
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosY, mc.getRenderManager()) + height,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosZ, mc.getRenderManager())
        );
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(79, 112, 117, 255));

        float yaw = mc.thePlayer.rotationYaw;

        // 使光环中心随玩家朝向旋转
        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        // 绘制内圈
        GL11.glLineWidth(2.2f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.2f,
                    (float) Math.sin(Math.toRadians(i)) * 0.2f
            );
        }
        GL11.glEnd();

        // 绘制外圈
        GL11.glTranslated(0.0f, 0.0f, -0.02f);
        GL11.glLineWidth(5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.3f;
            float y = (float) Math.sin(angle) * 0.3f;

            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        for (int i = 0; i < 360; i += 90) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.3f;
            float y = (float) Math.sin(angle) * 0.3f;

            // 在每个四等分点上添加突出效果
            if (i % 90 == 0) { // 四等分点
                float offset = 0.1f;
                float triangleX = x + (float) Math.cos(angle) * offset;
                float triangleY = y + (float) Math.sin(angle) * offset;

                // 计算旋转角度，使底边朝向圆心
                float rotationAngle = i - 90; // 使底边朝向圆心

                GL11.glLineWidth(5f * getExtraWidth());
                drawTriangle(triangleX / 1.35f, triangleY / 1.35f, 0.012f, 0.1f, rotationAngle);
            }
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();

        GL11.glPopMatrix();
    }

    public void drawReisaHalo() {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.thePlayer.height + 0.25f + (float) animation.getValue();
        GL11.glPushMatrix();
        Timer timer = ReflectionManager.Minecraft$getTimer(mc);
        if (timer == null) return;
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosX, mc.getRenderManager()),
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosY, mc.getRenderManager()) + height,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosZ, mc.getRenderManager())
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float yaw = mc.thePlayer.rotationYaw;

// 使光环中心随玩家朝向旋转
        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        RenderUtil.color(new Color(200, 200, 250, 220));

// 绘制大五角星
        GL11.glLineWidth(3.0f * getExtraWidth());
        drawStar(0.0f, 0.0f, 0.3f); // 半径为0.3的五角星

// 绘制小五角星
        GL11.glPushMatrix();
        GL11.glRotatef(36, 0F, 0F, 1F); // 旋转角度，使小五角星的尖端对准外部五角星的凹陷部分
        drawStar(0.0f, 0.0f, 0.14f); // 半径为0.15的五角星
        GL11.glPopMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();

    }

    public void drawNatsuHalo() {

        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.thePlayer.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        Timer timer = ReflectionManager.Minecraft$getTimer(mc);
        if (timer == null) return;
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosX, mc.getRenderManager()),
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosY, mc.getRenderManager()) + height,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosZ, mc.getRenderManager())
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(254, 200, 200, 240));

        float yaw = mc.thePlayer.rotationYaw;

        // 使光环中心随玩家朝向旋转
        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        // 绘制外圈
        GL11.glLineWidth(3.5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.3f,
                    (float) Math.sin(Math.toRadians(i)) * 0.3f
            );
        }
        GL11.glEnd();

        // 绘制内圈
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.15f;
            float y = (float) Math.sin(angle) * 0.15f;

            // 绘制外圈的线条
            GL11.glVertex2f(x, y);

            // 在每个四等分点上添加突出效果
            if (i % 90 == 0) {
                float offset = 0.05f;
                float inwardOffset = 0.05f;

                // 向外突出顶点
                GL11.glVertex2f(x + (float) Math.cos(angle) * offset, y + (float) Math.sin(angle) * offset);
                // 回到原始顶点
                GL11.glVertex2f(x, y);
                // 向内突出顶点
                GL11.glVertex2f(x - (float) Math.cos(angle) * inwardOffset, y - (float) Math.sin(angle) * inwardOffset);
                // 回到原始顶点
                GL11.glVertex2f(x, y);
            }
        }
        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public void drawYuukaHalo() {

        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.thePlayer.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        Timer timer = ReflectionManager.Minecraft$getTimer(mc);
        if (timer == null) return;
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosX, mc.getRenderManager()),
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosY, mc.getRenderManager()) + height,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosZ, mc.getRenderManager())
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float yaw = mc.thePlayer.rotationYaw;

        // 使光环中心随玩家朝向旋转
        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        // 绘制圆1
        RenderUtil.color(new Color(80, 150, 180, 250));
        GL11.glLineWidth(2f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.292f,
                    (float) Math.sin(Math.toRadians(i)) * 0.292f
            );
        }
        GL11.glEnd();

        // 绘制圆2
        RenderUtil.color(new Color(30, 30, 30, 200));
        GL11.glLineWidth(6f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.3f,
                    (float) Math.sin(Math.toRadians(i)) * 0.3f
            );
        }
        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public void drawHoshinoHalo() {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.thePlayer.height + 0.25f + (float) animation.getValue();
        float extraHeight = 0.035f;
        float extensionLength = 0.18f;
        float smallExtensionLength = 0.08f;

        GL11.glPushMatrix();
        Timer timer = ReflectionManager.Minecraft$getTimer(mc);
        if (timer == null) return;
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosX, mc.getRenderManager()),
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosY, mc.getRenderManager()) + height,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosZ, mc.getRenderManager())
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(237, MathHelper.clamp_int(110 + (int) (animation.getValue() * 600), 0, 255), 183, 220)); // RGB for pink

        float yaw = mc.thePlayer.rotationYaw;

        // 使光环中心随玩家朝向旋转
        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        // 绘制内圈
        GL11.glLineWidth(4.0f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            GL11.glVertex2f(
                    (float) Math.cos(Math.toRadians(i)) * 0.13f,
                    (float) Math.sin(Math.toRadians(i)) * 0.13f
            );
        }
        GL11.glEnd();

        // 绘制中间圈
        GL11.glTranslated(0.0f, 0.0f, -extraHeight);
        GL11.glLineWidth(2.5f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.20f;
            float y = (float) Math.sin(angle) * 0.20f;

            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        // 绘制外圈
        GL11.glTranslated(0.0f, 0.0f, -extraHeight);
        GL11.glLineWidth(4.0f * getExtraWidth());

        // 绘制外圈的第一半
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 15; i <= 165; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.27f;
            float y = (float) Math.sin(angle) * 0.27f;
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        // 绘制外圈的第二半
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 195; i <= 345; i += 5) {
            float angle = (float) Math.toRadians(i);
            float x = (float) Math.cos(angle) * 0.27f;
            float y = (float) Math.sin(angle) * 0.27f;
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        // 绘制缺口位置的延伸线条
        GL11.glLineWidth(4.0f * getExtraWidth());
        GL11.glBegin(GL11.GL_LINES);

        // 0度位置线条
        GL11.glVertex3f((float) Math.cos(Math.toRadians(0)) * 0.27f, (float) Math.sin(Math.toRadians(0)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(0)) * (0.27f + extensionLength), (float) Math.sin(Math.toRadians(0)) * (0.27f + extensionLength), 0.0f);

        // 180度位置线条
        GL11.glVertex3f((float) Math.cos(Math.toRadians(180)) * 0.27f, (float) Math.sin(Math.toRadians(180)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(180)) * (0.27f + extensionLength), (float) Math.sin(Math.toRadians(180)) * (0.27f + extensionLength), 0.0f);

        GL11.glEnd();

        // 绘制缺口边缘的短线
        GL11.glLineWidth(4.0f * getExtraWidth()); // 短线条的粗细
        GL11.glBegin(GL11.GL_LINES);

        // 15度位置的短线
        GL11.glVertex3f((float) Math.cos(Math.toRadians(15)) * 0.268f, (float) Math.sin(Math.toRadians(15)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(15)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(12)) * (0.27f + smallExtensionLength), 0.0f);

        // 165度位置的短线
        GL11.glVertex3f((float) Math.cos(Math.toRadians(165)) * 0.268f, (float) Math.sin(Math.toRadians(165)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(165)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(168)) * (0.27f + smallExtensionLength), 0.0f);

        // 195度位置的短线
        GL11.glVertex3f((float) Math.cos(Math.toRadians(195)) * 0.268f, (float) Math.sin(Math.toRadians(195)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(195)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(192)) * (0.27f + smallExtensionLength), 0.0f);

        // 345度位置的短线
        GL11.glVertex3f((float) Math.cos(Math.toRadians(345)) * 0.268f, (float) Math.sin(Math.toRadians(345)) * 0.27f, 0.0f);
        GL11.glVertex3f((float) Math.cos(Math.toRadians(345)) * (0.27f + smallExtensionLength), (float) Math.sin(Math.toRadians(348)) * (0.27f + smallExtensionLength), 0.0f);

        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public void drawArisHalo() {
        animation.animate(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true;

        float height = mc.thePlayer.height + 0.25f + (float) animation.getValue();

        GL11.glPushMatrix();
        Timer timer = ReflectionManager.Minecraft$getTimer(mc);
        if (timer == null) return;
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosX, mc.getRenderManager()),
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosY, mc.getRenderManager()) + height,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer.renderPartialTicks - RenderUtil.getRenderPos(RenderUtil.renderPosZ, mc.getRenderManager())
        );
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.color(new Color(161, 253, 228, 220));

        float yaw = mc.thePlayer.rotationYaw;

        // 使光环中心随玩家朝向旋转
        GL11.glRotatef(-yaw, 0F, 1F, 0F);
        GL11.glRotatef(90, 1F, 0F, 0F);

        drawRectangle(0.20f, 0.02f, 0.26f, 0.26f, 4f, false);
        drawRectangle(0.2f, 0.3f, 0.4f, 0.4f, 6f, false);
        drawRectangle(-0.09f, 0.21f, 0.35f, 0.35f, 5f, false);
        drawRectangle(-0.13f, 0.45f, 0.15f, 0.05f, 4f, false);
        drawRectangle(0.12f, 0.49f, 0.1f, 0f, 6f, false);

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public float getExtraWidth() {
        if (mc.gameSettings.thirdPersonView == 0 || (ReflectionManager.hasOptifine && Config.zoomMode)) {
            return 2;
        }
        //if (ModuleInstance.getModule(SmallPlayer.class).isEnabled()) return 0.5f;
        return 1;
    }

    private void drawRectangle(float x, float y, float width, float height, float lineWidth, boolean filled) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x - 0.05f, y - 0.15f, 0.0f);

        // 绘制长方形的边缘
        if (filled) {
            GL11.glLineWidth(lineWidth * getExtraWidth());
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(-width / 2, -height / 2);
            GL11.glVertex2f(width / 2, -height / 2);
            GL11.glVertex2f(width / 2, height / 2);
            GL11.glVertex2f(-width / 2, height / 2);
            GL11.glEnd();
        } else {
            GL11.glLineWidth(lineWidth * getExtraWidth());
            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex2f(-width / 2, -height / 2);
            GL11.glVertex2f(width / 2, -height / 2);
            GL11.glVertex2f(width / 2, height / 2);
            GL11.glVertex2f(-width / 2, height / 2);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
    }

    private void drawStar(float x, float y, float radius) {
        final int POINTS = 5; // 五角星的5个外顶点
        final float[] angles = new float[POINTS * 2];

        // 计算每个点的角度（逆时针计算）
        for (int i = 0; i < POINTS * 2; i++) {
            angles[i] = (float) Math.toRadians(i * 360.0f / (POINTS * 2) - 90.0f); // 从 -90 度开始使第一个点位于正上方
        }

        // 计算外层和内层的顶点
        float[] vertices = new float[POINTS * 4];
        float innerRadius = radius * 0.6f; // 调整内半径的比例（使边更短）

        for (int i = 0; i < POINTS * 2; i++) {
            float angle = angles[i];
            float currentRadius = (i % 2 == 0) ? radius : innerRadius;
            vertices[i * 2] = x + (float) Math.cos(angle) * currentRadius;
            vertices[i * 2 + 1] = y + (float) Math.sin(angle) * currentRadius;
        }

        // 绘制五角星的边框（按顺序连接每一个点）
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < POINTS * 2; i++) {
            GL11.glVertex2f(vertices[i * 2], vertices[i * 2 + 1]);
        }
        GL11.glEnd();
    }

    private void drawTriangle(float x, float y, float base, float height, float rotationAngle) {
        // 三角形的3个顶点
        float[] vertices = new float[6];

        // 计算三角形的3个顶点坐标
        // 顶点1：底边的左端点
        vertices[0] = -base / 2;
        vertices[1] = 0;

        // 顶点2：底边的右端点
        vertices[2] = base / 2;
        vertices[3] = 0;

        // 顶点3：三角形的顶点（在底边的正上方，定义的高度）
        vertices[4] = 0;
        vertices[5] = height;

        GL11.glPushMatrix();

        // 移动到三角形的中心
        GL11.glTranslatef(x, y, 0);

        // 旋转三角形
        GL11.glRotatef(rotationAngle, 0, 0, 1);

        // 绘制三角形的边框（按顺序连接每一个点）
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < 3; i++) {
            GL11.glVertex2f(vertices[i * 2], vertices[i * 2 + 1]);
        }
        GL11.glEnd();

        GL11.glPopMatrix();
    }
}