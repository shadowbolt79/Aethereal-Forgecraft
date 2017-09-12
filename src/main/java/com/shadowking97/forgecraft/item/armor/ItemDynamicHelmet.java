package com.shadowking97.forgecraft.item.armor;

import com.shadowking97.forgecraft.item.ItemBaseArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Stores components to get custom stats and rendering for armor
 *
 * Itemstack created automatically when components are added to the Fitting Stand
 *
 * TODO: Yes, everything. Complete after Item Components are working properly.
 * Created by Shadow Bolt on 7/22/2017.
 */
public class ItemDynamicHelmet extends ItemBaseArmor {
    public ItemDynamicHelmet(String name) {
        super(name, EntityEquipmentSlot.HEAD);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
    }
}
