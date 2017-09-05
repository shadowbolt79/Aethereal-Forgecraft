package com.shadowking97.forgecraft.block;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.gui.CraftingGUIHandler;
import com.shadowking97.forgecraft.tiles.TileForge;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
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

/**
 * Created by Shadow Bolt on 9/2/2017.
 */
public class Forge extends BlockContainerBase<TileForge> {
    public static final PropertyBool BURNING = PropertyBool.create("burning");
    public static final PropertyInteger FILLED = PropertyInteger.create("filled",0,12);
    public static final PropertyInteger PART = PropertyInteger.create("part",0,3);


    public Forge(String name) {
        super(Material.ROCK, name);
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
            //Break all other forge blocks
            int p = state.getValue(PART);

            if((p&1)==0)
                worldIn.setBlockToAir(pos.north());
            else
                worldIn.setBlockToAir(pos.south());
            BlockPos pos2;
            if((p&2)==0)
                pos2=pos.west();
            else
                pos2=pos.east();
            worldIn.setBlockToAir(pos2);
            if((p&1)==0)
                worldIn.setBlockToAir(pos2.north());
            else
                worldIn.setBlockToAir(pos2.south());

            //TODO: Do tile entity stuff
        }
    }

    /***
     * Responsible for interacting with forge for creating materials and the such
     * @return
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (state.getBlock() != this)
            return false;

        int p = state.getValue(PART);

        BlockPos pos2 = pos;
        if((p&1)==1)pos2=pos2.south();
        if((p&2)==2)pos2=pos2.east();
        IBlockState state2 = getActualState(worldIn.getBlockState(pos2),worldIn,pos2);

        if(state2.getValue(BURNING)) {
            if (heldItem != null) {
                if (heldItem.getItem() == Items.WATER_BUCKET) {
                    //Extinguish Block
                    if(worldIn.isRemote)
                    {
                        //Client response for fire being put out
                        ((WorldClient)worldIn).playSound(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 1.0F, false);

                        //Clouds out of top of forge
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos.getX()+0.375+(RANDOM.nextDouble()*0.125), pos.getY()+0.5, pos.getZ()+0.375+(RANDOM.nextDouble()*0.125), 0,0.1,0);
                        worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos.getX()+0.375+(RANDOM.nextDouble()*0.125), pos.getY()+0.3, pos.getZ()+0.375+(RANDOM.nextDouble()*0.125), 0,0.1,0);
                    }
                    else
                    {
                        if(!playerIn.isCreative())
                            heldItem.setItem(Items.BUCKET);

                        getTileEntity(worldIn,pos2).extinquish();
                        state2=state2.withProperty(BURNING,false);
                        worldIn.setBlockState(pos2,state2.withProperty(PART,0));
                        pos2=pos2.north();
                        worldIn.setBlockState(pos2,state2.withProperty(PART,1));
                        pos2=pos2.west();
                        worldIn.setBlockState(pos2,state2.withProperty(PART,3));
                        pos2=pos2.south();
                        worldIn.setBlockState(pos2,state2.withProperty(PART,2));
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
                    if(worldIn.isRemote) {
                        if (state2.getValue(FILLED) != 0)//Has at least some charcoal in it
                            ((WorldClient) worldIn).playSound(pos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 0.5F, 0.1F, false);
                    }
                    else {
                        if (getTileEntity(worldIn, pos2).ignite()) {

                            if (!playerIn.isCreative())
                                Items.FLINT_AND_STEEL.setDamage(heldItem, heldItem.getItemDamage() + 1);

                            state2=state2.withProperty(BURNING,true);
                            worldIn.setBlockState(pos2,state2.withProperty(PART,0));
                            pos2=pos2.north();
                            worldIn.setBlockState(pos2,state2.withProperty(PART,1));
                            pos2=pos2.west();
                            worldIn.setBlockState(pos2,state2.withProperty(PART,3));
                            pos2=pos2.south();
                            worldIn.setBlockState(pos2,state2.withProperty(PART,2));
                        }
                    }
                    //Stop going forward
                    return true;
                }
            }
        }

            if (heldItem != null) {
                //Add charcoal?
                if (heldItem.getItem() == Items.COAL && heldItem.getMetadata() == 1) {
                    if(!worldIn.isRemote)
                        playerIn.setHeldItem(hand, getTileEntity(worldIn, pos2).addCharcoal(heldItem, playerIn.isCreative()));
                    return true;
                }
            }


        if(worldIn.isRemote)
            playerIn.openGui(Forgecraft.instance, CraftingGUIHandler.FORGE_CRAFTING_GUI,worldIn,pos.getX(),pos.getY(),pos.getZ());

        return true;
    }

    /*
    CLIENT RENDERING
     */

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos smokePos, Random rand) {
        //Render flame particles?
    }

    @SideOnly(Side.CLIENT)
    public static void notifyAllBlocks(WorldClient client, BlockPos pos)
    {
        IBlockState state = client.getBlockState(pos);
        if(state.getBlock()==ModBlocks.forge)
        {
            int p = state.getValue(PART);

            BlockPos pos2 = pos;
            if((p&1)==1)pos2=pos2.south();
            if((p&2)==2)pos2=pos2.east();

            IBlockState state2 = client.getBlockState(pos2);
            client.notifyBlockUpdate(pos2,state2,ModBlocks.forge.getActualState(state2,client,pos2),3);
            pos2=pos2.north();
            state2 = client.getBlockState(pos2);
            client.notifyBlockUpdate(pos2,state2,ModBlocks.forge.getActualState(state2,client,pos2),3);
            pos2=pos2.west();
            state2 = client.getBlockState(pos2);
            client.notifyBlockUpdate(pos2,state2,ModBlocks.forge.getActualState(state2,client,pos2),3);
            pos2=pos2.south();
            state2 = client.getBlockState(pos2);
            client.notifyBlockUpdate(pos2,state2,ModBlocks.forge.getActualState(state2,client,pos2),3);
        }
    }


    /*
    BLOCK STATE
     */

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        int p = state.getValue(PART);
        BlockPos pos2 = pos;
        if((p&1)==1)pos2=pos2.south();
        if((p&2)==2)pos2=pos2.east();

        //Query tile entity for state
        TileEntity tileentity = worldIn instanceof ChunkCache ? ((ChunkCache)worldIn).getTileEntity(pos2, Chunk.EnumCreateEntityType.CHECK) : worldIn.getTileEntity(pos2);

        if(tileentity instanceof TileForge)
        {
            TileForge tileForge = (TileForge)tileentity;
            return state.withProperty(FILLED,tileForge.getFilled()).withProperty(BURNING,tileForge.isBurning());
        }

        FMLLog.log(Level.ERROR,"Forgecraft: Forge("+pos.getX()+","+pos.getY()+","+pos.getZ()+") unable to get tile entity!");
        return super.getActualState(state,worldIn,pos);
    }

    /**
     * Places additional copies of the forge block.
     */
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
        int p = 0;

        if(facing==EnumFacing.UP||facing==EnumFacing.DOWN) {
            if (hitX - Math.floor(hitX) > 0.5) p |= 2;
            if (hitZ - Math.floor(hitZ) > 0.5) p |= 1;
        }
        else
        {
            if(facing==EnumFacing.NORTH||facing==EnumFacing.SOUTH) {
                if (hitX - Math.floor(hitX) > 0.5) p |= 2;
                if(facing==EnumFacing.SOUTH)
                    p|=1;
            }
            else{
                if (hitZ - Math.floor(hitZ) > 0.5) p |= 1;
                if(facing==EnumFacing.EAST)
                    p|=2;
            }
        }

        //check that you can actually place here
        BlockPos pos2 = pos;
        if((p&1)==1)pos2=pos2.south();
        if((p&2)==0)pos2=pos2.west();
        boolean flag=false;

        if(!flag&&!world.getBlockState(pos2).getBlock().isReplaceable(world, pos2))
            flag=true;
        pos2=pos2.north();
        if(!flag&&!world.getBlockState(pos2).getBlock().isReplaceable(world, pos2))
            flag=true;
        pos2=pos2.east();
        if(!flag&&!world.getBlockState(pos2).getBlock().isReplaceable(world, pos2))
            flag=true;
        pos2=pos2.south();
        if(!flag&&!world.getBlockState(pos2).getBlock().isReplaceable(world, pos2))
            flag=true;

        if(flag)return Blocks.AIR.getDefaultState();

        IBlockState state = getDefaultState().withProperty(BURNING,false).withProperty(FILLED,0).withProperty(PART,p);
        world.setBlockState(pos,state);

        switch(p)
        {
            case 0:
                world.setBlockState(pos.north(),state.withProperty(PART,1));
                world.setBlockState(pos.west(),state.withProperty(PART,2));
                world.setBlockState(pos.west().north(),state.withProperty(PART,3));
                break;
            case 1:
                world.setBlockState(pos.south(),state.withProperty(PART,0));
                world.setBlockState(pos.west().south(),state.withProperty(PART,2));
                world.setBlockState(pos.west(),state.withProperty(PART,3));
                break;
            case 2:
                world.setBlockState(pos.north(),state.withProperty(PART,3));
                world.setBlockState(pos.east(),state.withProperty(PART,0));
                world.setBlockState(pos.east().north(),state.withProperty(PART,1));
                break;
            case 3:
                world.setBlockState(pos.south(),state.withProperty(PART,2));
                world.setBlockState(pos.east(),state.withProperty(PART,1));
                world.setBlockState(pos.east().south(),state.withProperty(PART,0));
                break;
        }

        if(placer instanceof EntityPlayer)
        {
            if(!((EntityPlayer) placer).isCreative())
                stack.stackSize--;
        }
        else
            stack.stackSize--;

        return state;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { BURNING, FILLED, PART });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PART,meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART);
    }

    /*
    TILE ENTITIES
     */

    @Override
    public boolean hasTileEntity(IBlockState state){
        return state.getValue(PART)==0;
    }

    @Override
    public TileForge createTileEntity(World worldIn, IBlockState state) {
        if(state.getValue(PART)==0)return new TileForge();
        return null;
    }

    @Override
    public Class<TileForge> getTileEntityClass() {
        return TileForge.class;
    }

    /**
     * Ensures that all four parts of the forge have the same tile entity
     */
    @Override
    public TileForge getTileEntity(IBlockAccess world, BlockPos pos) {
        int p = world.getBlockState(pos).getValue(PART);

        BlockPos pos2 = pos;
        if((p&1)==1)pos2=pos2.south();
        if((p&2)==2)pos2=pos2.east();

        return super.getTileEntity(world, pos2);
    }

    /*
    DEFAULT BLOCK PROPERTIES
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

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
}
