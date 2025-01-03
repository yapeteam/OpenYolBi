package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import com.mojang.blaze3d.platform.InputConstants;
import static cn.yapeteam.yolbi.module.Thread.Thread_InvClear.OpenThread;

public class InvClean extends Module {
    public InvClean(){
        super("InvCleanGrim", ModuleCategory.MISC, InputConstants.KEY_B);
    }

    @Override
    protected void onEnable() {
        OpenThread(true);
    }

    @Override
    protected void onDisable() {
        OpenThread(false);
    }
}
