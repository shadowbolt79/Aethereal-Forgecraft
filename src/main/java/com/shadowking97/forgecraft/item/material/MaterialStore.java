package com.shadowking97.forgecraft.item.material;

import com.google.common.collect.ImmutableList;
import com.shadowking97.forgecraft.item.ItemBaseColorable;
import com.shadowking97.forgecraft.item.ItemBaseMultiColored;
import com.shadowking97.forgecraft.item.ModItems;
import jline.internal.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.util.*;

import static com.google.common.collect.ImmutableList.builder;

/**
 * Created by Shadow Bolt on 7/16/2017.
 * Stores items to materials
 */
public class MaterialStore {

    HashSet<ItemMaterialDefinition> items2Mat;
    HashSet<ItemMaterialDefinition> ores2Mat;
    HashMap<String,ItemMaterial> matNames;

    public MaterialStore() {
        matNames=new HashMap<>();
        items2Mat = new HashSet<>();
        ores2Mat = new HashSet<>();
    }

    public static MaterialStore INSTANCE = new MaterialStore();

    public void init()
    {
        //Create materials & store items
        ItemMaterial itemMaterial;

        //#####METALS

        itemMaterial = generateMaterial("Steel", 10,9, ItemMaterial.MaterialType.Metal, 0x7d7d7d, 0xcecece, 0x4d4d4d);
        setMaterialForOreDict(itemMaterial,"nuggetSteel",1);
        setMaterialForOreDict(itemMaterial,"ingotSteel", 9);
        setMaterialForOreDict(itemMaterial,"blockSteel",81);
        setOreMaterialForOreDict(itemMaterial, "nuggetIron",1);
        setOreMaterialForOreDict(itemMaterial, "ingotIron", 9);
        setOreMaterialForOreDict(itemMaterial, "blockIron", 81);

        if(OreDictionary.doesOreNameExist("nuggetIron")||OreDictionary.doesOreNameExist("ingotIron")||OreDictionary.doesOreNameExist("blockIron")) {
            itemMaterial = generateMaterial("Iron", 8, 7, ItemMaterial.MaterialType.Metal, 0xc5c5c5, 0xffffff, 0x6e6e6e);
            setMaterialForOreDict(itemMaterial, "nuggetIron", 1);
            setMaterialForOreDict(itemMaterial, "ingotIron", 9);
            setMaterialForOreDict(itemMaterial, "blockIron", 81);
            setOreMaterialForOreDict(itemMaterial, "oreIron",9);
        }
        
        if(OreDictionary.doesOreNameExist("nuggetGold")||OreDictionary.doesOreNameExist("ingotGold")||OreDictionary.doesOreNameExist("blockGold")) {
            itemMaterial = generateMaterial("Gold", 4, 5, ItemMaterial.MaterialType.Metal, 0xf7f520, 0xffffff, 0xe7c419);
            setMaterialForOreDict(itemMaterial, "nuggetGold", 1);
            setMaterialForOreDict(itemMaterial, "ingotGold", 9);
            setMaterialForOreDict(itemMaterial, "blockGold", 81);
            setOreMaterialForOreDict(itemMaterial, "oreGold",9);
        }


        //####GEMSTONES

        itemMaterial = generateMaterial("Diamond", 9, 9, ItemMaterial.MaterialType.Crystal, 0x8cf4e2, 0xffffff, 0x1b7b6b);
        setMaterialForOreDict(itemMaterial,"gemDiamond",1);
        setMaterialForOreDict(itemMaterial, "blockDiamond",9);

        itemMaterial = generateMaterial("Emerald", 9, 9, ItemMaterial.MaterialType.Crystal, 0x00b038, 0xdaffeb, 0x007715);
        setMaterialForOreDict(itemMaterial,"gemEmerald",1);
        setMaterialForOreDict(itemMaterial, "blockEmerald",9);

        itemMaterial = generateMaterial("Lapis Lazuli", 4, 5, ItemMaterial.MaterialType.Crystal, 0x224baf, 0x7497ea, 0x0a2b7a);
        setMaterialForOreDict(itemMaterial,"gemLapis",1);
        setMaterialForOreDict(itemMaterial,"blockLapis",9);

        itemMaterial = generateMaterial("Prismarine", 8, 5, ItemMaterial.MaterialType.Crystal, 0x7fb8a4, 0x9ecabb, 0x3e7466);
        setMaterialForOreDict(itemMaterial,"gemPrismarine",1);
        setMaterialForOreDict(itemMaterial,"blockPrismarine",4);

        itemMaterial = generateMaterial("Quartz", 9, 8, ItemMaterial.MaterialType.Crystal, 0xe5dcd4, 0xf0ede9, 0x897b73);
        setMaterialForOreDict(itemMaterial,"gemQuartz",1);
        setMaterialForOreDict(itemMaterial, "blockQuartz",4);

        itemMaterial = generateMaterial("Redstone", 4, 5, ItemMaterial.MaterialType.Crystal, 0x720000, 0xff0100, 0x370000);
        setMaterialForOreDict(itemMaterial,"dustRedstone",1);
        setMaterialForOreDict(itemMaterial,"blockRedstone",9);

        itemMaterial = generateMaterial("Glowstone", 4, 5, ItemMaterial.MaterialType.Crystal, 0xd2d200, 0xffff00, 0x636300);
        setMaterialForOreDict(itemMaterial,"dustGlowstone",1);
        setMaterialForOreDict(itemMaterial,"blockGlowstone",4);

        /*        // Register dyes
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
        };*/
        //#####FUR
        itemMaterial = generateMultiMaterial("Fur", 1, 1, ItemMaterial.MaterialType.Fur,
                new int[]{
                        0x171717,0xb81d1d,0x247a00,0x79503f,0x003584,0x3e0052,0x00bcb1,0x919191,
                        0x5e5e5e,0xff85c2,0x00ff00,0xffff00,0x87cefa,0xff00ff,0xff8e00,0xf0f0f0
                }, //Base Color
                new int[]{
                        0x1f1f1f,0xc73c3c,0x2fb000,0x8a6e57,0x004cbc,0x79388f,0x22e6da,0xb0b0b0,
                        0x848484,0xffb5da,0x33ff33,0xffff33,0xbde6ff,0xff54ff,0xffb04d,0xffffff
                }, //HighLights
                new int[]{
                        0x0f0f0f,0x7a1313,0x194e00,0x4f3529,0x002254,0x280134,0x05a199,0x858585,
                        0x535353,0xd46ea1,0x14cc14,0xcccc14,0x6ba4c7,0xcc14cc,0xd1790a,0xbfbfbf
        });        //Shadows
        setMaterialForOreDict(itemMaterial, "furSheet", 1);


        //######LEATHER
        itemMaterial = generateMaterial("Leather", 2,3, ItemMaterial.MaterialType.Leather, 0x603d26, 0xa46f4a, 0x472c1c);
        setMaterialForOreDict(itemMaterial, "leather",8);
    }

