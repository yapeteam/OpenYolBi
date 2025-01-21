package cn.yapeteam.yolbi.module.impl.misc;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SwordItem;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.misc.InventoryUtils;

public class ChestStealer extends Module {
    private final TimerUtil stopwatch2 = new TimerUtil();
    private final TimerUtil stopwatch = new TimerUtil();
    private NumberValue<Integer> nextClick  = new NumberValue<Integer>("NextClick",50,0,1000,2);
    private NumberValue<Integer> lastClick  = new NumberValue<Integer>("LastClick",50,0,1000,2);
    private NumberValue<Integer> lastSteal  = new NumberValue<Integer>("NextClick",50,0,1000,2);
    public ChestStealer() {
        super("ChestStealer", ModuleCategory.PLAYER, InputConstants.KEY_B);
        addValues(nextClick,lastClick,lastSteal);
    }

    @Listener(value = Priority.NORMAL)
    public void onMotion(EventMotion event) {
        if (mc.player == null || mc.player.containerMenu == null ||
                !this.stopwatch.hasTimePassed(this.nextClick.getValue())) {
            return;
        }

        if (mc.player.containerMenu instanceof ChestMenu container) {
            this.lastSteal.setValue(lastSteal.getValue()+1);
            if (this.isChestEmpty(container) &&
                    this.stopwatch2.hasTimePassed(130L)) {
                mc.player.closeContainer();
                return;
            }

            for (int i = 0; i < container.getContainer().getContainerSize(); i++) {
                if (!container.getContainer().getItem(i).isEmpty() &&
                        this.lastSteal.getValue() > 1 && this.isItemUseful(container, i)) {
                    this.nextClick.setValue(50L);
                    mc.gameMode.handleInventoryMouseClick(container.containerId, i, 0, ClickType.QUICK_MOVE, mc.player);
                    this.stopwatch.reset();
                    this.stopwatch2.reset();
                    this.lastClick.setValue(0);
                    if (this.nextClick.getValue() > 0L) {
                        return;
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(ChestMenu c) {
        for (int i = 0; i < c.getContainer().getMaxStackSize(); i++) {
            if (!c.getContainer().getItem(i).isEmpty() && this.isItemUseful(c, i)) {
                return false;
            }
        }

        return true;
    }
    private boolean isItemUseful(ChestMenu c, int i) {
        ItemStack itemStack = c.getSlot(i).getItem();
        Item item = itemStack.getItem();
        if (item instanceof AxeItem || item instanceof PickaxeItem) {
            return true;
        } else if (itemStack.getItem().getFoodProperties() != null) {
            return true;
        } else if (item instanceof BowItem || item == Items.ARROW) {
            return true;
        } else if (item == Items.GOLDEN_APPLE) {
            return true;
        }
        if (item instanceof PotionItem) {
            return true;
        } else if (item instanceof SwordItem && InventoryUtils.isBestSword(c, itemStack)) {
            return true;
        } else if (item instanceof ArmorItem && InventoryUtils.isBestArmor(c, itemStack)) {
            return true;
        } else if (item instanceof BlockItem) {
            return true;
        } else if(item == Items.SNOWBALL){
            return true;
        }else if (item == Items.SLIME_BALL) {
            return true;
        } else if (item instanceof CrossbowItem) {
            return true;
        } else if (item == Items.WATER_BUCKET) {
            return true;
        } else if (item == Items.TOTEM_OF_UNDYING) {
            return true;
        } else {
            return item == Items.FIRE_CHARGE ? true : item == Items.ENDER_PEARL;
        }
    }

    public TimerUtil getStopwatch2() {
        return this.stopwatch2;
    }

    public TimerUtil getStopwatch() {
        return this.stopwatch;
    }

    public long getNextClick() {
        return this.nextClick.getValue();
    }

    public int getLastClick() {
        return this.lastClick.getValue();
    }

    public int getLastSteal() {
        return this.lastSteal.getValue();
    }
}