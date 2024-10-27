package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.ui.browser.BrowserHandler;

public class WebBrowser extends Module {
    public WebBrowser() {
        super("WebBrowser", ModuleCategory.VISUAL);
    }


    @Override
    protected void onEnable() {
        BrowserHandler.INSTANCE.display();
        setEnabled(false);
    }
}
