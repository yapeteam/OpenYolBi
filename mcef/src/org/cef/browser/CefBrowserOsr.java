package org.cef.browser;

import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IStringVisitor;
import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.client.StringVisitor;
import net.montoyo.mcef.utilities.Keyboard;
import net.montoyo.mcef.utilities.Log;
import net.montoyo.mcef.utilities.UnsafeUtils;
import org.cef.CefClient;
import org.cef.DummyComponent;
import org.cef.callback.*;
import org.cef.handler.CefDialogHandler;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefWindowHandler;
import org.cef.misc.CefPdfPrintSettings;
import org.cef.network.CefRequest;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

public class CefBrowserOsr extends CefBrowser_N implements CefRenderHandler, IBrowser {
    private CefRenderer renderer_;
    private Rectangle browser_rect_;
    private Point screenPoint_;
    private boolean isTransparent_;
    private final DummyComponent dc_;
    private MouseEvent lastMouseEvent;
    public static boolean CLEANUP = true;
    private final PaintData paintData;

    @Override
    public void replaceMisspelling(String str) {
        super.replaceMisspelling(str);
    }

    @Override
    public void stopFinding(boolean z) {
        super.stopFinding(z);
    }

    @Override
    public void find(int i, String str, boolean z, boolean z2, boolean z3) {
        super.find(i, str, z, z2, z3);
    }

    @Override
    public void printToPDF(String str, CefPdfPrintSettings cefPdfPrintSettings, CefPdfPrintCallback cefPdfPrintCallback) {
        super.printToPDF(str, cefPdfPrintSettings, cefPdfPrintCallback);
    }

    @Override
    public void print() {
        super.print();
    }

    @Override
    public void startDownload(String str) {
        super.startDownload(str);
    }

    @Override
    public void runFileDialog(CefDialogHandler.FileDialogMode fileDialogMode, String str, String str2, Vector vector, int i, CefRunFileDialogCallback cefRunFileDialogCallback) {
        super.runFileDialog(fileDialogMode, str, str2, vector, i, cefRunFileDialogCallback);
    }

    @Override
    public void setZoomLevel(double d) {
        super.setZoomLevel(d);
    }

    @Override
    public double getZoomLevel() {
        return super.getZoomLevel();
    }

    @Override
    public void setWindowVisibility(boolean z) {
        super.setWindowVisibility(z);
    }

    @Override
    public void setFocus(boolean z) {
        super.setFocus(z);
    }

    @Override
    public void close(boolean z) {
        super.close(z);
    }

    @Override
    public String getURL() {
        return super.getURL();
    }

    @Override
    public void executeJavaScript(String str, String str2, int i) {
        super.executeJavaScript(str, str2, i);
    }

    @Override
    public void loadURL(String str) {
        super.loadURL(str);
    }

    @Override
    public void loadRequest(CefRequest cefRequest) {
        super.loadRequest(cefRequest);
    }

    @Override
    public void getText(CefStringVisitor cefStringVisitor) {
        super.getText(cefStringVisitor);
    }

    @Override
    public void getSource(CefStringVisitor cefStringVisitor) {
        super.getSource(cefStringVisitor);
    }

    @Override
    public void viewSource() {
        super.viewSource();
    }

    @Override
    public boolean hasDocument() {
        return super.hasDocument();
    }

    @Override
    public boolean isPopup() {
        return super.isPopup();
    }

    @Override
    public int getFrameCount() {
        return super.getFrameCount();
    }

    @Override
    public Vector getFrameNames() {
        return super.getFrameNames();
    }

    @Override
    public Vector getFrameIdentifiers() {
        return super.getFrameIdentifiers();
    }

    @Override
    public CefFrame getFrame(String str) {
        return super.getFrame(str);
    }

    @Override
    public CefFrame getFrame(long j) {
        return super.getFrame(j);
    }

    @Override
    public CefFrame getFocusedFrame() {
        return super.getFocusedFrame();
    }

    @Override
    public CefFrame getMainFrame() {
        return super.getMainFrame();
    }

    @Override
    public int getIdentifier() {
        return super.getIdentifier();
    }

    @Override
    public void stopLoad() {
        super.stopLoad();
    }

