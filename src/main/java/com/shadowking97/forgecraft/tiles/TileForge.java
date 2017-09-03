package com.shadowking97.forgecraft.tiles;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.network.PacketRequestUpdateForge;
import com.shadowking97.forgecraft.network.PacketUpdateForge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

/**
 * Created by Shadow Bolt on 9/2/2017.
 */
public class TileForge extends TileEntity implements ITickable {
    private final static int MAXCHARCOAL = 256;

    private final static double COALBURNA = 625*Math.sqrt(5)/16777216;
    private final static double COALBURNB = 4.0/Math.sqrt(5);

    public ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if(slot<4)
            {
                if(stack!=null)
                    if(stack.getItem()== Items.COAL&&stack.getMetadata()==1)
                        super.setStackInSlot(slot,stack);
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(slot<4)
            {
                if(stack!=null)
                    if(stack.getItem()==Items.COAL&&stack.getMetadata()==1)
                        return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        //Should force only charcoal to be added to these stacks from a hopper
        @Override
        public ItemStack getStackInSlot(int slot) {
            if(slot<4) {
                ItemStack stack = super.getStackInSlot(slot);
                if(stack==null)
                    stack = new ItemStack(Items.COAL, 0, 1);
                return stack;
            }
            else
                return super.getStackInSlot(slot);
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability== CapabilityItemHandler.ITEM_HANDLER_CAPABILITY&&facing==EnumFacing.UP?true:super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability== CapabilityItemHandler.ITEM_HANDLER_CAPABILITY&&facing==EnumFacing.UP?((T)inventory):super.getCapability(capability, facing);
    }

    private int temp = -1;
    private int timeSinceLastCharcoal = 0;

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.worldObj.isRemote) {
            Forgecraft.network.sendToServer(new PacketRequestUpdateForge(this));
        }
    }


    public int getTemp(){return temp;}
    public int getNumCharcoal(){return inventory.getStackInSlot(0).stackSize+inventory.getStackInSlot(1).stackSize+inventory.getStackInSlot(2).stackSize+inventory.getStackInSlot(3).stackSize;}

    public void setTemp(int temp){this.temp= temp;markDirty();}
    public void setNumCharcoal(int numCharcoal){
        if(numCharcoal>192)
        {
            inventory.setStackInSlot(0,new ItemStack(Items.COAL, 64, 1));
            inventory.setStackInSlot(1,new ItemStack(Items.COAL, 64, 1));
            inventory.setStackInSlot(2,new ItemStack(Items.COAL, 64, 1));
            inventory.setStackInSlot(3,new ItemStack(Items.COAL, numCharcoal-192, 1));
        }
        else if(numCharcoal>128)
        {
            inventory.setStackInSlot(0,new ItemStack(Items.COAL, 64, 1));
            inventory.setStackInSlot(1,new ItemStack(Items.COAL, 64, 1));
            inventory.setStackInSlot(2,new ItemStack(Items.COAL, numCharcoal-128, 1));
            inventory.setStackInSlot(3,null);
        }
        else if(numCharcoal>64)
        {
            inventory.setStackInSlot(0,new ItemStack(Items.COAL, 64, 1));
            inventory.setStackInSlot(1,new ItemStack(Items.COAL, numCharcoal-64, 1));
            inventory.setStackInSlot(2,null);
            inventory.setStackInSlot(3,null);
        }
        else if(numCharcoal>0)
        {
            inventory.setStackInSlot(0,new ItemStack(Items.COAL, numCharcoal, 1));
            inventory.setStackInSlot(1,null);
            inventory.setStackInSlot(2,null);
            inventory.setStackInSlot(3,null);
        }
        else
        {
            inventory.setStackInSlot(0,null);
            inventory.setStackInSlot(1,null);
            inventory.setStackInSlot(2,null);
            inventory.setStackInSlot(3,null);
        }
    }

    public void sendUpdate()
    {
        if(!worldObj.isRemote)
            Forgecraft.network.sendToAllAround(new PacketUpdateForge(this), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("temperature",temp);
        compound.setTag("inventory",inventory.serializeNBT());

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        temp = compound.getInteger("temperature");
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    public boolean isBurning()
    {
        return temp>0;
    }

    public void extinquish()
    {
        temp = -1;
        sendUpdate();
        markDirty();
    }

    public boolean ignite()
    {
        if(temp<100&&getNumCharcoal()>20) {
            temp = 100;
            markDirty();
            sendUpdate();
            return true;
        }
        return false;
    }

    public ItemStack addCharcoal(ItemStack charcoal, boolean isCreative)
    {
        int i = getFilled();

        if(charcoal.getItem()!= Items.COAL||charcoal.getMetadata()!=1||getNumCharcoal()>=MAXCHARCOAL)return charcoal;
        int stackSize = charcoal.stackSize;
        charcoal = inventory.insertItem(0,charcoal,false);
        if(charcoal!=null&&charcoal.stackSize!=0)
            charcoal = inventory.insertItem(1,charcoal,false);
        if(charcoal!=null&&charcoal.stackSize!=0)
            charcoal = inventory.insertItem(2,charcoal,false);
        if(charcoal!=null&&charcoal.stackSize!=0)
            charcoal = inventory.insertItem(3,charcoal,false);

        if(isCreative){
            if(charcoal==null)
                charcoal=new ItemStack(Items.COAL,stackSize,1);
            else
                charcoal.stackSize=stackSize;
        }

        if(i!=getFilled())
            sendUpdate();

        return charcoal;
    }

    public int getFilled()
    {
        int numCharcoal = getNumCharcoal();
        if(numCharcoal==0)
            return 0;
        return (int) Math.ceil(numCharcoal*12.0/256.0);
    }

    float timeSinceLastBellows = 0;

    public void useBellows()
    {
        if(temp>0&&timeSinceLastBellows==0) {
            temp += 25;
            timeSinceLastBellows=2;
        }
    }

    private boolean burnCharcoal()
    {
        if(inventory.extractItem(3,1,false)==null)
            if(inventory.extractItem(2,1,false)==null)
                if(inventory.extractItem(1,1,false)==null)
                    return inventory.extractItem(0,1,false)!=null;
        return true;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock()!=newSate.getBlock() ;
    }

    private static final Random random = new Random();
    private int tick = random.nextInt(20);

    @Override
    public void update() {
        if(worldObj.isRemote)return;

        if(isBurning())
        {
            ++tick;
            if(tick<20)
                return;
            tick=0;

            boolean updateFlag = false;

            if(timeSinceLastBellows>0)
                --timeSinceLastBellows;

            //Figure out what the idle temperature is
            int idleTemp = 200 + (getNumCharcoal() << 2);
            //Modulate temperature towards idle temperature
            temp += (idleTemp-temp)>>3;
            System.out.println("Forge Temp: "+temp);


            timeSinceLastCharcoal+=2;

            if(timeSinceLastCharcoal > 120 || timeSinceLastCharcoal > (int)(1/(COALBURNA*Math.pow(COALBURNB,temp/100.0))))
            {
                int i = getFilled();
                if(!burnCharcoal())
                {
                    temp=-1;
                }
                timeSinceLastCharcoal=0;

                if(getFilled()!=i)
                    updateFlag=true;
                System.out.println("Forge charcoal: "+getNumCharcoal());
                System.out.println("Forge time to next burn:"+(1/(COALBURNA*Math.pow(COALBURNB,temp/100.0))));
            }
            if(updateFlag)
                sendUpdate();
        }
    }
}