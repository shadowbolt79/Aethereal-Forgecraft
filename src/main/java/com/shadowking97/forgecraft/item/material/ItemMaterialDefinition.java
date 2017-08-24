package com.shadowking97.forgecraft.item.material;

import net.minecraft.item.ItemStack;

/**
 * Created by Shadow Bolt on 7/30/2017.
 * Defines an item to a material
 */
public class ItemMaterialDefinition {
    private final ItemMaterial material;
    private final ItemStack item;
    private final int value;
    private final int hashcode;

    public ItemMaterialDefinition(ItemStack itemStack,ItemMaterial material, int value) throws Exception
    {
        if(itemStack==null)
            throw new AssertionError(itemStack);
        if(value<=0)
            throw new Exception("Item "+itemStack.toString()+" for material "+material.getName()+" mut have a value greater than 0!");
        item = itemStack;
        hashcode = (itemStack.getUnlocalizedName()+"@"+itemStack.getMetadata()).hashCode();
        this.material=material;
        this.value=value;
    }

    public ItemMaterial getMaterial() {return material;}
    public int getValue() {return value;}

    @Override
    public int hashCode() {
        return hashcode;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof ItemStack)
        {
            ItemStack stack = (ItemStack)o;
            return hashcode == (stack.getUnlocalizedName()+"@"+stack.getMetadata()).hashCode();
        }
        if(o instanceof ItemMaterial)
        {
            return material.equals(o);
        }
        return false;
    }

    public ItemStack getItemStack()
    {
        return item.copy();
    }
}
