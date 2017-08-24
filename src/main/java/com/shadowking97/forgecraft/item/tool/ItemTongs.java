package com.shadowking97.forgecraft.item.tool;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.block.Bloomery;
import com.shadowking97.forgecraft.block.ModBlocks;
import com.shadowking97.forgecraft.item.ItemBase;
import com.shadowking97.forgecraft.item.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

/**
 * Created by Shadow Bolt on 7/30/2017.
 */
public class ItemTongs extends ItemBase {

    public ItemTongs(String name) {
        super(name);

    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if(stack.getMetadata()==1){
            //molten metal
            NBTTagCompound compound = stack.getTagCompound();
            if(compound!=null)
                tooltip.add("Molten "+compound.getString("material")+" ("+compound.getInteger("amount")+")");
        }
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if(!player.worldObj.isRemote && stack.getMetadata()==0)
        {
            if(entity instanceof EntityItem)
            {
                EntityItem item = (EntityItem)entity;

                if(item.getEntityItem().getItem()== ModItems.moltenMetal)
                {
                    NBTTagCompound compound = item.getEntityItem().getTagCompound();
                    stack.setItemDamage(1);
                    stack.setTagCompound(compound);
                    entity.setDead();
                    return true;
                }
            }
        }
        else if(!player.worldObj.isRemote && stack.getMetadata()==1)
        {
            if(entity instanceof EntityLiving){
                entity.setFire(2);
                NBTTagCompound compound = stack.getTagCompound();
                int amount = compound.getInteger("amount")-itemRand.nextInt(6);
                if(amount>0)
                {
                    compound.setInteger("amount",amount);
                    stack.setTagCompound(compound);
                }
                else
                {
                    stack.setItemDamage(0);
                    stack.setTagCompound(null);
                }
                return true;
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void registerItemModel(Item item) {
        Forgecraft.proxy.registerItemRenderer(this,0,name+"Closed");
        Forgecraft.proxy.registerItemRenderer(this, 1, name+"Molten");
    }
}