package cn.yapeteam.yolbi;

import cn.yapeteam.loader.VersionInfo;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.command.CommandManager;
import cn.yapeteam.yolbi.config.ConfigManager;
import cn.yapeteam.yolbi.event.EventManager;
import cn.yapeteam.yolbi.font.FontManager;
import cn.yapeteam.yolbi.managers.RotationManager;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.module.impl.combat.Killaura;
import cn.yapeteam.yolbi.server.WebServer;
import lombok.Getter;
import net.minecraft.world.entity.PlayerRideable;

import java.io.File;
import java.io.IOException;

@Getter
public class YolBi {
    public static YolBi instance = new YolBi();
    public static final String name = "YolBi Lite";
    public static final String version = VersionInfo.version;
    public static final File YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi");
    public static boolean initialized = false;
    private EventManager eventManager;
    private ConfigManager configManager;
    private ModuleManager moduleManager;
    private RotationManager rotationManager;
    private CommandManager commandManager;
    private FontManager fontManager;
    private ConfigManager cfgmanager;
    private Killaura ka;
    public EventManager getEventManager() {
        if (eventManager == null)
            eventManager = new EventManager();
        return eventManager;
    }
    public ConfigManager getcfgmanager(){
        if (cfgmanager == null)
            cfgmanager = new ConfigManager();
        return cfgmanager;
    }
    public RotationManager getRotationManager() {
        if (rotationManager == null)
            rotationManager = new RotationManager();
        return rotationManager;
    }

    public Killaura getka() {
        if (ka == null)
            ka = new Killaura();
        return ka;
    }
    public ModuleManager getModuleManager() {
        if (moduleManager == null)
            moduleManager = new ModuleManager();
        return moduleManager;
    }
    public FontManager getFontManager() {
        if (fontManager == null)
            fontManager = new FontManager();
        return fontManager;
    }

    public static void initialize() {
        if (initialized || instance == null) return;
        initialized = true;
        boolean ignored = YOLBI_DIR.mkdirs();
        if (instance.eventManager == null)
            instance.eventManager = new EventManager();
        if (instance.fontManager == null)
            instance.fontManager = new FontManager();
        if (instance.moduleManager == null)
            instance.moduleManager = new ModuleManager();
        if (instance.rotationManager == null)
            instance.rotationManager = new RotationManager();
        instance.commandManager = new CommandManager();
        instance.configManager = new ConfigManager();
        // instance.eventManager.register(instance.rotationManager);
        instance.eventManager.register(instance.moduleManager);
        instance.eventManager.register(instance.commandManager);
        instance.moduleManager.load();
        try {
            instance.getConfigManager().load();
            WebServer.start();
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }

    public void shutdown() {
        try {
            Logger.info("Shutting down Yolbi Lite");
            Logger.writeCache();
            configManager.save();
            WebServer.stop();
            instance = new YolBi();
            System.gc();
        } catch (IOException e) {
            Logger.exception(e);
        }
    }
}
