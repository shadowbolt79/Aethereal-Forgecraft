package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.item.armor.ItemDynamicHelmet;
import com.shadowking97.forgecraft.item.components.ItemComponent;
import com.shadowking97.forgecraft.item.material.ItemMolten;
import com.shadowking97.forgecraft.item.tool.ItemHammer;
import com.shadowking97.forgecraft.item.tool.ItemTongs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Shadow Bolt on 7/16/2017.
 */
public class ModItems {

    public static ItemBaseColorable nuggetSteel;
    public static ItemBaseColorable ingotSteel;

    public static ItemBaseColorable nuggetIron;

    public static ItemBaseColorable furSheet;

    public static ItemBase tongs;
    public static ItemBase moltenMetal;
    public static ItemBase hammer;

    public static ItemComponent itemComponentTools;
    public static ItemComponent itemComponentArmor;

    public static ItemBaseArmor itemHelmet;
    public static ItemBaseArmor itemChest;
    public static ItemBaseArmor itemLeggings;
    public static ItemBaseArmor itemBoots;

    public static void init(){
        tongs = register(new ItemTongs("tongs").setCreativeTab(CreativeTabs.MATERIALS));
        moltenMetal = register(new ItemMolten("moltenMetal"));
        hammer = register(new ItemHammer("hammer").setCreativeTab(CreativeTabs.MATERIALS));

        nuggetSteel = register(new ItemBaseColorable("nuggetSteel").setCreativeTab(CreativeTabs.MATERIALS));
        ingotSteel = register(new ItemBaseColorable("ingotSteel").setCreativeTab(CreativeTabs.MATERIALS));
        OreDictionary.registerOre("nuggetSteel", nuggetSteel);
        OreDictionary.registerOre("ingotSteel", ingotSteel);

        nuggetIron = register(new ItemBaseColorable("nuggetIron").setCreativeTab(CreativeTabs.MATERIALS));
        OreDictionary.registerOre("nuggetIron", nuggetIron);

        furSheet = register(new ItemBaseMultiColored("furSheet").setCreativeTab(CreativeTabs.MATERIALS));
        OreDictionary.registerOre("furSheet",new ItemStack(furSheet,1,OreDictionary.WILDCARD_VALUE));

        //testHelmet = register(new TestArmorHelmet().setCreativeTab(CreativeTabs.COMBAT));
        itemComponentTools = register(new ItemComponent("componentTool"));
        itemComponentArmor = register(new ItemComponent("componentArmor").setArmor());

        itemHelmet = register(new ItemDynamicHelmet("itemHelm")).setCreativeTab(CreativeTabs.COMBAT);

        // Register Multicolor items
        String[] dyes =
                {
                        "Black",
                        "Red",
                        "Green",
                        "Brown",
                        "Blue",
                        "Purple",
                        "Cyan",
                        "LightGray",
                        "Gray",
                        "Pink",
                        "Lime",
                        "Yellow",
                        "LightBlue",
                        "Magenta",
                        "Orange",
                        "White"
                };
        for(int i = 0; i < dyes.length; i++){
            OreDictionary.registerOre("furSheet"+dyes[i], new ItemStack(furSheet,1,i));
        }
    }

    private static <T extends Item> T register(T item){
        GameRegistry.register(item);

        if(item instanceof ItemModelProvider){
            ((ItemModelProvider)item).registerItemModel(item);
        }

        return item;
    }
}