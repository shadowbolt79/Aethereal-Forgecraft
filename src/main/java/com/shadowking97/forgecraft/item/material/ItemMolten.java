package com.shadowking97.forgecraft.item.material;

import com.shadowking97.forgecraft.interfaces.IHammerableItem;
import com.shadowking97.forgecraft.item.ItemBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO: Add item cooling requiring resmelting
 * Created by Shadow Bolt on 8/4/2017.
 */
public class ItemMolten extends ItemBase implements IHammerableItem {
    private final static Random random = new Random();

    public ItemMolten(String name) {
        super(name);
        setMaxStackSize(1);
    }

    @Nullable
    public static ItemMaterial getMaterial(ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound==null)return null;
        return MaterialStore.INSTANCE.getMaterialByName(compound.getString("material"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound==null)
            return super.getItemStackDisplayName(stack);
        return "Molten "+ MaterialStore.INSTANCE.getMaterialByName("material").getName();
    }

    public static int getAmount(ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound==null)return 0;
        return compound.getInteger("amount");
    }

    @Override
    public boolean onUseHammer(ItemStack stack, BlockPos pos, World inWorld)
    {
        if(inWorld.isRemote)return false;
        NBTTagCompound compound = stack.getTagCompound();

        if(compound==null){
            stack.setCount(0);
            return false;
        }

        ItemMaterial material = MaterialStore.INSTANCE.getMaterialByName(compound.getString("material"));
        int amountLeft = compound.getInteger("amount");
        if(material==null||amountLeft<=0){
            stack.setCount(0);
            return false;
        }

        int toDrop = random.nextInt(48)+9;
        if(toDrop>amountLeft) {
            toDrop = amountLeft;
            amountLeft=0;
            stack.setCount(0);
        }
        else
            compound.setInteger("amount",amountLeft-toDrop);


        ArrayList<ItemMaterialDefinition> definitionList = MaterialStore.INSTANCE.getItemsForMaterial(material);

        for(ItemMaterialDefinition definition: definitionList)
        {
            int numMaterial = 0;
            while(toDrop>=definition.getValue()) {
                ++numMaterial;
                toDrop-=definition.getValue();
            }
            if(numMaterial>0)
            {
                ItemStack stack1 = definition.getItemStack().copy();
                stack1.setCount(numMaterial);
                EntityItem item = new EntityItem(inWorld, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, stack1);
                item.setVelocity(random.nextDouble()*0.1-0.05,random.nextDouble()*0.3,random.nextDouble()*0.1-0.05);
                inWorld.spawnEntity(item);
            }
        }

        if(amountLeft!=0)
        {
            stack.setTagCompound(compound);
        }
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        //molten metal
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null)
            tooltip.add("Amount: " + compound.getInteger("amount"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if(worldIn.isRemote)return;
        if(entityIn instanceof EntityLivingBase)
        {
            EntityLivingBase theEntity = (EntityLivingBase)entityIn;
            if(!theEntity.isImmuneToFire())
                theEntity.setFire(1);
        }
    }
}