    @Override
    public void reloadIgnoreCache() {
        super.reloadIgnoreCache();
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public boolean isLoading() {
        return super.isLoading();
    }

    @Override
    public void goForward() {
        super.goForward();
    }

    @Override
    public boolean canGoForward() {
        return super.canGoForward();
    }

    @Override
    public void goBack() {
        super.goBack();
    }

    @Override
    public boolean canGoBack() {
        return super.canGoBack();
    }

    @Override
    public CefBrowser getDevTools(Point point) {
        return super.getDevTools(point);
    }

    @Override
    public CefBrowser getDevTools() {
        return super.getDevTools();
    }

    @Override
    public void onBeforeClose() {
        super.onBeforeClose();
    }

    @Override
    public boolean doClose() {
        return super.doClose();
    }

    @Override
    public void setCloseAllowed() {
        super.setCloseAllowed();
    }

    @Override
    public CefWindowHandler getWindowHandler() {
        return super.getWindowHandler();
    }

    @Override
    public CefClient getClient() {
        return super.getClient();
    }

    public CefBrowserOsr(CefClient client, String url, boolean transparent, CefRequestContext context) {
        this(client, url, transparent, context, null, null);
    }

    private CefBrowserOsr(CefClient client, String url, boolean transparent, CefRequestContext context, CefBrowserOsr parent, Point inspectAt) {
        super(client, url, context, parent, inspectAt);
        this.browser_rect_ = new Rectangle(0, 0, 1, 1);
        this.screenPoint_ = new Point(0, 0);
        this.dc_ = new DummyComponent();
        this.lastMouseEvent = new MouseEvent(this.dc_, 503, 0L, 0, 0, 0, 0, false);
        this.paintData = new PaintData();
        this.isTransparent_ = transparent;
        try {
            this.renderer_ = CefBrowserFactory.Renderer.getConstructor(boolean.class).newInstance(transparent); // new CefRenderer(transparent);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createImmediately() {
        createBrowserIfRequired(false);
    }

    @Override
    public Component getUIComponent() {
        return this.dc_;
    }

    @Override
    public CefRenderHandler getRenderHandler() {
        return this;
    }

    @Override
    public void loadString(String val, String url) {
    }

    @Override
    protected CefBrowser_N createDevToolsBrowser(CefClient client, String url, CefRequestContext context, CefBrowser_N parent, Point inspectAt) {
        return new CefBrowserOsr(client, url, this.isTransparent_, context, this, inspectAt);
    }

    @Override
    public Rectangle getViewRect(CefBrowser browser) {
        return this.browser_rect_;
    }

    @Override
    public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
        Point screenPoint = new Point(this.screenPoint_);
        screenPoint.translate(viewPoint.x, viewPoint.y);
        return screenPoint;
    }

    @Override
    public void onPopupShow(CefBrowser browser, boolean show) {
        if (!show) {
            this.renderer_.clearPopupRects();
            invalidate();
        }
    }

    @Override
    public void onPopupSize(CefBrowser browser, Rectangle size) {
        this.renderer_.onPopupSize(size);
    }

    public static class PaintData {
        private ByteBuffer buffer;
        private int width;
        private int height;
        private Rectangle[] dirtyRects;
        private boolean hasFrame;
        private boolean fullReRender;

        private PaintData() {
        }
    }

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
        if (popup) {
            return;
        }
        int size = (width * height) << 2;
        synchronized (this.paintData) {
            if (buffer.limit() <= size) {
                if (this.paintData.hasFrame) {
                    this.paintData.fullReRender = true;
                }
                if (this.paintData.buffer == null || size != this.paintData.buffer.capacity()) {
                    this.paintData.buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
                }
                this.paintData.buffer.position(0);
                this.paintData.buffer.limit(buffer.limit());
                buffer.position(0);
                this.paintData.buffer.put(buffer);
                this.paintData.buffer.position(0);
                this.paintData.width = width;
                this.paintData.height = height;
                this.paintData.dirtyRects = dirtyRects;
                this.paintData.hasFrame = true;
            } else {
                Log.warning("Skipping MCEF browser frame, data is too heavy", new Object[0]);
            }
        }
    }

    public void mcefUpdate() {
        synchronized (this.paintData) {
            if (this.paintData.hasFrame) {
                this.renderer_.onPaint(false, this.paintData.dirtyRects, this.paintData.buffer, this.paintData.width, this.paintData.height, this.paintData.fullReRender);
                this.paintData.hasFrame = false;
                this.paintData.fullReRender = false;
            }
        }
        sendMouseEvent(this.lastMouseEvent);
    }

