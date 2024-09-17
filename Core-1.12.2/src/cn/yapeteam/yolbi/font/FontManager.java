package cn.yapeteam.yolbi.font;

import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.yolbi.font.awt.AWTFontRenderer;
import lombok.Getter;

import java.awt.*;

@Getter
public class FontManager {
    public FontManager() {
        JelloRegular18 = new AWTFontRenderer(getFont("JelloRegular.ttf", 18), false);
        PingFang10 = new AWTFontRenderer(getFont("PingFang_Normal.ttf", 10), true);
        PingFang12 = new AWTFontRenderer(getFont("PingFang_Normal.ttf", 12), true);
        PingFang13 = new AWTFontRenderer(getFont("PingFang_Normal.ttf", 13), true);
        PingFang14 = new AWTFontRenderer(getFont("PingFang_Normal.ttf", 14), true);
        PingFang18 = new AWTFontRenderer(getFont("PingFang_Normal.ttf", 18), true);
        PingFang16 = new AWTFontRenderer(getFont("PingFang_Normal.ttf", 16), true);
        PingFangBold18 = new AWTFontRenderer(getFont("PingFang_Bold.ttf", 16), true);
        FLUXICON14 = new AWTFontRenderer(getFont("fluxicon.ttf", 18), false);
        default18 = new AWTFontRenderer(new Font(null, Font.PLAIN, 18), true);
    }

    private static Font getFont(String name, int size, int type) {
        return FontUtil.getFontFromTTF(ResourceManager.resources.getStream("fonts/" + name), size, type);
    }

    private static Font getFont(String name, int size) {
        return FontUtil.getFontFromTTF(ResourceManager.resources.getStream("fonts/" + name), size, Font.PLAIN);
    }

    private final AbstractFontRenderer JelloRegular18;
    private final AbstractFontRenderer PingFang10;
    private final AbstractFontRenderer PingFang12;
    private final AbstractFontRenderer PingFang13;
    private final AbstractFontRenderer PingFang14;
    private final AbstractFontRenderer PingFang16;
    private final AbstractFontRenderer PingFang18;
    private final AbstractFontRenderer PingFangBold18;
    private final AbstractFontRenderer FLUXICON14;
    private final AbstractFontRenderer default18;
}
