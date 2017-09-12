package com.shadowking97.forgecraft.block;

import com.shadowking97.forgecraft.interfaces.IHammerable;
import com.shadowking97.forgecraft.interfaces.IHammerableItem;
import com.shadowking97.forgecraft.item.ModItems;
import com.shadowking97.forgecraft.tiles.TileAnvil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.*;

/**
 * Defines properties and interactions for the Anvil block
 * TODO: Consider allowing diagonal anvils
 * Created by Shadow Bolt on 8/4/2017.
 */
public class Anvil extends BlockContainerBase<TileAnvil> implements IHammerable {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public Anvil(Material materialIn, String name) {
        super(materialIn, name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, NORTH));
        this.setHardness(3F);
        this.setResistance(5f);
        this.setLightOpacity(0);
    }

    /*
    BLOCK INTERACTION METHODS
     */

    @Override
    /***
     * Allows the hammer object to hit the items in the anvil
     */
    public void useHammer(ItemStack hammer, EntityPlayer player, BlockPos pos) {
        if(!player.world.isRemote)
        {
            TileAnvil tileAnvil = getTileEntity(player.world,pos);
            IItemHandler iItemHandler = tileAnvil.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,UP);
            ItemStack stack = iItemHandler.getStackInSlot(0);
            if(stack!=null && stack.getItem() instanceof IHammerableItem)
            {
                ((IHammerableItem) stack.getItem()).onUseHammer(stack,pos,player.world);
            }
            if(stack==null||stack.getCount()==0)
                iItemHandler.extractItem(0,1,false);

            tileAnvil.markDirty();
        }
    }


    @Override
    /***
     * Drops the item on the anvil when the block is broken
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileAnvil tile = getTileEntity(worldIn,pos);
        if(tile==null)return;
        IItemHandler iItemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        if(iItemHandler==null)return;

        ItemStack stack = iItemHandler.getStackInSlot(0);
        if(stack!=null){
            EntityItem item = new EntityItem(worldIn,pos.getX(),pos.getY(),pos.getZ(),stack);
            worldIn.spawnEntity(item);
        }
        super.breakBlock(worldIn,pos,state);
    }

    /**
     * Takes items and adds them to the anvil, and allows the hammer's code to run
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        //Sanity checks
        if(state.getBlock()!=this)
            return false;
        if(heldItem==null)
            return false;

        //Hammer has internal use code. Must return false here in order for that code to run
        if(heldItem.getItem()== ModItems.hammer)
            return false;

        if(!worldIn.isRemote) {
            if (heldItem.getItem() instanceof IHammerableItem) {
                //Adds the held item to the anvil
                TileAnvil tileAnvil = getTileEntity(worldIn, pos);
                IItemHandler iItemHandler = tileAnvil.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                if(iItemHandler.getStackInSlot(0)==null) {
                    playerIn.setHeldItem(hand, iItemHandler.insertItem(0, heldItem, false));
                    tileAnvil.markDirty();
                }
            }
            else if (heldItem.getItem() == ModItems.tongs && heldItem.getMetadata() == 1) {
                //Creates a molten item from tongs and add to inventory
                TileAnvil tileAnvil = getTileEntity(worldIn, pos);
                IItemHandler iItemHandler = tileAnvil.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                ItemStack moltenStack = new ItemStack(ModItems.moltenMetal,1,0);
                moltenStack.setTagCompound(heldItem.getTagCompound());
                if(iItemHandler.insertItem(0, moltenStack, false)==null){
                    tileAnvil.markDirty();

                    //Return tongs to empty
                    heldItem.setItemDamage(0);
                    heldItem.setTagCompound(null);
                }
            }
        }

        return true;
    }

    /*
    BLOCKSTATE
     */

    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.fastFloor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
        IBlockState state = getDefaultState().withProperty(FACING, enumfacing);
        world.setBlockState(pos,state);
        if(placer instanceof EntityPlayer)
        {
            if(!((EntityPlayer) placer).isCreative())stack.shrink(1);
        }
        else
            stack.shrink(1);
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { FACING });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();

        switch(meta&3)
        {
            case 1:
                state = state.withProperty(FACING, EAST);
                break;
            case 2:
                state = state.withProperty(FACING,SOUTH);
                break;
            case 3:
                state = state.withProperty(FACING,WEST);
                break;
            default:
                state = state.withProperty(FACING,NORTH);
        }

        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;

        switch(state.getValue(FACING))
        {
            case EAST:
                meta|=1;
                break;
            case WEST:
                meta|=3;
                break;
            case SOUTH:
                meta|=2;
        }

        return meta;
    }

    /*
    TILE ENTITY
     */

    @Override
    public TileAnvil createTileEntity(World worldIn, IBlockState state) {
        return new TileAnvil();
    }

    @Override
    public Class<TileAnvil> getTileEntityClass() {
        return TileAnvil.class;
    }


    /*
    STANDARD BLOCK PROPERTIES
     */

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {return false;}

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {return false;}

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return false;
    }
}
