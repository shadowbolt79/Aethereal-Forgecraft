package com.shadowking97.forgecraft.item.tool;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.item.ItemBase;
import com.shadowking97.forgecraft.item.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Shadow Bolt on 7/30/2017.
 */
public class ItemTongs extends ItemBase {

    public ItemTongs(String name) {
        super(name);

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(stack.getMetadata()==1){
            //molten metal
            NBTTagCompound compound = stack.getTagCompound();
            if(compound!=null)
                tooltip.add("Molten "+compound.getString("material")+" ("+compound.getInteger("amount")+")");
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if(!player.world.isRemote && stack.getMetadata()==0)
        {
            if(entity instanceof EntityItem)
            {
                EntityItem item = (EntityItem)entity;
                if(item.getItem().getItem()== ModItems.moltenMetal)
                {
                    NBTTagCompound compound = item.getItem().getTagCompound();
                    stack.setItemDamage(1);
                    stack.setTagCompound(compound);
                    entity.setDead();
                    return true;
                }
            }
        }
        else if(!player.world.isRemote && stack.getMetadata()==1)
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