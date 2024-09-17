package cn.yapeteam.yolbi.module.impl.visual;


import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventView;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;

public class Rotations extends Module {
    public Rotations() {
        super("Rotations", ModuleCategory.VISUAL);
    }

    @Listener
    public void onView(EventView event) {

        if(mc.player!=null&& YolBi.instance.getRotationManager().isActive()) {
            mc.player.setYBodyRot(YolBi.instance.getRotationManager().getRation().y);
            mc.player.setYHeadRot(YolBi.instance.getRotationManager().getRation().y);
        }
    }

}
