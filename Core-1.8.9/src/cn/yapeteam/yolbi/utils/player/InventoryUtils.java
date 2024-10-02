package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtils implements IMinecraft {
    public static int getItemDurability(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static Item getHeldItem() {
        if (mc.thePlayer == null || mc.thePlayer.getCurrentEquippedItem() == null) return null;
        return mc.thePlayer.getCurrentEquippedItem().getItem();
    }
}
