package cn.yapeteam.yolbi.ui.webui.impl;

import net.montoyo.mcef.api.IJSQueryCallback;

public interface Handler {
    void handle(String[] args, IJSQueryCallback callback);
}
