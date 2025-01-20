package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvClean extends Module {
    private final TimerUtil timer = new TimerUtil();
    private final int[] bestArmorPieces = new int[6];
    private final List<Integer> trash = new ArrayList<>();
    private final int[] bestToolSlots = new int[2];
    private final List<Integer> gappleStackSlots = new ArrayList<>();
    private int bestSwordSlot = -1;
    private int bestPearlSlot = -1;
    private int bestBowSlot = -1;
    private int bestWaterSlot = -1;
    private int ticksSinceLastClick;
    private boolean nextTickCloseInventory;
    private boolean serverOpen;
    private boolean clientOpen;

    public InvClean() {
        super("InvCleaner", ModuleCategory.PLAYER, 66);
    }

    @Override
    public void onEnable() {
        this.ticksSinceLastClick = 0;
        this.clientOpen = mc.screen instanceof InventoryScreen;
        this.serverOpen = this.clientOpen;
    }

    @Override
    public void onDisable() {
        clearInventory();
    }

    @Listener(value = Priority.NORMAL)
    public void onMotion(EventMotion event) {
        if (mc.player == null || mc.player.isSpectator()) return;
        
        if (!mc.player.isUsingItem()) {
            this.ticksSinceLastClick++;
            if (this.ticksSinceLastClick < 5 / 50.0) {
                return;
            }

            if (mc.screen instanceof InventoryScreen) {
                clearInventory();
                scanInventory();
                
                if (timer.hasTimePassed(20) && equipArmor(true)) {
                    return;
                }

                if (dropItem(this.trash)) {
                    return;
                }

                sortItemsToFixedSlots();
            }
        }
    }

    private void scanInventory() {
        for (int slot = 5; slot < 45; slot++) {
            if (!mc.player.containerMenu.slots.get(slot).hasItem()) continue;
            
            ItemStack stack = mc.player.containerMenu.slots.get(slot).getItem();
            Item item = stack.getItem();
            
            if (item instanceof SwordItem) {
                if (isBetterSword(stack, bestSwordSlot)) {
                    if (bestSwordSlot != -1) {
                        trash.add(bestSwordSlot);
                    }
                    this.bestSwordSlot = slot;
                } else {
                    trash.add(slot);
                }
            } else if (item instanceof DiggerItem) {
                int toolType = getToolType(stack);
                if (toolType != -1) {
                    if (isBetterTool(stack, bestToolSlots[toolType])) {
                        if (bestToolSlots[toolType] != -1) {
                            trash.add(bestToolSlots[toolType]);
                        }
                        this.bestToolSlots[toolType] = slot;
                    } else {
                        trash.add(slot);
                    }
                }
            } else if (item instanceof ArmorItem) {
                handleArmorItem(stack, slot);
            } else if (isUsefulItem(stack)) {
                handleUsefulItem(stack, slot);
            } else if (!this.trash.contains(slot)) {
                this.trash.add(slot);
            }
        }
    }

    private void handleArmorItem(ItemStack stack, int slot) {
        ArmorItem armor = (ArmorItem)stack.getItem();
        int index = armor.getSlot().ordinal();
        if (index >= 1 && index < this.bestArmorPieces.length + 2) {
            if (isBetterArmor(stack, bestArmorPieces[index])) {
                if (bestArmorPieces[index] != -1) {
                    trash.add(bestArmorPieces[index]);
                }
                this.bestArmorPieces[index] = slot;
            } else {
                trash.add(slot);
            }
        }
    }

    private boolean isUsefulItem(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.GOLDEN_APPLE ||
               item == Items.ENDER_PEARL ||
               item == Items.WATER_BUCKET ||
               item == Items.TOTEM_OF_UNDYING ||
               item instanceof BowItem ||
               item instanceof BlockItem;
    }

    private void handleUsefulItem(ItemStack stack, int slot) {
        Item item = stack.getItem();
        if (item == Items.GOLDEN_APPLE) {
            this.gappleStackSlots.add(slot);
        } else if (item == Items.ENDER_PEARL) {
            this.bestPearlSlot = slot;
        } else if (item == Items.WATER_BUCKET) {
            this.bestWaterSlot = slot;
        } else if (item instanceof BowItem) {
            this.bestBowSlot = slot;
        }
    }

    private int getToolType(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof PickaxeItem) return 0;
        if (item instanceof AxeItem) return 1;
        return -1;
    }

    private void sortItemsToFixedSlots() {
        if (bestSwordSlot != -1 && bestSwordSlot != 36) {
            putItemInSlot(36, bestSwordSlot);
        }
        if (bestToolSlots[0] != -1 && bestToolSlots[0] != 37) {
            putItemInSlot(37, bestToolSlots[0]);
        }
        if (bestToolSlots[1] != -1 && bestToolSlots[1] != 38) {
            putItemInSlot(38, bestToolSlots[1]);
        }
        if (!gappleStackSlots.isEmpty()) {
            int bestGappleSlot = gappleStackSlots.get(0);
            if (bestGappleSlot != 39) {
                putItemInSlot(39, bestGappleSlot);
            }
        }
        if (bestWaterSlot != -1 && bestWaterSlot != 40) {
            putItemInSlot(40, bestWaterSlot);
        }
        if (bestBowSlot != -1 && bestBowSlot != 41) {
            putItemInSlot(41, bestBowSlot);
        }
        if (bestPearlSlot != -1 && bestPearlSlot != 43) {
            putItemInSlot(43, bestPearlSlot);
        }
    }

    private boolean equipArmor(boolean moveItems) {
        if (!timer.hasTimePassed(20)) {
            return false;
        }
        
        for (int i = 0; i < bestArmorPieces.length; i++) {
            int newArmorSlot = bestArmorPieces[i];
            if (newArmorSlot != -1) {
                int currentArmorSlot = getArmorSlot(EquipmentSlot.values()[i]);
                if (currentArmorSlot >= 0 && currentArmorSlot < mc.player.containerMenu.slots.size()) {
                    ItemStack currentArmor = mc.player.containerMenu.slots.get(currentArmorSlot).getItem();
                    ItemStack newArmor = mc.player.containerMenu.slots.get(newArmorSlot).getItem();
                    
                    if (currentArmor.isEmpty()) {
                        if (moveItems) {
                            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, newArmorSlot, 0, ClickType.QUICK_MOVE, mc.player);
                        }
                        timer.reset();
                        return true;
                    } 
                    else if (currentArmor.getItem() instanceof ArmorItem && isBetterArmor(newArmor, currentArmorSlot)) {
                        if (moveItems) {
                            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, currentArmorSlot, 0, ClickType.QUICK_MOVE, mc.player);
                            timer.reset();
                            if (!timer.hasTimePassed(20)) return true;
                            
                            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, newArmorSlot, 0, ClickType.QUICK_MOVE, mc.player);
                            timer.reset();
                            if (!timer.hasTimePassed(20)) return true;
                            
                            for (int slot = 9; slot < 45; slot++) {
                                ItemStack stack = mc.player.containerMenu.slots.get(slot).getItem();
                                if (stack.equals(currentArmor)) {
                                    trash.add(slot);
                                    break;
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getArmorSlot(EquipmentSlot slot) {
        switch (slot) {
            case HEAD: return 5;
            case CHEST: return 6;
            case LEGS: return 7;
            case FEET: return 8;
            default: return -1;
        }
    }

    private void putItemInSlot(int slot, int slotIn) {
        mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, slotIn, slot - 36, ClickType.SWAP, mc.player);
    }

    private boolean dropItem(List<Integer> listOfSlots) {
        if (!listOfSlots.isEmpty()) {
            int slot = listOfSlots.remove(0);
            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, slot, 1, ClickType.THROW, mc.player);
            return true;
        }
        return false;
    }

    private void clearInventory() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.bestWaterSlot = -1;
        this.bestPearlSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }

    private boolean isBetterSword(ItemStack newSword, int currentSlot) {
        if (currentSlot == -1) return true;
        ItemStack currentSword = mc.player.containerMenu.slots.get(currentSlot).getItem();
        
        float newDamage = ((SwordItem)newSword.getItem()).getDamage();
        float currentDamage = ((SwordItem)currentSword.getItem()).getDamage();
        
        int newSharpness = getEnchantmentLevel(newSword, Enchantments.SHARPNESS);
        int currentSharpness = getEnchantmentLevel(currentSword, Enchantments.SHARPNESS);
        
        int newFire = getEnchantmentLevel(newSword, Enchantments.FIRE_ASPECT);
        int currentFire = getEnchantmentLevel(currentSword, Enchantments.FIRE_ASPECT);
        
        int newKnockback = getEnchantmentLevel(newSword, Enchantments.KNOCKBACK);
        int currentKnockback = getEnchantmentLevel(currentSword, Enchantments.KNOCKBACK);
        
        float newScore = newDamage + newSharpness * 1.25f + newFire * 0.5f + newKnockback * 0.5f;
        float currentScore = currentDamage + currentSharpness * 1.25f + currentFire * 0.5f + currentKnockback * 0.5f;
        
        return newScore > currentScore;
    }

    private boolean isBetterTool(ItemStack newTool, int currentSlot) {
        if (currentSlot == -1) return true;
        ItemStack currentTool = mc.player.containerMenu.slots.get(currentSlot).getItem();
        
        BlockState stoneState = Blocks.STONE.defaultBlockState();
        
        float newSpeed = ((DiggerItem)newTool.getItem()).getDestroySpeed(newTool, stoneState);
        float currentSpeed = ((DiggerItem)currentTool.getItem()).getDestroySpeed(currentTool, stoneState);
        
        int newEfficiency = getEnchantmentLevel(newTool, Enchantments.BLOCK_EFFICIENCY);
        int currentEfficiency = getEnchantmentLevel(currentTool, Enchantments.BLOCK_EFFICIENCY);
        
        int newUnbreaking = getEnchantmentLevel(newTool, Enchantments.UNBREAKING);
        int currentUnbreaking = getEnchantmentLevel(currentTool, Enchantments.UNBREAKING);
        
        int newFortune = getEnchantmentLevel(newTool, Enchantments.BLOCK_FORTUNE);
        int currentFortune = getEnchantmentLevel(currentTool, Enchantments.BLOCK_FORTUNE);
        
        float newScore = newSpeed + newEfficiency * 1.5f + newUnbreaking * 0.5f + newFortune * 1.0f;
        float currentScore = currentSpeed + currentEfficiency * 1.5f + currentUnbreaking * 0.5f + currentFortune * 1.0f;
        
        return newScore > currentScore;
    }

    private boolean isBetterArmor(ItemStack newArmor, int currentSlot) {
        if (currentSlot == -1) return true;
        ItemStack currentArmor = mc.player.containerMenu.slots.get(currentSlot).getItem();
        
        ArmorItem newArmorItem = (ArmorItem)newArmor.getItem();
        ArmorItem currentArmorItem = (ArmorItem)currentArmor.getItem();
        
        float newMaterialValue = getArmorMaterialValue(newArmorItem);
        float currentMaterialValue = getArmorMaterialValue(currentArmorItem);
        
        if (newMaterialValue != currentMaterialValue) {
            return newMaterialValue > currentMaterialValue;
        }
        
        float newProtection = newArmorItem.getDefense();
        float currentProtection = currentArmorItem.getDefense();
        
        int newProt = getEnchantmentLevel(newArmor, Enchantments.ALL_DAMAGE_PROTECTION);
        int currentProt = getEnchantmentLevel(currentArmor, Enchantments.ALL_DAMAGE_PROTECTION);
        
        int newUnbreaking = getEnchantmentLevel(newArmor, Enchantments.UNBREAKING);
        int currentUnbreaking = getEnchantmentLevel(currentArmor, Enchantments.UNBREAKING);
        
        int newThorns = getEnchantmentLevel(newArmor, Enchantments.THORNS);
        int currentThorns = getEnchantmentLevel(currentArmor, Enchantments.THORNS);
        
        float newScore = newProtection + newProt * 1.5f + newUnbreaking * 0.5f + newThorns * 0.5f;
        float currentScore = currentProtection + currentProt * 1.5f + currentUnbreaking * 0.5f + currentThorns * 0.5f;
        
        return newScore > currentScore;
    }

    private float getArmorMaterialValue(ArmorItem armor) {
        String material = armor.getMaterial().getName();
        switch (material) {
            case "netherite": return 5.0f;
            case "diamond": return 4.0f;
            case "iron": return 3.0f;
            case "chainmail": return 2.5f;
            case "gold": return 2.0f;
            case "leather": return 1.0f;
            default: return 0.0f;
        }
    }

    private int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
    }
} 