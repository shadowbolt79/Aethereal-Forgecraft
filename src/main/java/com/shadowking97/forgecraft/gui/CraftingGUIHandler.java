package com.shadowking97.forgecraft.gui;

import com.shadowking97.forgecraft.block.Forge;
import com.shadowking97.forgecraft.block.ModBlocks;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by Shadow Bolt on 9/3/2017.
 */
public class CraftingGUIHandler implements IGuiHandler {
    public static int FORGE_CRAFTING_GUI = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID==FORGE_CRAFTING_GUI){
            BlockPos blockPos = new BlockPos(x,y,z);
            IBlockState blockState = world.getBlockState(blockPos);
            if(blockState.getBlock()== ModBlocks.forge)
            {
                return new CraftingGUIContainer(((Forge)ModBlocks.forge).getTileEntity(world,blockPos), ItemMaterial.MaterialType.Metal,player);
            }
        }
        return null;
    }
}
