package cn.yapeteam.yolbi.utils.misc;

import cn.yapeteam.yolbi.utils.IMinecraft;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public final class InventoryUtils2 implements IMinecraft {
    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;
    private static final Set<MobEffect> GOOD_STATUS_EFFECTS = new HashSet<>(
            Set.of(
                    MobEffects.MOVEMENT_SPEED,
                    MobEffects.HEAL,
                    MobEffects.DAMAGE_BOOST,
                    MobEffects.JUMP,
                    MobEffects.REGENERATION,
                    MobEffects.DAMAGE_RESISTANCE,
                    MobEffects.FIRE_RESISTANCE,
                    MobEffects.WATER_BREATHING,
                    MobEffects.NIGHT_VISION,
                    MobEffects.HEALTH_BOOST,
                    MobEffects.ABSORPTION,
                    MobEffects.SATURATION,
                    MobEffects.LUCK,
                    MobEffects.SLOW_FALLING,
                    MobEffects.CONDUIT_POWER,
                    MobEffects.DOLPHINS_GRACE,
                    MobEffects.HERO_OF_THE_VILLAGE,
                    MobEffects.INVISIBILITY
            )
    );

    public static boolean isBestCrossBow( ItemStack itemStack) {
        double bestBowDmg = -1.0;
            if (itemStack.getItem() instanceof CrossbowItem) {
                double damage = getCrossBowDamage(itemStack);
                for(int i=0;i<=mc.player.getInventory().items.size();i++){
                    if (damage > bestBowDmg) {
                        bestBowDmg = damage;
                    }else{
                        return false;
                    }
                }

            }
        return true;
    }



    public static double getCrossBowDamage(ItemStack stack) {
        double damage = 0.0;
        if (stack.getItem() instanceof CrossbowItem && stack.hasFoil()) {
            damage += (double)getLevel(Enchantments.POWER_ARROWS, stack);
        }

        return damage;
    }






    public static boolean isBestSword( ItemStack itemStack) {
        double damage = 0.0;
        for (int i=0;i<=mc.player.getInventory().items.size();i++) {
            if (itemStack.getItem() instanceof SwordItem) {
                double newDamage = getItemDamage(itemStack);
                if (newDamage > damage) {
                    damage = newDamage;
                }else{
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isBestArmor(ItemStack itemStack) {
        ArmorItem itemArmor = (ArmorItem)itemStack.getItem();
        double reduction = 0.0;
        for (int i=0;i<=mc.player.getInventory().items.size();i++) {
            if (!itemStack.isEmpty()) {
                Item newReduction = itemStack.getItem();
                if (newReduction instanceof ArmorItem) {
                    ArmorItem stackArmor = (ArmorItem)newReduction;
                    if (stackArmor.getSlot().getFilterFlag() - 1 == itemArmor.getSlot().getFilterFlag() - 1) {
                        if (itemArmor.getDefense() > reduction) {
                            reduction = itemArmor.getDefense();
                        }else{
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public static double getDamageReduction(ItemStack stack) {
        double reduction = 0.0;
        if (!(stack.getItem() instanceof ArmorItem)) {
            return 0.0;
        } else {
            ArmorItem armor = (ArmorItem)stack.getItem();
            reduction += (double)armor.getDefense();
            if (stack.hasFoil()) {
                reduction += (double)getLevel(Enchantments.ALL_DAMAGE_PROTECTION, stack) * 0.25;
            }

            return reduction;
        }
    }

    public static double getItemDamage(ItemStack stack) {
        double damage = 0.0;
        Multimap<Attribute, AttributeModifier> attributeModifierMap = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);

        for (Attribute attributeName : attributeModifierMap.keySet()) {
            if (attributeName.getDescriptionId().equals("attribute.name.generic.attack_damage")) {
                Iterator<AttributeModifier> attributeModifiers = attributeModifierMap.get(attributeName).iterator();
                if (attributeModifiers.hasNext()) {
                    damage += attributeModifiers.next().getAmount();
                }
                break;
            }
        }

        if (stack.hasFoil()) {
            damage += (double)EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
            damage += (double)EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, stack) * 1.25;
        }

        return damage;
    }

    private static int getLevel(Enchantment registryKey, ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(registryKey, itemStack);
    }

    public static int getToolType(ItemStack stack) {
        DiggerItem tool = (DiggerItem)stack.getItem();
        if (tool instanceof PickaxeItem) {
            return 0;
        } else {
            return tool instanceof AxeItem ? 1 : -1;
        }
    }

    public static List<ItemStack> getItemStacks(Player player) {
        List<ItemStack> result = new ArrayList<>();

        for (Slot slot : player.inventoryMenu.slots) {
            if (!slot.getItem().isEmpty()) {
                result.add(slot.getItem());
            }
        }

        return result;
    }

    public static float getPlayerArmorScore(Player player) {
        float score = 0.0F;

        for (int armorSlot = 5; armorSlot < 9; armorSlot++) {
            ItemStack stack = player.inventoryMenu.getSlot(armorSlot).getItem();
            if (stack != null) {
                score += (float)getDamageReduction(stack);
            }
        }

        return score;
    }

    public static boolean isArmorBetter(Player player) {
        return getPlayerArmorScore(player) < getPlayerArmorScore(mc.player);
    }


    private InventoryUtils2() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static enum BlockAction {
        PLACE,
        REPLACE,
        PLACE_ON;
    }

    private static class Tool {
        private final int slot;
        private final double efficiency;
        private final ItemStack stack;

        public Tool(int slot, double efficiency, ItemStack stack) {
            this.slot = slot;
            this.efficiency = efficiency;
            this.stack = stack;
        }

        public int getSlot() {
            return this.slot;
        }

        public double getEfficiency() {
            return this.efficiency;
        }

        public ItemStack getItem() {
            return this.stack;
        }
    }
}