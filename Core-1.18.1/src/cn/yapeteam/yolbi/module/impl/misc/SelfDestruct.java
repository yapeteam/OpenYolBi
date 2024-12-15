package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.mixin.MixinManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;

public class SelfDestruct extends Module {
    public SelfDestruct() {
        super("SelfExit", ModuleCategory.MISC);
    }

    public void onEnable() {
        try {
            setEnabled(false);
            if (mc.screen != null) mc.setScreen(null);
            Natives.DeleteInjectorJarHistory();
            MixinManager.destroyClient();
            YolBi.instance.shutdown();
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }
}
