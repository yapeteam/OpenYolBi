package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.mixin.MixinManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;

public class SelfDestruct extends Module {
    public SelfDestruct() {
        super("自毁", ModuleCategory.MISC);
    }

    public void onEnable() {
        try {
            setEnabled(false);
            if (mc.currentScreen != null) mc.displayGuiScreen(null);
            MixinManager.destroyClient();
            YolBi.instance.shutdown();
        } catch (Throwable e) {
            Logger.exception(e);
            YolBi.instance.getNotificationManager().post(
                    new Notification(
                            "SelfDestruct Failed",
                            Easing.EASE_IN_OUT_QUAD,
                            Easing.EASE_IN_OUT_QUAD,
                            2500, NotificationType.FAILED
                    )
            );
        }
    }
}
