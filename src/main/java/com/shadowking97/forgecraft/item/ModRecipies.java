package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.block.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Shadow Bolt on 7/17/2017.
 */
public class ModRecipies {
    public static void addRecipies(){
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.nuggetSteel,9), ModItems.ingotSteel);
        GameRegistry.addShapedRecipe(new ItemStack(ModItems.ingotSteel), "###","###","###",'#',ModItems.nuggetSteel);

        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.nuggetIron,9), Items.IRON_INGOT);
        GameRegistry.addShapedRecipe(new ItemStack(Items.IRON_INGOT), "###","###","###",'#',ModItems.nuggetIron);

        GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.bloomery), "# #","# #","#@#",'#', new ItemStack(Blocks.HARDENED_CLAY,1,OreDictionary.WILDCARD_VALUE),'@',new ItemStack(Blocks.FURNACE));
        GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.bloomery), "# #","# #","#@#",'#', new ItemStack(Blocks.STAINED_HARDENED_CLAY,1,OreDictionary.WILDCARD_VALUE),'@',new ItemStack(Blocks.FURNACE));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.hammer),"@#@"," & ",'#',"ingotIron",'@',"nuggetIron",'&',"stickWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.anvil),"%#%"," & ",'#',new ItemStack(Blocks.IRON_BLOCK),'&',"logWood",'%',new ItemStack(Items.IRON_INGOT)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.tongs), "X X"," X ","x x",'X',"ingotIron",'x',"nuggetIron"));

        //Colored recipies
        for(int i = 0; i < 16; i++)
        {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,1,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,2,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,3,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,4,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,5,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,6,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,7,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.furSheet,8,i),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(ModItems.furSheet, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE,1,i));
        }
    }
}
