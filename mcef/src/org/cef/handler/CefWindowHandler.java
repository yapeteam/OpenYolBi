package org.cef.handler;

import org.cef.browser.CefBrowser;

import java.awt.*;

public interface CefWindowHandler {
    Rectangle getRect(CefBrowser cefBrowser);

    void onMouseEvent(CefBrowser cefBrowser, int i, int i2, int i3, int i4, int i5);
}
