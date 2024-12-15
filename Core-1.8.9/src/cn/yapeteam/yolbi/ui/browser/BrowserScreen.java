package cn.yapeteam.yolbi.ui.browser;

import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.ymixin.annotations.Super;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.API;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.MCEFApi;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

public class BrowserScreen extends GuiScreen {
    IBrowser browser = null;
    private GuiButton back = null;
    private GuiButton fwd = null;
    private GuiButton go = null;
    private GuiButton min = null;
    private GuiTextField url = null;
    private String urlToLoad;

    public BrowserScreen() {
        urlToLoad = MCEF.HOME_PAGE;
    }

    public BrowserScreen(String url) {
        urlToLoad = (url == null) ? MCEF.HOME_PAGE : url;
    }

    Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    protected List<GuiButton> buttonList;
    @Shadow
    public int width, height;

    @Super
    @Override
    public void initGui() {
        if (browser == null) {
            //Grab the API and make sure it isn't null.
            API api = MCEFApi.getAPI();
            if (api == null)
                return;

            //Create a browser and resize it to fit the screen
            browser = api.createBrowser((urlToLoad == null) ? MCEF.HOME_PAGE : urlToLoad, false);
            urlToLoad = null;
        }

        //Resize the browser if window size changed
        if (browser != null)
            browser.resize(mc.displayWidth, mc.displayHeight - scaleY(20));

        //Create GUI
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        FontRenderer fontRenderer = mc.fontRendererObj;

        if (url == null) {
            buttonList.add(back = (new GuiButton(0, 0, 0, 20, 20, "<")));
            buttonList.add(fwd = (new GuiButton(1, 20, 0, 20, 20, ">")));
            buttonList.add(go = (new GuiButton(2, width - 60, 0, 20, 20, "Go")));
            buttonList.add(min = (new GuiButton(3, width - 20, 0, 20, 20, "_")));

            url = new GuiTextField(5, fontRenderer, 40, 0, width - 100, 20);
            url.setMaxStringLength(65535);
            //url.setText("mod://mcef/home.html");
        } else {
            buttonList.add(back);
            buttonList.add(fwd);
            buttonList.add(go);
            buttonList.add(min);

            //Handle resizing
            go.xPosition = width - 60;
            min.xPosition = width - 20;

            String old = url.getText();
            url = new GuiTextField(5, fontRenderer, 40, 0, width - 100, 20);
            url.setMaxStringLength(65535);
            url.setText(old);
        }
    }

    public int scaleY(int y) {
        double sy = ((double) y) / ((double) height) * ((double) mc.displayHeight);
        return (int) sy;
    }

    public void loadURL(String url) {
        if (browser == null)
            urlToLoad = url;
        else
            browser.loadURL(url);
    }

    @Super
    @Override
    public void updateScreen() {
        if (urlToLoad != null && browser != null) {
            browser.loadURL(urlToLoad);
            urlToLoad = null;
        }
    }

    @Super
    @Override
    public void drawScreen(int i1, int i2, float f) {
        //Render the URL box first because it overflows a bit
        url.drawTextBox();

        //Render buttons
        super.drawScreen(i1, i2, f);

        //Renders the browser if itsn't null
        if (browser != null) {
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            browser.draw(.0d, height, width, 20.d); //Don't forget to flip Y axis.
            GlStateManager.enableDepth();
        }
    }

    @Super
    @Override
    public void onGuiClosed() {
        //Make sure to close the browser when you don't need it anymore.
        if (!BrowserHandler.INSTANCE.hasBackup() && browser != null)
            browser.close();

        Keyboard.enableRepeatEvents(false);
    }

    @Super
    @Override
    public void handleInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                mc.displayGuiScreen(null);
                return;
            }

            boolean pressed = Keyboard.getEventKeyState();
            boolean focused = url.isFocused();
            char key = Keyboard.getEventCharacter();
            int num = Keyboard.getEventKey();

            if (browser != null && !focused) { //Inject events into browser
                if (pressed)
                    browser.sendKeyPressed(num, key, 0);
                else
                    browser.sendKeyReleased(num, key, 0);

                if (key != 0) {
                    browser.sendKeyTyped(key, 0);
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_V)) {
                        browser.sendPaste();
                    }
                }
            }

            //Forward event to text box.
            if (!pressed && focused && num == Keyboard.KEY_RETURN) {
                String fixedURL = BrowserHandler.INSTANCE.getAPI().punycode(url.getText());
                browser.loadURL(fixedURL);
            } else if (pressed)
                url.textboxKeyTyped(key, num);
        }

        while (Mouse.next()) {
            int btn = Mouse.getEventButton();
            boolean pressed = Mouse.getEventButtonState();
            int sx = Mouse.getEventX();
            int sy = Mouse.getEventY();
            int wheel = Mouse.getEventDWheel();

            if (browser != null) { //Inject events into browser. TODO: Handle mods & leaving.
                int y = mc.displayHeight - sy - scaleY(20); //Don't forget to flip Y axis.

                if (wheel != 0)
                    browser.sendMouseWheel(sx, y, 0, 1, wheel);
                else if (btn == -1)
                    browser.sendMouseMove(sx, y, 0, y < 0);
                else
                    browser.sendMouseButton(sx, y, 0, btn + 1, pressed, 1);
            }

            if (pressed) { //Forward events to GUI.
                int x = sx * width / mc.displayWidth;
                int y = height - (sy * height / mc.displayHeight) - 1;

                try {
                    super.mouseClicked(x, y, btn);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                url.mouseClicked(x, y, btn);
            }
        }
    }

    //Called by ExampleMod when the current browser's URL changes.
    public void onUrlChanged(IBrowser b, String nurl) {
        if (b == browser && url != null) {
            url.setText(nurl);
        }
    }

    //Handle button clicks
    @Super
    @Override
    protected void actionPerformed(GuiButton src) {
        if (browser == null) return;
        if (src.id == 0)
            browser.goBack();
        else if (src.id == 1)
            browser.goForward();
        else if (src.id == 2) {
            String fixedURL = BrowserHandler.INSTANCE.getAPI().punycode(url.getText());
            browser.loadURL(fixedURL);
        } else if (src.id == 3) {
            BrowserHandler.INSTANCE.setBackup(this);
            mc.displayGuiScreen(null);
        }
    }

    @Super
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
