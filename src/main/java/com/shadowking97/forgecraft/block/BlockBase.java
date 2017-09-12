package com.shadowking97.forgecraft.block;

import com.shadowking97.forgecraft.Forgecraft;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Ease of life class to automatically do things for different blocks
 * Created by Shadow Bolt on 7/17/2017.
 */
public class BlockBase extends Block implements BlockModelProvider {
    protected String name;

    public BlockBase(Material materialIn, String name) {
        super(materialIn);
        this.name = name;

        setUnlocalizedName(name);
        setRegistryName(name);
    }

    @Override
    public void registerBlockItem() {
        ItemBlock item = new ItemBlock(this);
        item.setRegistryName(name);
        ForgeRegistries.ITEMS.register(item);
        Forgecraft.proxy.registerBlockRenderer(item,0,name);
    }

    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

}
