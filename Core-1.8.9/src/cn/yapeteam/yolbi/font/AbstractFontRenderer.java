package cn.yapeteam.yolbi.font;

import java.awt.*;

public interface AbstractFontRenderer {
    float getStringWidth(String text);

    float getStringHeight(String text);

    float drawStringWithShadow(String text, float x, float y, int color);

    void drawStringWithShadow(String text, float x, float y, Color color);

    float drawString(String text, float x, float y, int color, boolean shadow);

    float drawString(String text, float x, float y, Color color);

    float drawString(String text, float x, float y, int color);

    float getStringHeight();

    Font getFont();
}
