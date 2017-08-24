package com.shadowking97.forgecraft.item.material;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shadow Bolt on 7/16/2017.
 *
 * Used to define properties used by different materials for the construction of armor and tools
 * TODO: Add enchanting properties
 */
public class ItemMaterial implements IItemColor {

    private final int baseColor;       //Used for coloring material items
    private final int highlightColor;  //Used for coloring the highlight of items
    private final int shadowColor;

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        if(tintIndex==0)
            return baseColor;
        else if(tintIndex==1)
            return highlightColor;
        else if(tintIndex==2)
            return shadowColor;
        return 0xFFFFFF;
    }

    public enum MaterialType {
        Metal,       //Smeltable Materials, made at forge
        Cloth,       //Dyable Materials
        Leather,     //Tannable Materials
        Wood,        //Unnaffected by Sonic Technology
        Threadding,  //Made from spinning
        Bone,        //For the necromancer in all of us
        Skull,       //No crafting required - direct attachment of mob skulls to the armor
        Crystal,     //For future use.
        Fur,
        Misc
    }

    public static final MaterialType getTypeFromString(String s)
    {
        if(s=="METAL")
            return MaterialType.Metal;
        if(s=="CLOTH")
            return MaterialType.Cloth;
        if(s=="LEATHER")
            return MaterialType.Leather;
        if(s=="WOOD")
            return MaterialType.Wood;
        if(s=="THREADDING")
            return MaterialType.Threadding;
        if(s=="BONE")
            return MaterialType.Bone;
        if(s=="SKULL")
            return MaterialType.Skull;
        if(s=="CRYSTAL")
            return MaterialType.Crystal;
        if(s=="FUR")
            return MaterialType.Fur;
        if(s=="MISC")
            return MaterialType.Misc;
        return null;
    }

    private final String name;
    private final int durability; //How much durability this piece adds
    private final int strength; //strength means different things to armor and tools
    private final MaterialType matType;

    public ItemMaterial(String name, int durability, int strength, MaterialType matType, int baseColor, int highlightColor, int shadowColor)
    {
        this.durability = durability;
        this.strength = strength;
        this.name = name;
        this.matType = matType;
        this.baseColor = baseColor;
        this.highlightColor = highlightColor;
        this.shadowColor = shadowColor;
    }

    public String getName()
    {
        return name;
    }

    public int getDurability()
    {
        return durability;
    }

    public int getStrength()
    {
        return strength;
    }

    public MaterialType getType()
    {
        return matType;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
