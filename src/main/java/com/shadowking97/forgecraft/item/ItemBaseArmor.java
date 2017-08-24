package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.Forgecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

/**
 * Created by Shadow Bolt on 8/15/2017.
 */
public class ItemBaseArmor extends ItemArmor implements ItemModelProvider{
    protected String name;

    public ItemBaseArmor(String name, EntityEquipmentSlot equipmentSlotIn) {
        super(ArmorMaterial.IRON, -1, equipmentSlotIn);
        this.name = name;

        setUnlocalizedName(name);
        setRegistryName(name);
    }

    @Override
    public void registerItemModel(Item item) {
        Forgecraft.proxy.registerItemRenderer(this,0,name);
    }

    @Override
    public ItemBaseArmor setCreativeTab(CreativeTabs tab){
        super.setCreativeTab(tab);
        return this;
    }
}
