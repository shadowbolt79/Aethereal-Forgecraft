package com.shadowking97.forgecraft.item.tool;

import com.shadowking97.forgecraft.interfaces.IHammerable;
import com.shadowking97.forgecraft.item.ItemBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Shadow Bolt on 8/4/2017.
 */
public class ItemHammer extends ItemBase {
    public static final Random random = new Random();

    public ItemHammer(String name) {
        super(name);
        this.setMaxStackSize(1);
        this.setMaxDamage(1000);

    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        if(state.getBlock() instanceof IHammerable)
        {
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound compound = stack.getTagCompound();
            if(!stack.hasTagCompound()) {
                compound = new NBTTagCompound();
                compound.setLong("lastUsed",0);
            }

            if(compound.getLong("lastUsed")<=worldIn.getTotalWorldTime()-(worldIn.isRemote?28:30)) {

                if(worldIn.isRemote) {
                    ((WorldClient) worldIn).playSound(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.05f, itemRand.nextFloat() * 0.25f + 0.75f, false);
                    for(int k = 0; k < 3; k++)
                        worldIn.spawnParticle(EnumParticleTypes.CRIT,
                                pos.getX()+0.5,pos.getY()+1,pos.getZ()+0.5,
                                random.nextDouble()*0.3-0.15,random.nextDouble()*0.15,random.nextDouble()*0.3-0.15);
                }
                ((IHammerable) state.getBlock()).useHammer(stack, player, pos);
                compound.setLong("lastUsed", worldIn.getTotalWorldTime());
                stack.setTagCompound(compound);


                return EnumActionResult.SUCCESS;

            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(IBlockState blockIn)
    {
        return false;
    }

}
