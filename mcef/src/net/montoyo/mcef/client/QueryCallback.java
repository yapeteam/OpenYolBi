package net.montoyo.mcef.client;

import net.montoyo.mcef.api.IJSQueryCallback;
import org.cef.callback.CefQueryCallback;

public class QueryCallback implements IJSQueryCallback {

    private CefQueryCallback cb;
    
    public QueryCallback(CefQueryCallback cb) {
        this.cb = cb;
    }
    
    @Override
    public void success(String response) {
        cb.success(response);
    }

    @Override
    public void failure(int errId, String errMsg) {
        cb.failure(errId, errMsg);
    }

}
