package com.shadowking97.forgecraft.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;

/**
 * Used by blocks to enable players to use the hammer on them. May switch to capabilities.
 * Created by Shadow Bolt on 7/26/2017.
 */
public interface IHammerable {
    void useHammer(ItemStack hammer, EntityPlayer player, BlockPos pos);
}
