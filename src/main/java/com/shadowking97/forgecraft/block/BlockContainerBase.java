package com.shadowking97.forgecraft.block;

import jline.internal.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Variant of the BlockBase that also has a tile entity
 * Created by Shadow Bolt on 7/30/2017.
 */
public abstract class BlockContainerBase<TE extends TileEntity> extends BlockBase {

    public BlockContainerBase(Material materialIn, String name) {
        super(materialIn, name);
    }

    @Nullable
    @Override
    public abstract TE createTileEntity(World worldIn, IBlockState state);

    public abstract Class<TE> getTileEntityClass();

    public TE getTileEntity(IBlockAccess world, BlockPos pos){
        return (TE)world.getTileEntity(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state){return true;}

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
}
