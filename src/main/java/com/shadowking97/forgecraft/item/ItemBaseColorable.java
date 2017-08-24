package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * Created by Shadow Bolt on 7/18/2017.
 *
 * Uses a material to color items correctly
 */
public class ItemBaseColorable extends ItemBase{
    protected ItemMaterial material;

    public ItemBaseColorable(String name) {
        super(name);
    }

    @Override
    public ItemBaseColorable setCreativeTab(CreativeTabs tab){
        super.setCreativeTab(tab);
        return this;
    }

    public void setMaterial(ItemMaterial material)
    {
        this.material = material;
        Forgecraft.proxy.registerItemColor(this);
    }

    public ItemMaterial getMaterial()
    {
        return material;
    }
}
