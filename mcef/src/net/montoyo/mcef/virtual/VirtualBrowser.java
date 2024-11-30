package net.montoyo.mcef.virtual;

import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IStringVisitor;

public class VirtualBrowser implements IBrowser {

    @Override
    public void close() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void draw(double x1, double y1, double x2, double y2) {
    }

    @Override
    public int getTextureID() {
        return 0;
    }

    @Override
    public void sendMouseMove(int x, int y, int mods, boolean left) {

    }

    @Override
    public void sendMouseButton(int x, int y, int mods, int button, boolean pressed, int ccnt) {

    }

    @Override
    public void sendMouseWheel(int x, int y, int mods, int amount, int rot) {

    }

    @Override
    public void sendKeyPressed(int key, char c, int mods) {

    }

    @Override
    public void sendKeyTyped(int key, int mods) {

    }

    @Override
    public void sendKeyReleased(int key, char c, int mods) {

    }

    @Override
    public void sendPaste() {

    }

    @Override
    public void runJS(String script, String frame) {
    }

    @Override
    public void loadURL(String url) {
    }

    @Override
    public void goBack() {
    }

    @Override
    public void goForward() {
    }

    @Override
    public String getURL() {
        return "about:blank";
    }

    @Override
    public void visitSource(IStringVisitor isv) {
        isv.visit("https://www.youtube.com/watch?v=VX5gXHcbJAk");
    }

    @Override
    public boolean isPageLoading() {
        return true;
    }

}
