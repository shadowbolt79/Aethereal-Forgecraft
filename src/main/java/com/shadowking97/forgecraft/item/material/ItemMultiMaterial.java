package com.shadowking97.forgecraft.item.material;

import net.minecraft.item.ItemStack;

/**
 * Created by Shadow Bolt on 7/25/2017.
 */
public class ItemMultiMaterial extends ItemMaterial {

    private final int baseColors[];
    private final int shadowColors[];
    private final int highlightColors[];

    public ItemMultiMaterial(String name, int durability, int strength, MaterialType matType, int[] baseColors, int[] highlightColors, int[] shadowColors) {
        super(name, durability, strength, matType, 0, 0, 0);
        this.baseColors = baseColors;
        this.shadowColors = shadowColors;
        this.highlightColors = highlightColors;
        assert(baseColors.length==shadowColors.length&&shadowColors.length==highlightColors.length);
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {

        int metaData = stack.getMetadata();
        if(metaData>=baseColors.length||metaData<0)metaData=0;

        if(tintIndex==0)
            return baseColors[metaData];
        else if(tintIndex==1)
            return highlightColors[metaData];
        else if(tintIndex==2)
            return shadowColors[metaData];
        return 0xFFFFFF;
    }


}