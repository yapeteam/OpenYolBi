package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class YolBiTelly extends Module {
    public YolBiTelly() {
        super("YolBiTelly",ModuleCategory.COMBAT,InputConstants.KEY_R);
        addValues();
    }
    public static int findBlock() {
        if (mc.player != null) {
            for (int i = 0; i < mc.player.getInventory().items.size(); i++) {
                ItemStack stack = mc.player.getInventory().items.get(i);
                if (stack.getItem() instanceof BlockItem blockItem) {
                    return i;
                }
            }
        }
        return -1;
    }
    public static boolean selectBlock() {
        int slot = findBlock();
        if (slot != -1) {
            mc.player.getInventory().selected = slot; // 设置当前选中槽位
            return true;
        }

        return false; // 未找到任何方块
    }
    public float MathYaw(){
        if (mc.player == null) {
            return -1111111;
        }
        float yaw = mc.player.getYRot() + 180;
        float delta = yaw % 45;
        if (delta > 22.5 && delta <= 45)
            yaw += 45 - delta;
        else if (delta < -22.5 && delta >= -45)
            yaw -= 45 + delta;
        else if (delta <= 22.5 && delta > 0)
            yaw -= delta;
        else if (delta >= -22.5 && delta < 0)
            yaw -= delta;
        return yaw;
    }
    @Listener
    public void aim(EventRender2D e){
        if(mc.player==null){
            return;
        }
        PoseStack ps = e.poseStack();
        Boolean Block = selectBlock();
        float y = MathYaw();
        if(y==-1111111){
            return;
        }
        mc.player.setYRot(y);
        mc.player.setXRot(38);
    }
}
