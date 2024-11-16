package cn.yapeteam.yolbi.notification;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.utils.player.animation.Animation;
import cn.yapeteam.yolbi.utils.player.animation.Easing;
import cn.yapeteam.yolbi.utils.render.ColorUtil;
import cn.yapeteam.yolbi.utils.render.GradientBlur;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @author yuxiangll & TIMER_err
 */
@Getter
public class Notification {
    private final String content;
    private final Animation animationX, animationY, animationProcess;
    private final NotificationType type;
    private final Color color;
    private final long begin_time, duration;
    private boolean initialized = false;
    private final float height = 20;

    public Notification(String content, Easing easingX, Easing easingY, long duration, NotificationType type) {
        this.content = content;
        this.animationX = new Animation(easingX, (long) (duration * 0.2));
        this.animationY = new Animation(easingY, 400);
        this.animationProcess = new Animation(Easing.EASE_OUT_QUART, (long) (duration * 0.8));
        this.type = type;
        switch (type) {
            case INIT:
                color = new Color(0, 119, 255);
                break;
            case SUCCESS:
                color = new Color(44, 253, 37);
                break;
            case FAILED:
                color = new Color(255, 0, 0);
                break;
            case WARNING:
                color = new Color(255, 196, 0);
                break;
            default:
                color = new Color(-1);
        }
        begin_time = System.currentTimeMillis();
        this.duration = duration;
    }

    public Notification(String content, long duration, NotificationType type) {
        this.content = content;
        this.animationX = new Animation(Easing.EASE_OUT_BACK, (long) (duration * 0.2));
        this.animationY = new Animation(Easing.EASE_IN_OUT_CUBIC, 400);
        this.animationProcess = new Animation(Easing.EASE_OUT_QUART, (long) (duration * 0.8));
        this.type = type;
        switch (type) {
            case INIT:
                color = new Color(0, 119, 255);
                break;
            case SUCCESS:
                color = new Color(44, 253, 37);
                break;
            case FAILED:
                color = new Color(255, 0, 0);
                break;
            case WARNING:
                color = new Color(255, 196, 0);
                break;
            default:
                color = new Color(-1);
        }
        begin_time = System.currentTimeMillis();
        this.duration = duration;
    }

    public boolean isDone() {
        return System.currentTimeMillis() >= begin_time + duration;
    }

    private final GradientBlur blur = new GradientBlur(GradientBlur.Type.LR);

    public void render(ScaledResolution sr, int index, float partialTicks) {
        val font = YolBi.instance.getFontManager().getJelloRegular18();

        float width = font.getStringWidth(content) + 5 * 2;
        float targetY = sr.getScaledHeight() - (height + 2) * (index + 1);
        if (!initialized) {
            animationX.setStartValue(sr.getScaledWidth());
            animationY.setStartValue(targetY);
            initialized = true;
        }
        float targetX = sr.getScaledWidth() - width - 2;
        if (System.currentTimeMillis() >= begin_time + duration * 0.8) {
            targetX = sr.getScaledWidth() + 2;
            animationX.setDuration((long) (duration * 0.2));
        }

        float x = (float) animationX.animate(targetX), y = (float) animationY.animate(targetY);
        blur.update(x, y, width, height);
        RenderUtil.drawBloomShadow(x, y, width, height, 6, 5, new Color(0, 0, 0).getRGB(), false);
        blur.render(x, y, width, height, partialTicks, 1);
        RenderUtil.drawRect(x, y, x + width * animationProcess.animate(1), y + height, ColorUtil.reAlpha(color, 0.6f).getRGB());
        font.drawString(content, x + 5, y + (height - font.getStringHeight()) / 2f, type == NotificationType.WARNING ? 0 : -1);
    }
}
