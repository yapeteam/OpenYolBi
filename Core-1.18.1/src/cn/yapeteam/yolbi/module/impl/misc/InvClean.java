package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.InventoryUtils2;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;

import java.util.concurrent.TimeUnit;

public class InvClean extends Module {
    public InvClean(){
        super("InvCleanGrim", ModuleCategory.MISC, InputConstants.KEY_B);
    }
    @Listener
    public void onUpdate(EventRender2D e){
        if(mc.player==null||mc.player.getInventory().isEmpty()){
            return;
        }
       int size =  mc.player.getInventory().items.size();
        if(size == 0){
            return;
        }
        int block = findBlock();
        if(block == -1){
            return;
        }
        for(int i=0;i<=size;i++){
                if(!isItemUseful(i)&&!mc.player.getInventory().items.get(i).isEmpty()){
                    dropItem(i);
                    try{
                        TimeUnit.MILLISECONDS.sleep(20);
                    }catch (Throwable e2){
                        YolBi.information("TIMER_err error in InvClean!");
                    }

                }
        }
    }
    private int findBlock(){
        if(mc.player!=null){
            int num = 0;
            for(int i=0;i<=mc.player.getInventory().items.size();i++){
                if (mc.screen instanceof InventoryScreen) {
                    if(!isItemUseful(i)&&!mc.player.getInventory().items.get(i).isEmpty()){
                        if(mc.player.getInventory().items.get(i).getItem() instanceof BlockItem){
                            num+=1;
                        }
                    }
                }
            }
            return num;
        }
        return -1;
    }
    private boolean dropItem(int i) {

        mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, i, 1, ClickType.THROW, mc.player);
        mc.getConnection().send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        return true;
    }
    private boolean isItemUseful( int i) {
        ItemStack itemStack = mc.player.getInventory().items.get(i);
        Item item = itemStack.getItem();
        if (item instanceof AxeItem || item instanceof PickaxeItem) {
            return true;
        } else if (itemStack.getItem().getFoodProperties() != null) {
            return true;
        } else if (item instanceof BowItem  && InventoryUtils2.isBestCrossBow(itemStack))  {
            return true;
        } else if (item == Items.GOLDEN_APPLE) {
            return true;
        }
        if (item instanceof PotionItem) {
            return true;
        } else if (item instanceof SwordItem && InventoryUtils2.isBestSword(itemStack)) {
            return true;
        } else if (item instanceof ArmorItem && InventoryUtils2.isBestArmor(itemStack)) {
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
}
