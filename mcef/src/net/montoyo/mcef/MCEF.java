package net.montoyo.mcef;

import cn.yapeteam.loader.Loader;
import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.utilities.Log;

import java.io.File;

public class MCEF {
    public static boolean SKIP_UPDATES;
    public static boolean WARN_UPDATES;
    public static boolean USE_FORGE_SPLASH;
    public static String HOME_PAGE;
    public static String[] CEF_ARGS = new String[0];
    public static boolean CHECK_VRAM_LEAK;
    public static boolean SHUTDOWN_JCEF;
    public static boolean SECURE_MIRRORS_ONLY;
    public static MCEF INSTANCE = new MCEF();
    public static final BaseProxy PROXY = new ClientProxy();

    public void onInit() {
        Log.info("Loading MCEF config...");

        //Config: main
        SKIP_UPDATES = true;
        WARN_UPDATES = true;
        USE_FORGE_SPLASH = false;
        CEF_ARGS = new String[]{"--autoplay-policy=no-user-gesture-required", "--disable-web-security"};

        //Config: debug
        CHECK_VRAM_LEAK = true;
        PROXY.onInit(new File(Loader.YOLBI_DIR, "resources/cef/").getAbsolutePath());
    }

    public static void onMinecraftShutdown() {
        Log.info("Minecraft shutdown hook called!");
        PROXY.onShutdown();
    }
}
