package com.shadowking97.forgecraft.block;

import com.shadowking97.forgecraft.item.ModItems;
import com.shadowking97.forgecraft.tiles.TileBloomery;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.util.EnumFacing.*;

/**
 * Defines properties and interactions for the bloomery
 *
 * Bloomery is created with two blocks when placed into the world.
 * Bottom block is responsible for rendering and has the block's state stored
 * Top block has the tile entity. This is so hoppers can feed in charcoal from the top
 *
 * Created by Shadow Bolt on 7/29/2017.
 */
public class Bloomery extends BlockContainerBase<TileBloomery> {
    //Static constants
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool BURNING = PropertyBool.create("burning");
    public static final PropertyBool TOPPART = PropertyBool.create("top");
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyInteger SMELTING = PropertyInteger.create("smelting",0,2);
    public static final PropertyInteger FILLED = PropertyInteger.create("filled",0,3);
    public static final Random RANDOM = new Random();

    public Bloomery(Material materialIn, String name) {
        super(materialIn, name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, NORTH).withProperty(BURNING,false).withProperty(TOPPART,false).withProperty(OPEN,false).withProperty(FILLED,0).withProperty(SMELTING,0));
        this.setHarvestLevel("pickaxe",0);
        this.setHardness(3F);
        this.setResistance(5f);
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }

    /*
    INTERACTION METHODS
     */

    /**
     * Breaks the adjoining block and drops items
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(state.getBlock()==this)
        {
            boolean isTop = state.getValue(TOPPART);
            if(isTop)
                worldIn.setBlockToAir(pos.down());
            else
                worldIn.setBlockToAir(pos.up());

            ItemStack stack = getTileEntity(worldIn,pos).inventory.extractItem(0,64,false);
            if(stack!=null)worldIn.spawnEntity(new EntityItem(worldIn,pos.getX(),pos.getY(),pos.getZ(),stack));
            stack = getTileEntity(worldIn,pos).inventory.extractItem(1,64,false);
            if(stack!=null)worldIn.spawnEntity(new EntityItem(worldIn,pos.getX(),pos.getY(),pos.getZ(),stack));
            //TODO: Drop metal lump
        }
    }

    /**
     * Responsible for putting items into the bloomery, igniting it, and extinguishing it
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(state.getBlock()!=this)
            return false;

        //State is stored in the bottom block
        BlockPos pos2 = pos;
        if(state.getValue(TOPPART))
            pos2=pos.down();
        IBlockState state2 = getActualState(worldIn.getBlockState(pos2),worldIn,pos2);

        ItemStack heldItem = playerIn.getHeldItem(hand);

        if(state2.getValue(BURNING)) {
            if (heldItem != null) {
                if (heldItem.getItem() == Items.WATER_BUCKET) {
                    //Extinguish Block
                    if(worldIn.isRemote)
                    {
                        //Client response for fire being put out
                        ((WorldClient)worldIn).playSound(pos2, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 1.0F, false);

                        //Clouds out of side holes of bloomery
                        //TODO: Investigate why some clouds randomly float down instead of up
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos2.getX()+0.5, pos2.getY()+0.5, pos2.getZ()-0.1, 0,0.1,-0.01);
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos2.getX()+0.5, pos2.getY()+0.5, pos2.getZ()+1.1, 0,0.1,0.01);
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos2.getX()-0.1, pos2.getY()+0.5, pos2.getZ()+0.5, -0.01,0.1,0);
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos2.getX()+1.1, pos2.getY()+0.5, pos2.getZ()+0.5, 0.01,0.1,0);

                        //Clouds out of top of bloomery
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos2.getX()+0.375+(RANDOM.nextDouble()*0.125), pos2.getY()+1.5, pos2.getZ()+0.375+(RANDOM.nextDouble()*0.125), 0,0.1,0);
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos2.getX()+0.375+(RANDOM.nextDouble()*0.125), pos2.getY()+1.3, pos2.getZ()+0.375+(RANDOM.nextDouble()*0.125), 0,0.1,0);
                    }
                    else
                    {
                        if(!playerIn.isCreative())
                            playerIn.setHeldItem(hand,new ItemStack(Items.BUCKET));

                        getTileEntity(worldIn,pos2.up()).extinquish();
                        worldIn.setBlockState(pos2,state2.withProperty(BURNING,false));
                    }
                    //stop going forward
                    return true;
                }
            }
        }
        else { //If not burning
            if (heldItem != null) {
                if (heldItem.getItem() == Items.FLINT_AND_STEEL) {
                    //Ignite block
                    if(worldIn.isRemote)
                        if(state2.getValue(FILLED)!=0)//Has at least some charcoal in it
                            ((WorldClient)worldIn).playSound(pos2, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 0.5F, 0.1F, false);
                        else
                        {
                            if(!playerIn.isCreative())
                                heldItem.damageItem(1,playerIn);

                            if(getTileEntity(worldIn,pos2.up()).ignite())
                                worldIn.setBlockState(pos2, state2.withProperty(BURNING, true));
                        }
                    //Stop going forward
                    return true;
                }
            }
        }

        if(!worldIn.isRemote) {
            if (heldItem != null) {
                //Add charcoal?
                if (heldItem.getItem() == Items.COAL && heldItem.getMetadata() == 1)
                    playerIn.setHeldItem(hand, getTileEntity(worldIn, pos2.up()).addCharcoal(heldItem, playerIn.isCreative()));
                    //Extract material with tongs?
                else if(heldItem.getItem() == ModItems.tongs && heldItem.getMetadata()==0)
                    getTileEntity(worldIn,pos2.up()).extractMaterial(heldItem,playerIn);
                    //Attempt to add itemstack as material
                else
                    getTileEntity(worldIn, pos2.up()).addMaterial(heldItem, playerIn.isCreative());
            }
            else //Attempt to extract material with bare hands. This is dangerous.
                getTileEntity(worldIn,pos2.up()).extractMaterial(null, playerIn);
        }
        return true;
    }

    /*
    CLIENT RENDERING
     */

    /**
     * Displays effects while the block is burning. Fire particles + slag
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos smokePos, Random rand) {
        stateIn = getActualState(stateIn,worldIn,smokePos);

        //Don't need to check if TOPPART because BURNING can only be true in bottom
        if(stateIn.getValue(BURNING))
        {
            //Smoke particles
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                    smokePos.getX()+0.375+(rand.nextDouble()*0.125),
                    smokePos.getY()+1.95,
                    smokePos.getZ()+0.375+(rand.nextDouble()*0.125),
                    0,0.025,0);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                    smokePos.getX()+0.375+(rand.nextDouble()*0.125),
                    smokePos.getY()+1.95,
                    smokePos.getZ()+0.375+(rand.nextDouble()*0.125),
                    0,0.025,0);

            //Flame particles
            worldIn.spawnParticle(EnumParticleTypes.FLAME,
                    smokePos.getX()+0.375+(rand.nextDouble()*0.125),
                    smokePos.getY()+1.4,
                    smokePos.getZ()+0.375+(rand.nextDouble()*0.125),
                    0,0.05,0);
            worldIn.spawnParticle(EnumParticleTypes.FLAME,
                    smokePos.getX()+0.375+(rand.nextDouble()*0.125),
                    smokePos.getY()+1.2,
                    smokePos.getZ()+0.375+(rand.nextDouble()*0.125),
                    0,0.05,0);

            if(rand.nextInt()%10==0)
                ((WorldClient)worldIn).playSound(smokePos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.1F + rand.nextFloat() * 0.05F, rand.nextFloat() * 0.15F + 0.15F, false);

            //Render slag particles if smelting is finished
            //'dripping' flame emulates this perfectly
            if(getTileEntity(worldIn,smokePos.up()).finishedSmelting())
            {
                switch(stateIn.getValue(FACING))
                {
                    case NORTH:
                        worldIn.spawnParticle(EnumParticleTypes.FLAME,
                                smokePos.getX()+0.5,
                                smokePos.getY()+0.5,
                                smokePos.getZ()+1.07,
                                0,-0.05,0);
                        break;
                    case EAST:
                        worldIn.spawnParticle(EnumParticleTypes.FLAME,
                                smokePos.getX()-0.07,
                                smokePos.getY()+0.5,
                                smokePos.getZ()+0.5,
                                0,-0.05,0);
                        break;
                    case WEST:
                        worldIn.spawnParticle(EnumParticleTypes.FLAME,
                                smokePos.getX()+1.07,
                                smokePos.getY()+0.5,
                                smokePos.getZ()+0.5,
                                0,-0.05,0);
                        break;
                    case SOUTH:
                        worldIn.spawnParticle(EnumParticleTypes.FLAME,
                                smokePos.getX()+0.5,
                                smokePos.getY()+0.5,
                                smokePos.getZ()-0.07,
                                0,-0.05,0);
                        break;
                }
            }
        }
    }

    /*
    BLOCK STATE
     */

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if(state.getValue(TOPPART))
            return super.getActualState(state, worldIn, pos);

        //Query tile entity for state
        TileEntity tileentity = worldIn instanceof ChunkCache ? ((ChunkCache)worldIn).getTileEntity(pos.up(), Chunk.EnumCreateEntityType.CHECK) : worldIn.getTileEntity(pos.up());
        if(tileentity instanceof TileBloomery)
        {
            TileBloomery tileBloomery = (TileBloomery)tileentity;
            return state.withProperty(FILLED,tileBloomery.getFilled()).withProperty(OPEN,tileBloomery.isOpen()).withProperty(BURNING,tileBloomery.isBurning()).withProperty(SMELTING,tileBloomery.finishedSmelting()?2:tileBloomery.getNumSmelting()>0?1:0);
        }

        FMLLog.log(Level.ERROR,"Forgecraft: Bloomery("+pos.getX()+","+pos.getY()+","+pos.getZ()+") unable to get tile entity!");
        return super.getActualState(state,worldIn,pos);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.fastFloor((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
        IBlockState state = getDefaultState().withProperty(FACING, enumfacing).withProperty(BURNING,false).withProperty(TOPPART,false).withProperty(OPEN,false).withProperty(FILLED,0).withProperty(SMELTING,0);
        world.setBlockState(pos,state);
        world.setBlockState(pos.up(),state.withProperty(TOPPART,true).withProperty(OPEN,true));

        ItemStack stack = placer.getHeldItem(hand);
        if(placer instanceof EntityPlayer)
        {
            if(!((EntityPlayer) placer).isCreative())
                stack.shrink(1);

        }
        else
            stack.shrink(1);
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { FACING, BURNING, TOPPART, OPEN, FILLED, SMELTING });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();

        if(meta==8)
        {
            return state.withProperty(TOPPART,true).withProperty(OPEN,true).withProperty(FILLED,0);
        }

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
        if(state.getValue(TOPPART))
            return 8;

        int meta = 0;

        switch(state.getValue(FACING))
        {
            case EAST:
                meta=1;
                break;
            case WEST:
                meta=3;
                break;
            case SOUTH:
                meta=2;
        }

        return meta;
    }

    /*
    TILE ENTITY
     */

    /**
     * Only the top part has tile entity, to enable interactions from hoppers from the top
     * @param state
     * @return
     */
    @Override
    public boolean hasTileEntity(IBlockState state){
        return state.getValue(TOPPART);
    }

    @Nullable
    @Override
    public TileBloomery createTileEntity(World worldIn, IBlockState state) {
        return state.getValue(TOPPART)?(new TileBloomery()):null;
    }

    /**
     * Ensures that both the top part and bottom part of the bloomery return the same tile entity.
     */
    @Override
    public TileBloomery getTileEntity(IBlockAccess world, BlockPos pos) {
        return super.getTileEntity(world, world.getBlockState(pos).getValue(TOPPART)?pos:pos.up());
    }

    @Override
    public Class getTileEntityClass() {
        return TileBloomery.class;
    }

    /*
    DEFAULT BLOCK PROPERTIES
     */

    /**
     * If the block is burning, it produces light level of 13
     */
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(BURNING)?13:super.getLightValue(state,world,pos);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return false;
    }

    /**
     * Ensures that there are two spots for the bloomery to exist, since it is two blocks high
     */
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)&&worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up());
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side== EnumFacing.DOWN;
    }
}
