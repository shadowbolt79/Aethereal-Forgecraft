package com.shadowking97.forgecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Stores blocks and registers for the mod
 * Created by Shadow Bolt on 7/27/2017.
 */
public class ModBlocks {

    public static BlockBase bloomery;
    public static BlockBase anvil;

    public static void init()
    {
        bloomery = register(new Bloomery(Material.ROCK,"bloomery"));
        anvil = register(new Anvil(Material.ANVIL,"anvil").setCreativeTab(CreativeTabs.MATERIALS));

    }

    private static <T extends Block> T register(T itemBlock){
        GameRegistry.register(itemBlock);

        if(itemBlock instanceof BlockModelProvider)
            ((BlockModelProvider)itemBlock).registerBlockItem();

        if (itemBlock instanceof BlockContainerBase)
            GameRegistry.registerTileEntity(((BlockContainerBase<?>)itemBlock).getTileEntityClass(), itemBlock.getRegistryName().toString());

        return itemBlock;
    }
}