    @Override
    public void onCursorChange(CefBrowser browser, int cursorType) {
    }

    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        return false;
    }

    @Override
    public void updateDragCursor(CefBrowser browser, int operation) {
    }

    private void createBrowserIfRequired(boolean hasParent) {
        if (getNativeRef("CefBrowser") == 0) {
            if (getParentBrowser() != null) {
                createDevTools(getParentBrowser(), getClient(), 0L, true, this.isTransparent_, null, getInspectAt());
            } else {
                createBrowser(getClient(), 0L, getUrl(), true, this.isTransparent_, null, getRequestContext());
            }
            return;
        }
        setFocus(true);
    }

    @Override
    public void close() {
        if (CLEANUP) {
            ((ClientProxy) MCEF.PROXY).removeBrowser(this);
            this.renderer_.cleanup();
        }
        super.close(true);
    }

    @Override
    public void resize(int width, int height) {
        this.browser_rect_.setBounds(0, 0, width, height);
        this.dc_.setBounds(this.browser_rect_);
        this.dc_.setVisible(true);
        wasResized(width, height);
    }

    @Override
    public void draw(double x1, double y1, double x2, double y2) {
        this.renderer_.render(x1, y1, x2, y2);
    }

    @Override
    public int getTextureID() {
        return this.renderer_.texture_id_[0];
    }

    public static int remapKeycode(int kc, char c) {
        switch (kc) {
            case 1:
                return 27;
            case 14:
                return 8;
            case 15:
                return 9;
            case 28:
                return 13;
            case 199:
                return 36;
            case CefMenuModel.MenuId.MENU_ID_SPELLCHECK_SUGGESTION_0:
                return 38;
            case CefMenuModel.MenuId.MENU_ID_SPELLCHECK_SUGGESTION_1:
                return 33;
            case CefMenuModel.MenuId.MENU_ID_SPELLCHECK_SUGGESTION_3:
                return 37;
            case CefMenuModel.MenuId.MENU_ID_NO_SPELLING_SUGGESTIONS:
                return 39;
            case 207:
                return 35;
            case 208:
                return 40;
            case 209:
                return 34;
            case 211:
                return 46;
            default:
                return c;
        }
    }

    @Override
    public void sendMouseMove(int x, int y, int mods, boolean left) {
        MouseEvent ev = new MouseEvent(this.dc_, 503, 0L, mods, x, y, 0, false);
        this.lastMouseEvent = ev;
        sendMouseEvent(ev);
    }

    @Deprecated
    public void injectMouseButton(int x, int y, int mods, int btn, boolean pressed, int ccnt) {
        MouseEvent ev = new MouseEvent(this.dc_, pressed ? 501 : 502, 0L, mods, x, y, ccnt, false, btn);
        sendMouseEvent(ev);
    }

    @Override
    public void sendMouseButton(int x, int y, int mods, int button, boolean pressed, int ccnt) {
        if (button == 2) {
            button = 0;
        } else if (button == 3) {
            button = 2;
        }

        if (button == 4 && canGoBack()) {
            goBack();
            return;
        } else if (button == 5 && canGoForward()) {
            goForward();
            return;
        }

        MouseEvent event = new MouseEvent(dc_, pressed ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED, 0, mods, x, y, ccnt, false, button);
        sendMouseEvent(event);
    }

    @Override
    public void sendMouseWheel(int x, int y, int mods, int amount, int rot) {
        MouseWheelEvent event = new MouseWheelEvent(dc_, MouseEvent.MOUSE_WHEEL, 0, mods, x, y, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, amount, rot);
        sendMouseWheelEvent(event);
    }

    @Override
    public void sendKeyPressed(int key, char c, int mods) {
        if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_HOME || key == Keyboard.KEY_END ||
                key == Keyboard.KEY_UP || key == Keyboard.KEY_DOWN || key == Keyboard.KEY_LEFT || key == Keyboard.KEY_RIGHT ||
                key == Keyboard.KEY_CAPITAL || key == Keyboard.KEY_PAUSE || key == Keyboard.KEY_INSERT) {
            KeyEvent ev = UnsafeUtils.makeEvent(dc_, remapKeyCode(key, KeyEvent.CHAR_UNDEFINED, mods), KeyEvent.CHAR_UNDEFINED,
                    KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_PRESSED, 0, remapModifiers(mods), mapScanCode(key, c));
            sendKeyEvent(ev);
        } else {

            int raw = key;

            if (c == '\n') {
                raw = 13;
            }

            KeyEvent ev = UnsafeUtils.makeEvent(dc_, remapKeyCode(key, c, mods), c,
                    KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_PRESSED, 0, remapModifiers(mods), mapScanCode(key, c), raw);
            sendKeyEvent(ev);
        }
    }

    @Override
    public void sendKeyTyped(int key, int mods) {
        char c = (char) key;
        int keyRemapped = remapKeyCode(key, (char) key, mods);

        if (key != KeyEvent.VK_UNDEFINED) {

            if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_HOME || key == Keyboard.KEY_END ||
                    key == Keyboard.KEY_UP || key == Keyboard.KEY_DOWN || key == Keyboard.KEY_RIGHT ||
                    key == Keyboard.KEY_LEFT || key == Keyboard.KEY_PAUSE || key == Keyboard.KEY_INSERT) {
                KeyEvent ev = UnsafeUtils.makeEvent(dc_, keyRemapped, KeyEvent.CHAR_UNDEFINED,
                        KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_TYPED, 0, remapModifiers(mods), mapScanCode(keyRemapped, c));
                sendKeyEvent(ev);
            } else if (key == KeyEvent.VK_ENTER || key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {

                int raw = key;

                if (c == '\n') {
                    raw = 13;
                }

                KeyEvent ev = UnsafeUtils.makeEvent(dc_, key, '\n',
                        KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_TYPED, 0, remapModifiers(mods), mapScanCode(key, c), raw);
                sendKeyEvent(ev);
            } else {
                KeyEvent ev = UnsafeUtils.makeEvent(dc_, keyRemapped, c,
                        KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_TYPED, 0, remapModifiers(mods), mapScanCode(keyRemapped, c));
                sendKeyEvent(ev);
            }
        }
    }

    @Override
    public void sendKeyReleased(int key, char c, int mods) {
        KeyEvent ev;
        if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_HOME || key == Keyboard.KEY_END || key == Keyboard.KEY_UP ||
                key == Keyboard.KEY_DOWN || key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_LEFT || key == Keyboard.KEY_PAUSE ||
                key == Keyboard.KEY_INSERT) {
            ev = UnsafeUtils.makeEvent(dc_, remapKeyCode(key, KeyEvent.CHAR_UNDEFINED, mods), c,
                    KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_RELEASED, 0, remapModifiers(mods), mapScanCode(key, c));
        } else {
            ev = UnsafeUtils.makeEvent(dc_, remapKeyCode(key, c, mods), c,
                    KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_RELEASED, 0, remapModifiers(mods), mapScanCode(key, c));
        }
        sendKeyEvent(ev);
    }

    @Override
    public void sendPaste() {
        String clipboardText = getStringFromClipboard();

        if (clipboardText != null) {
            for (char c : clipboardText.toCharArray()) {
                KeyEvent ev = new KeyEvent(dc_, KeyEvent.KEY_TYPED, 0, 0, 0, c);
                sendKeyEvent(ev);
            }
        }
    }

    public static String getStringFromClipboard() {
        try {
            return Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor).toString();
        } catch (Exception e) {
            return null;
        }
    }


    private int remapModifiers(int mods) {

        int vkMods = 0;

        if ((mods & Keyboard.KEY_LCONTROL) != 0) {
            vkMods |= KeyEvent.CTRL_DOWN_MASK | KeyEvent.CTRL_MASK;
        }

        if ((mods & Keyboard.KEY_LMENU) != 0) {
            vkMods |= KeyEvent.ALT_DOWN_MASK | KeyEvent.ALT_MASK;
        }

        if ((mods & Keyboard.KEY_LSHIFT) != 0) {
            vkMods |= KeyEvent.SHIFT_DOWN_MASK | KeyEvent.SHIFT_MASK;
        }

        if ((mods & Keyboard.KEY_LMETA) != 0) {
            vkMods |= KeyEvent.META_DOWN_MASK | KeyEvent.META_MASK;
        }

        return vkMods;
    }

    private int remapKeyCode(int key, char c, int mods) {

        if (key == Keyboard.KEY_ESCAPE) {
            return KeyEvent.VK_ESCAPE;
        }

        if (key == Keyboard.KEY_TAB) {
            return KeyEvent.VK_TAB;
        }

        int ck = getChar(key, 0, mods);

        if (ck == 0) {
            return c;
        }

        return ck;
    }

    private int getChar(int key, int scanCode, int mod) {

        if (key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_RCONTROL || key == Keyboard.KEY_END) {
            return '\uFFFF';
        }

        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER || key == KeyEvent.VK_ENTER) {
            return '\n';
        }

        if (key == Keyboard.KEY_SPACE) {
            return 32;
        }

        if (key == Keyboard.KEY_BACK) {
            return 8;
        }

        if (key == Keyboard.KEY_DELETE) {
            return '\u007F';
        }

        return key;
    }

    private long mapScanCode(int key, char c) {

        if (key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_RCONTROL) {
            return 29;
        }

        if (key == Keyboard.KEY_DELETE) {
            return 83;
        }

        if (key == Keyboard.KEY_LEFT) {
            return 75;
        }

        if (key == Keyboard.KEY_DOWN) {
            return 80;
        }

        if (key == Keyboard.KEY_UP) {
            return 72;
        }

        if (key == Keyboard.KEY_RIGHT) {
            return 77;
        }

        if (key == Keyboard.KEY_END) {
            return 79;
        }

        if (key == Keyboard.KEY_HOME) {
            return 71;
        }

        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER || key == KeyEvent.VK_ENTER) {
            return 28;
        }

        if (key == Keyboard.KEY_BACK) {
            return 14;
        }

        return 0;
    }


    @Override
    public void runJS(String script, String frame) {
        executeJavaScript(script, frame, 0);
    }

    @Override
    public void visitSource(IStringVisitor isv) {
        getSource(new StringVisitor(isv));
    }

    @Override
    public boolean isPageLoading() {
        return isLoading();
    }
}
