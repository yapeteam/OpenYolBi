package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.managers.ReflectionManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SlotHandler extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Default", "Default", "Silent");

    private final NumberValue<Integer> maxdelay = new NumberValue<>("Max Delay", 100, 0, 1000, 1);
    private final NumberValue<Integer> mindelay = new NumberValue<>("Min Delay", 50, 0, 1000, 1);

    private final NumberValue<Integer> maxswitchbackdelay = new NumberValue<>("Max Switch Back Delay", 100, 0, 1000, 1);
    private final NumberValue<Integer> minswitchbackdelay = new NumberValue<>("Min Switch Back Delay", 50, 0, 1000, 1);

    private static @Nullable Integer currentSlot = null;
    private static long lastSetCurrentSlotTime = -1;

    public SlotHandler() {
        super("SlotHandler", ModuleCategory.PLAYER);
        NumberValue.setBound(mindelay, maxdelay);
        NumberValue.setBound(minswitchbackdelay, maxswitchbackdelay);
        addValues(mode, maxdelay, mindelay, maxswitchbackdelay, minswitchbackdelay);
    }

    public static int getCurrentSlot() {
        if (currentSlot != null)
            return currentSlot;
        return mc.thePlayer.inventory.currentItem;
    }

    public static @Nullable ItemStack getHeldItem() {
        final InventoryPlayer inventory = mc.thePlayer.inventory;
        if (currentSlot != null)
            return currentSlot < 9 && currentSlot >= 0 ? inventory.mainInventory[currentSlot] : null;
        return getRenderHeldItem();
    }

    public static @Nullable ItemStack getRenderHeldItem() {
        final InventoryPlayer inventory = mc.thePlayer.inventory;
        return inventory.currentItem < 9 && inventory.currentItem >= 0 ? inventory.mainInventory[inventory.currentItem] : null;
    }

    public static void setCurrentSlot(int slot) {
        if (slot != -1) {
            currentSlot = slot;
            lastSetCurrentSlotTime = System.currentTimeMillis();
        }
    }

    @Listener
    public void onPreUpdate(EventUpdate eventUpdate) {
        switch (mode.getValue()) {
            case "Default":
                mc.thePlayer.inventory.currentItem = getCurrentSlot();
                currentSlot = null;
                break;
            case "Silent":
                if (currentSlot != null
                        && !(ReflectionManager.PlayerControllerMP$isHittingBlock(mc.playerController))
                        && System.currentTimeMillis() - lastSetCurrentSlotTime > maxswitchbackdelay.getValue()) {
                    currentSlot = null;
                }
                break;
        }
    }
}
