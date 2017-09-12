package com.shadowking97.forgecraft.item.recipeFactories;

import com.google.gson.JsonObject;
import com.shadowking97.forgecraft.Forgecraft;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

/***
 * Used for coloring items and blocks with dye. IE: Fur
 */
public class ColorizedItemFactory implements IRecipeFactory {
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context,json);

        return new ColorizedItemRecipe(new ResourceLocation(Forgecraft.MODID,"colorized_item_crafting"),recipe.getIngredients(),recipe.getRecipeOutput());
    }

    public static class ColorizedItemRecipe extends ShapelessOreRecipe {
        public ColorizedItemRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result) {
            super(group, input, result);
        }

        @Nonnull
        @Override
        public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
            ItemStack colored = output.copy();
            for(int i = 0; i < var1.getSizeInventory();++i)
            {
                ItemStack stack = var1.getStackInSlot(i);

                if(!stack.isEmpty())
                {
                    int[] ores = OreDictionary.getOreIDs(stack);
                    for(int j: ores){
                        String s = OreDictionary.getOreName(j);

                        switch(s){
                            case "dyeBlack":
                                colored.setItemDamage(0);
                                return colored;
                            case "dyeRed":
                                colored.setItemDamage(1);
                                return colored;
                            case "dyeGreen":
                                colored.setItemDamage(2);
                                return colored;
                            case "dyeBrown":
                                colored.setItemDamage(3);
                                return colored;
                            case "dyeBlue":
                                colored.setItemDamage(4);
                                return colored;
                            case "dyePurple":
                                colored.setItemDamage(5);
                                return colored;
                            case "dyeCyan":
                                colored.setItemDamage(6);
                                return colored;
                            case "dyeLightGray":
                                colored.setItemDamage(7);
                                return colored;
                            case "dyeGray":
                                colored.setItemDamage(8);
                                return colored;
                            case "dyePink":
                                colored.setItemDamage(9);
                                return colored;
                            case "dyeLime":
                                colored.setItemDamage(10);
                                return colored;
                            case "dyeYellow":
                                colored.setItemDamage(11);
                                return colored;
                            case "dyeLightBlue":
                                colored.setItemDamage(12);
                                return colored;
                            case "dyeMagenta":
                                colored.setItemDamage(13);
                                return colored;
                            case "dyeOrange":
                                colored.setItemDamage(14);
                                return colored;
                            case "dyeWhite":
                                colored.setItemDamage(15);
                                return colored;
                        }
                    }
                }
            }
            return colored;
        }
    }
}
