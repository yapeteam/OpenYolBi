package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import com.mojang.blaze3d.platform.InputConstants;

public class YeShiEE extends Module {
    public YeShiEE(){
        super("YeShi", ModuleCategory.VISUAL, InputConstants.KEY_H);
    }

    @Override
    protected void onEnable() {
        if(mc.options!=null){
            mc.options.gamma = 500;
        }

    }

    @Override
    protected void onDisable() {
        if(mc.options!=null){
            mc.options.gamma = 20;
        }
    }
}
