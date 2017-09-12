package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.Forgecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Created by Shadow Bolt on 7/25/2017.
 */
public class ItemBaseMultiColored extends ItemBaseColorable {

    public ItemBaseMultiColored(String name) {
        super(name);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < 16; ++i) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public void registerItemModel(Item item) {
        for(int i = 0; i < 16; i++)
            Forgecraft.proxy.registerItemRenderer(this,i,name);
    }


    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(i).getUnlocalizedName();
    }
}