    private ItemMaterial generateMaterial(String name, int durability, int strength, ItemMaterial.MaterialType type, int baseColor, int highlightColor, int shadowColor)
    {
        ItemMaterial itemMaterial = new ItemMaterial(name, durability, strength, type, baseColor, highlightColor, shadowColor);
        matNames.put(name,itemMaterial);
        return itemMaterial;
    }
    private ItemMaterial generateMultiMaterial(String name, int durability, int strength, ItemMaterial.MaterialType type, int baseColor[], int highlightColor[], int shadowColor[])
    {
        ItemMaterial itemMaterial = new ItemMultiMaterial(name, durability, strength, type, baseColor, highlightColor, shadowColor);
        matNames.put(name,itemMaterial);
        return itemMaterial;
    }

    @Nullable
    public ItemMaterialDefinition getMaterialDefinitionForItem(ItemStack stack)
    {
        for(ItemMaterialDefinition materialDefinition: items2Mat)
        {
            if(materialDefinition.equals(stack))
                return materialDefinition;
        }
        return null;
    }

    public ArrayList<ItemMaterialDefinition> getItemsForMaterial(ItemMaterial material)
    {
        ArrayList<ItemMaterialDefinition> definitions = new ArrayList<>();

        for(ItemMaterialDefinition definition: items2Mat)
        {
            if(definition.equals(material))
                definitions.add(definition);
        }

        definitions.sort(((o1, o2) -> -1*(o1.getValue()-o2.getValue())));

        return definitions;
    }

