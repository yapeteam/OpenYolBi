package cn.yapeteam.yolbi.ui.webui.impl;

import cn.yapeteam.yolbi.ui.webui.WebScreen;
import cn.yapeteam.yolbi.ui.webui.impl.handlers.CategoryHandler;
import cn.yapeteam.yolbi.ui.webui.impl.handlers.ModuleHandler;
import cn.yapeteam.yolbi.ui.webui.impl.handlers.SetHandler;
import cn.yapeteam.yolbi.ui.webui.impl.handlers.ValueHandler;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IJSQueryCallback;

import java.util.HashMap;
import java.util.Map;

public class ClickUI extends WebScreen {
    public static ClickUI instance = new ClickUI();

    @Override
    public void onAddressChange(IBrowser browser, String url) {

    }

    @Override
    public void onTitleChange(IBrowser browser, String title) {
        System.out.println("Title: " + title);
    }

    @Override
    public void onTooltip(IBrowser browser, String text) {

    }

    @Override
    public void onStatusMessage(IBrowser browser, String value) {

    }

    private final Map<String, Handler> handlers = new HashMap<>();

    public ClickUI() {
        super("clickui");
        handlers.put("cats", new CategoryHandler());
        handlers.put("mods", new ModuleHandler());
        handlers.put("values", new ValueHandler());
        handlers.put("set", new SetHandler());
    }

    @Override
    public boolean handleQuery(IBrowser b, long queryId, String query, boolean persistent, IJSQueryCallback cb) {
        String[] parts = query.split(":");
        if (parts.length != 2 && parts.length != 1) return false;
        String[] path = parts[0].split("/");
        if (path.length == 2 && path[0].equals("clickui")) {
            String[] args = parts[1].split(",");
            Handler handler = handlers.get(path[1]);
            if (handler == null) {
                cb.failure(0, "invalid request");
                return true;
            }
            handler.handle(args, cb);
            return true;
        }
        return false;
    }

    @Override
    public void cancelQuery(IBrowser b, long queryId) {

    }
}
