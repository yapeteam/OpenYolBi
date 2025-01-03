package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefDragData;

import java.awt.*;
import java.nio.ByteBuffer;

public interface CefRenderHandler {
    Rectangle getViewRect(CefBrowser cefBrowser);

    Point getScreenPoint(CefBrowser cefBrowser, Point point);

    void onPopupShow(CefBrowser cefBrowser, boolean z);

    void onPopupSize(CefBrowser cefBrowser, Rectangle rectangle);

    void onPaint(CefBrowser cefBrowser, boolean z, Rectangle[] rectangleArr, ByteBuffer byteBuffer, int i, int i2);

    void onCursorChange(CefBrowser cefBrowser, int i);

    boolean startDragging(CefBrowser cefBrowser, CefDragData cefDragData, int i, int i2, int i3);

    void updateDragCursor(CefBrowser cefBrowser, int i);
}
