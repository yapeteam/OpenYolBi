package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMouseOver;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import com.mojang.blaze3d.platform.InputConstants;

public class Reach extends Module {
    public Reach(){
        super("Reach", ModuleCategory.COMBAT, InputConstants.KEY_R);
        addValues(reach);
    }
    private NumberValue<Double> reach = new NumberValue<Double>("Reach",3.0,2.0,10.1,0.1);
    @Listener
    public void onMouse(EventMouseOver e){
        e.setReach((float) reach.getValue().doubleValue());
    }
}
