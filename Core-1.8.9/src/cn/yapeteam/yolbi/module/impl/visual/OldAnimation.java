package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;

public class OldAnimation extends Module {
    public static OldAnimation instance;

    public OldAnimation() {
        super("OldAnimation", ModuleCategory.VISUAL);
        instance = this;
    }
}