    @Nullable
    public ItemMaterialDefinition getMaterialDefinitionForOre(ItemStack stack)
    {
        for(ItemMaterialDefinition materialDefinition: ores2Mat)
        {
            if(materialDefinition.equals(stack))
                return materialDefinition;
        }
        return null;
    }


    public ArrayList<ItemMaterialDefinition> getOresForMaterial(ItemMaterial material)
    {
        ArrayList<ItemMaterialDefinition> definitions = new ArrayList<>();

        for(ItemMaterialDefinition definition: ores2Mat)
        {
            if(definition.equals(material))
                definitions.add(definition);
        }

        definitions.sort(((o1, o2) -> -1*(o1.getValue()-o2.getValue())));

        return definitions;
    }

    private void setMaterialForOreDict(ItemMaterial itemMaterial, String name, int value)
    {
        List<ItemStack> ores = OreDictionary.getOres(name);
        for(ItemStack ore : ores)
        {
            setMaterialForItem(ore,itemMaterial,value);
        }
    }

    private void setMaterialForItem(ItemStack stack, ItemMaterial material, int value)
    {
        int i = stack.getMetadata();

        if(i==OreDictionary.WILDCARD_VALUE)
        {
            ItemStack itemStack = stack.copy();
            itemStack.setItemDamage(0);
            if(items2Mat.contains(itemStack))
                return;
        }
        else if(items2Mat.contains(stack))
            return;

        if(stack.getItem() instanceof ItemBaseColorable)
            ((ItemBaseColorable) stack.getItem()).setMaterial(material);

        if(i==OreDictionary.WILDCARD_VALUE&&stack.getItem().getHasSubtypes())
        {
            List<ItemStack> subItems = new ArrayList<ItemStack>();
            stack.getItem().getSubItems(stack.getItem(),null,subItems);
            for(ItemStack itemStack : subItems) {
                try {
                    items2Mat.add(new ItemMaterialDefinition(itemStack, material, value));
                }catch(Exception e){
                    //Do nothing
                    FMLLog.log(Level.ERROR,e,"");
                }
            }
        }
        else {
            try {
                items2Mat.add(new ItemMaterialDefinition(stack, material, value));
            }catch(Exception e){
                //Do nothing
                FMLLog.log(Level.ERROR,e,"");
            }
        }
    }

    private void setOreMaterialForOreDict(ItemMaterial itemMaterial, String name, int value)
    {
        List<ItemStack> ores = OreDictionary.getOres(name);
        for(ItemStack ore : ores)
        {
            setOreMaterialForItem(ore,itemMaterial,value);
        }
    }

    private void setOreMaterialForItem(ItemStack stack, ItemMaterial material, int value)
    {
        int i = stack.getMetadata();

        if(i==OreDictionary.WILDCARD_VALUE)
        {
            ItemStack itemStack = stack.copy();
            itemStack.setItemDamage(0);
            if(ores2Mat.contains(itemStack))
                return;
        }
        else if(ores2Mat.contains(stack))
            return;

        if(stack.getItem() instanceof ItemBaseColorable)
            ((ItemBaseColorable) stack.getItem()).setMaterial(material);

        if(i==OreDictionary.WILDCARD_VALUE&&stack.getItem().getHasSubtypes())
        {
            List<ItemStack> subItems = new ArrayList<ItemStack>();
            stack.getItem().getSubItems(stack.getItem(),null,subItems);
            for(ItemStack itemStack : subItems) {
                try {
                    ores2Mat.add(new ItemMaterialDefinition(itemStack, material, value));
                }catch(Exception e){
                    //Do nothing
                    FMLLog.log(Level.ERROR,e,"");
                }
            }
        }
        else {
            try {
                ores2Mat.add(new ItemMaterialDefinition(stack, material, value));
            }catch(Exception e){
                //Do nothing
                FMLLog.log(Level.ERROR,e,"");
            }
        }
    }

    public ItemMaterial getMaterialByName(String name)
    {
        return matNames.get(name);
    }
}