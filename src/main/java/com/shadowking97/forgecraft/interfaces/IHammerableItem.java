package com.shadowking97.forgecraft.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Used by items inside of blocks to register being hit by a hammer.
 * Created by Shadow Bolt on 8/4/2017.
 */
public interface IHammerableItem {
    boolean onUseHammer(ItemStack stack, BlockPos pos, World inWorld);
}
