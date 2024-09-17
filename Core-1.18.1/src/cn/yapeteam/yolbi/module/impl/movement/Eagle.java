package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class Eagle extends Module {
    private final BooleanValue autoBuild = new BooleanValue("AutoBuild", true);

    public Eagle() {
        super("Eagle", ModuleCategory.MOVEMENT, 71);
        addValues(autoBuild);
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
            if (mc.player.isOnGround()) {
                KeyMapping.set(mc.options.keyShift.getKey(), true);
                if (mc.player.getMainHandItem().getItem() instanceof BlockItem
                        || mc.player.getOffhandItem().getItem() instanceof BlockItem && this.autoBuild.getValue()) {
                    BlockHitResult hitResult = (BlockHitResult) mc.hitResult;
                    if (hitResult != null && hitResult.getType() == Type.BLOCK) {
                        BlockPos blockPos = hitResult.getBlockPos().relative(hitResult.getDirection());
                        if (mc.level != null) {
                            BlockState state = mc.level.getBlockState(blockPos);
                            if (state.getBlock() instanceof AirBlock) {
                                KeyMapping.set(mc.options.keyUse.getKey(), true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.setShiftKeyDown(false);
            KeyMapping.set(mc.options.keyUse.getKey(), false);
        }
    }

    @Override
    public void onDisable() {
        KeyMapping.set(mc.options.keyShift.getKey(), false);
        KeyMapping.set(mc.options.keyUse.getKey(), false);
    }
}
