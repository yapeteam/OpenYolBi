package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.utils.misc.KeyCodeMapper;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;

public class Eagle extends Module {

    private final BooleanValue onlyblocks = new BooleanValue("Only Holding Blocks", true);

    private final BooleanValue onlybackwards = new BooleanValue("Only Backwards", true);

    private final BooleanValue onlyground = new BooleanValue("Only Ground", false);

    public Eagle() {
        super("Eagle", ModuleCategory.MOVEMENT);
        addValues(onlyblocks, onlybackwards, onlyground);
    }

    public Block getBlockUnderPlayer() {
        return PlayerUtil.blockRelativeToPlayer(0, -1, 0);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.currentScreen != null)
            return;
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
            reset();
            return;
        }
        if (mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) && onlyblocks.getValue()) {
            reset();
            return;
        }
        if ((mc.thePlayer.onGround || !onlyground.getValue()) &&
                (getBlockUnderPlayer() instanceof BlockAir) &&
                (Natives.IsKeyDown(KeyCodeMapper.mapLWJGLKeyCode(mc.gameSettings.keyBindBack.getKeyCode())) || !onlybackwards.getValue())
        ) {
            setSneak(true);
        } else if ((Natives.IsKeyDown(KeyCodeMapper.mapLWJGLKeyCode(mc.gameSettings.keyBindBack.getKeyCode())) || !onlybackwards.getValue()))
            reset();
        else if (Natives.IsKeyDown(KeyCodeMapper.mapLWJGLKeyCode(mc.gameSettings.keyBindForward.getKeyCode()))) reset();
    }

    private void setSneak(boolean sneak) {
        Natives.SetKeyBoard(KeyCodeMapper.mapLWJGLKeyCode(mc.gameSettings.keyBindSneak.getKeyCode()), sneak);
    }

    private void reset() {
        if (!Natives.IsKeyDown(KeyCodeMapper.mapLWJGLKeyCode(mc.gameSettings.keyBindSneak.getKeyCode())))
            setSneak(false);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        reset();
        super.onDisable();
    }
}
