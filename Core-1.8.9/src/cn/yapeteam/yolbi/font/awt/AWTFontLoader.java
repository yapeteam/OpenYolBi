package cn.yapeteam.yolbi.font.awt;

import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.font.FontUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ChengFeng
 * @since 2024/7/29
 **/
public class AWTFontLoader {
    private static final Map<Integer, AWTFontRenderer> d1faults = new HashMap<>();
    public static void registerFonts() {
        d1fault(18);
    }


    public static AWTFontRenderer d1fault(int size) {
        return get(d1faults, size, new Font(null, Font.PLAIN, size), true);
    }

    private static AWTFontRenderer get(Map<Integer, AWTFontRenderer> map, int size, String name, boolean chinese) {
        if (!map.containsKey(size)) {
            Logger.info("Registering font " + name + (chinese ? " including Chinese." : ".") + " Size: " + size);
            java.awt.Font font = FontUtil.getFontFromTTF(ResourceManager.resources.getStream("fonts/" + name), size, Font.PLAIN);
            if (font != null) {
                map.put(size, new AWTFontRenderer(font, chinese));
            }
        }

        return map.get(size);
    }

    private static AWTFontRenderer get(Map<Integer, AWTFontRenderer> map, int size, Font font, boolean chinese) {
        if (!map.containsKey(size)) {
            Logger.info("Registering font " + font.getFontName() + (chinese ? " including Chinese." : ".") + " Size: " + size);
            map.put(size, new AWTFontRenderer(font, chinese));
        }
        return map.get(size);
    }
}
