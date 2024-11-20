package cn.yapeteam.loader.oauth.util;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.oauth.login.MicrosoftLogin;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URI;

public class AgnosticUtils {
    public static JsonElement parseJson(String json) {
        return new JsonParser().parse(json);
    }

    public static boolean isEmpty(JsonArray array) {
        return array == null || array.size() == 0;
    }

    public static void openUri(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (Exception e) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uri), null);
                MicrosoftLogin.getUpdateStatus().accept("Copied link, please continue login in your browser.");
                // Logger.exception(e);
            } catch (Exception e2) {
                Logger.warn(uri);
            }
        }
    }
}
