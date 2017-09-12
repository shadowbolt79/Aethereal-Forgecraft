package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.Forgecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Shadow Bolt on 7/16/2017.
 */
public class ItemBase extends Item implements ItemModelProvider {
    protected String name;

    public ItemBase(String name)
    {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
    }

    @Override
    public void registerItemModel(Item item) {
        Forgecraft.proxy.registerItemRenderer(this,0,name);
    }

    @Override
    public ItemBase setCreativeTab(CreativeTabs tab){
        super.setCreativeTab(tab);
        return this;
    }
}
