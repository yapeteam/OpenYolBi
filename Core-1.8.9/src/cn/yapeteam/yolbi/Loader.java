package cn.yapeteam.yolbi;

import cn.yapeteam.loader.*;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.mixin.MixinManager;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public class Loader {
    public static void start() {
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            if (BootStrap.getVersion().first != Version.V1_8_9) {
                Logger.error("Unsupported Minecraft version: {}", BootStrap.getVersion().first.getVersion());
                SocketSender.send("CLOSE");
                return;
            }
            Logger.info("Start Loading!");
            Logger.info("Initializing MixinLoader...");
            MixinManager.init();
            Logger.warn("Start transforming!");
            MixinManager.transform();
            Logger.success("Welcome {} ver {}", YolBi.name, YolBi.version);
            SocketSender.send("CLOSE");
            SocketSender.close();
            System.setProperty("skija.library.path", new File(YolBi.YOLBI_DIR,"resources/natives").getAbsolutePath());
            YolBi.initialize();
        } catch (InvocationTargetException e) {
            Logger.exception(e);
            Logger.error("Cause:");
            Logger.exception(e.getCause());
            Logger.error("Target exception:");
            Logger.exception(e.getTargetException());
        } catch (Throwable e) {
            Logger.exception(e);
            try {
                Logger.writeCache();
                Desktop.getDesktop().open(Logger.getLog());
            } catch (Throwable ignored) {
            }
        }
    }
}
