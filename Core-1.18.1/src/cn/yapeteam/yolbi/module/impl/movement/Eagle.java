package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;

public class Eagle extends Module {
    public Eagle() {
        super("Eagle", ModuleCategory.MOVEMENT);
    }

    public static Block getBlock(BlockPos pos) {
        if (mc.level != null) {
            return mc.level.getBlockState(pos).getBlock();
        }
        return null;
    }

    public static Block getBlockUnderPlayer(Player player) {
        return getBlock(new BlockPos(player.getX(), player.getY() - 1.0, player.getZ()));
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.player != null && getBlockUnderPlayer(mc.player) instanceof AirBlock) {
            if (mc.player.isOnGround())
                KeyMapping.set(mc.options.keyShift.getKey(), true);
        } else if (!Natives.IsKeyDown(VirtualKeyBoard.VK_LSHIFT))
            KeyMapping.set(mc.options.keyShift.getKey(), false);
    }

    @Override
    public void onEnable() {
        if (mc.player != null)
            mc.player.setShiftKeyDown(false);
    }

    @Override
    public void onDisable() {
        KeyMapping.set(mc.options.keyShift.getKey(), false);
    }
}
