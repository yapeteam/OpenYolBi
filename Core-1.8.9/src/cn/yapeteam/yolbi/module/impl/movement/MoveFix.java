package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMoveInput;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.player.MoveUtil;

public class MoveFix extends Module {
    public MoveFix() {
        super("MoveFix", ModuleCategory.MOVEMENT);
    }

    @Listener
    private void onMoveInput(EventMoveInput event) {
        if (rotationManager.active && rotationManager.rotations != null) {
            final float yaw = rotationManager.rotations.x;
            MoveUtil.fixMovement(event, yaw);
        }
    }
}
