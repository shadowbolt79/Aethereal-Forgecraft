package com.shadowking97.forgecraft.tiles;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.item.ModItems;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import com.shadowking97.forgecraft.item.material.ItemMaterialDefinition;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import com.shadowking97.forgecraft.network.PacketRequestUpdateBloomery;
import com.shadowking97.forgecraft.network.PacketUpdateBloomery;
import com.shadowking97.forgecraft.util.CommonUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

/**
 * Created by Shadow Bolt on 7/30/2017.
 */
public class TileBloomery extends TileEntity implements ITickable {
    private final static int MAXCHARCOAL = 128;
    private final static int MAXMATERIAL = 576;

    //precalculated maths
    private final static double COALBURNA = 625*Math.sqrt(5)/16777216;
    private final static double COALBURNB = 4.0/Math.sqrt(5);
    private final static double SMELTINGA = 5.0/(16.0*((Math.sqrt(165.0)-9.0)/6.0));
    private final static double SMELTINGB = (Math.sqrt(165.0)-3.0)/6.0;

    public ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if(slot<2)
            {
                if(stack!=null)
                    if(stack.getItem()== Items.COAL&&stack.getMetadata()==1)
                        super.setStackInSlot(slot,stack);
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(slot<2)
            {
                if(stack!=null)
                    if(stack.getItem()== Items.COAL&&stack.getMetadata()==1)
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
            if(slot<2) {
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
        return capability== CapabilityItemHandler.ITEM_HANDLER_CAPABILITY&&facing== EnumFacing.UP?true:super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability== CapabilityItemHandler.ITEM_HANDLER_CAPABILITY&&facing== EnumFacing.UP?((T)inventory):super.getCapability(capability, facing);
    }

    private final static Random random = new Random();

    private int temp = -1;
    //private int numCharcoal = 0;

    private ItemMaterial materialSmelting = null;
    private float smeltingProgress = -1;
    private int numSmelting = 0;
    private boolean isOpen = false;
    private int timeSinceLastCharcoal = 0;

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.world.isRemote) {
            Forgecraft.network.sendToServer(new PacketRequestUpdateBloomery(this));
        }
    }

    public int getTemp(){return temp;}
    public int getNumCharcoal(){return inventory.getStackInSlot(0).getCount()+inventory.getStackInSlot(1).getCount();}
    public ItemMaterial getMaterialSmelting(){return materialSmelting;}
    public float getSmeltingProgress() {return smeltingProgress;}
    public int getNumSmelting() {return numSmelting;}

    public void setTemp(int temp){this.temp= temp;markDirty();}
    public void setNumCharcoal(int numCharcoal){
        if(numCharcoal>64)
        {
            inventory.setStackInSlot(0,new ItemStack(Items.COAL, 64, 1));
            inventory.setStackInSlot(1,new ItemStack(Items.COAL, numCharcoal-64, 1));
        }
        else if(numCharcoal>0)
        {
            inventory.setStackInSlot(0,new ItemStack(Items.COAL, numCharcoal, 1));
            inventory.setStackInSlot(1,null);
        }
        else
        {
            inventory.setStackInSlot(0,null);
            inventory.setStackInSlot(1,null);
        }
    }
    public void setMaterialSmelting(ItemMaterial materialSmelting){this.materialSmelting=materialSmelting;markDirty();}
    public void setSmeltingProgress(float smeltingProgress) {this.smeltingProgress=smeltingProgress;markDirty();}
    public void setNumSmelting(int numSmelting) {this.numSmelting=numSmelting;markDirty();}
    public void setOpen(boolean isOpen){this.isOpen=isOpen;markDirty();}

    public void sendUpdate()
    {
        if(!world.isRemote)
            Forgecraft.network.sendToAllAround(new PacketUpdateBloomery(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("temperature",temp);
        compound.setTag("inventory",inventory.serializeNBT());
        compound.setString("materialSmelting",materialSmelting==null?"null":materialSmelting.getName());
        compound.setFloat("smeltingProgress",smeltingProgress);
        compound.setInteger("numSmelting",numSmelting);
        compound.setBoolean("isOpen",isOpen);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        temp = compound.getInteger("temperature");
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        materialSmelting = MaterialStore.INSTANCE.getMaterialByName(compound.getString("materialSmelting"));
        smeltingProgress = compound.getFloat("smeltingProgress");
        numSmelting = compound.getInteger("numSmelting");
        isOpen = compound.getBoolean("isOpen");
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
            if (materialSmelting != null&&smeltingProgress<0)
                smeltingProgress = 0;
            markDirty();
            sendUpdate();
            return true;
        }
        return false;
    }

    public boolean finishedSmelting()
    {
        return smeltingProgress>=100;
    }

    public ItemStack addCharcoal(ItemStack charcoal, boolean isCreative)
    {
        //int numCharcoal = getNumCharcoal();
        if(charcoal.getItem()!= Items.COAL||charcoal.getMetadata()!=1||getNumCharcoal()>=MAXCHARCOAL)return charcoal;
        int stackSize = charcoal.getCount();
        charcoal = inventory.insertItem(0,charcoal,false);
        if(charcoal!=null&&charcoal.getCount()!=0)
            charcoal = inventory.insertItem(1,charcoal,false);

        if(isCreative){
            if(charcoal==null)
                charcoal=new ItemStack(Items.COAL,stackSize,1);
            else
                charcoal.setCount(stackSize);
        }

        return charcoal;
    }

    public boolean addMaterial(ItemStack material, boolean isCreative)
    {
        ItemMaterialDefinition definition = MaterialStore.INSTANCE.getMaterialDefinitionForOre(material);
        if(definition==null||definition.getMaterial().getType()!= ItemMaterial.MaterialType.Metal)
        {
            //Not a smeltable material
            return false;
        }

        //add the stack into the bloomery
        if(materialSmelting==null)
        {
            materialSmelting = definition.getMaterial();
            int iAdding = (material.getCount()*definition.getValue());

            int iRemainder = 0;
            while(iAdding>MAXMATERIAL)
            {
                iAdding = ((material.getCount()-(++iRemainder))*definition.getValue());
            }
            numSmelting=iAdding;
            if(!isCreative)
                material.setCount(iRemainder);

            CommonUtil.debug("Bloomery("+pos.getX()+","+pos.getY()+","+pos.getZ()+"): Added "+(iAdding)+ " "+materialSmelting.getName());
            if(temp>0)
                smeltingProgress=0;
            isOpen=false;
            markDirty();
            sendUpdate();
            return true;
        }
        //TODO: Consider making bloomery unable to accept more material?
        else if(materialSmelting==definition.getMaterial())
        {
            int iAdding = (material.getCount()*definition.getValue());
            int iRemainder = 0;
            while(iAdding>MAXMATERIAL-numSmelting)
            {
                iAdding = ((material.getCount()-(++iRemainder))*definition.getValue());
            }
            CommonUtil.debug("Bloomery("+pos.getX()+","+pos.getY()+","+pos.getZ()+"): Added "+(iAdding)+ " "+materialSmelting.getName());
            numSmelting+=iAdding;
            if(!isCreative)
                material.setCount(iRemainder);

            if(temp>=100)
                smeltingProgress=0;
            isOpen=false;
            markDirty();
            sendUpdate();
            return true;
        }
        return false;
    }

    public boolean extractMaterial(ItemStack tongs, EntityPlayer thePlayer)
    {
        if(materialSmelting!=null&&smeltingProgress>=100)
        {
            if(isOpen) {
                //Molten Material
                if(tongs!=null) {
                    tongs.setItemDamage(1);
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setString("material", materialSmelting.getName());
                    compound.setInteger("amount", (int) (numSmelting * (1.1 + (random.nextFloat() * 0.5f))));
                    tongs.setTagCompound(compound);
                }
                else
                {
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setString("material", materialSmelting.getName());
                    compound.setInteger("amount", (int) (numSmelting * (1.1 + (random.nextFloat() * 0.5f))));
                    ItemStack stack = new ItemStack(ModItems.moltenMetal,1,0);
                    stack.setTagCompound(compound);
                    if(!thePlayer.inventory.addItemStackToInventory(stack)){
                        //Shouldn't be possible, but meh
                        thePlayer.world.spawnEntity(new EntityItem(thePlayer.world,thePlayer.posX,thePlayer.posY,thePlayer.posZ,stack));
                        thePlayer.setFire(5);
                    }
                }
                materialSmelting = null;
                numSmelting = -1;
                smeltingProgress = -1;
                markDirty();
            }
            else
                isOpen=true;
            sendUpdate();
            return true;
        }
        else open();

        return false;
    }

    public boolean isOpen() {return isOpen;}

    public int getFilled()
    {
        int numCharcoal = getNumCharcoal();
        if(numCharcoal==0)
            return 0;
        if(numCharcoal<84)
            return 1;
        if(numCharcoal<118)
            return 2;
        return 3;
    }

    public void open() {
        if(!isOpen) {
            if (smeltingProgress >= 100 || smeltingProgress < 0)
                isOpen = true;
        }
        else
            isOpen=false;
        sendUpdate();
        markDirty();
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
        if(inventory.extractItem(1,1,false)==null)
            return inventory.extractItem(0,1,false)!=null;
        return true;
    }

    private int tick = random.nextInt(20);

    @Override
    public void update() {
        if(world.isRemote)return;
        if(isBurning())
        {
            ++tick;
            if(tick<20)
                return;
            tick=0;

            boolean updateFlag = false;

            if(timeSinceLastBellows>0)
                --timeSinceLastBellows;

            //TODO: Figure out what the idle temperature is
            int idleTemp = 400 + (getNumCharcoal() << 2);
            if(isOpen)
                idleTemp>>=1;
            //TODO: Modulate temperature towards idle temperature
            temp += (idleTemp-temp)>>3;
            CommonUtil.debug("Bloomery("+pos.getX()+","+pos.getY()+","+pos.getZ()+") Temp: "+temp);

            //TODO: Figure out how much coal was burned

            //10 charcoal at default idle @ 320 912
            //6 charcoal at blow idle @ 60 1111
            //0 charcoal @ ininity 0

            //~32 second/charcoal @912
            //~10 second/charcoal @1111
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
                CommonUtil.debug("\tCharcoal: "+getNumCharcoal());
                CommonUtil.debug("\tNext Burn: "+(1/(COALBURNA*Math.pow(COALBURNB,temp/100.0))));
            }

            if(materialSmelting!=null&&temp>800&&smeltingProgress<100)
            {
                //TODO: Figure out how much smelting progress was made
                //Vanilla smelt time: 10 ticks / 5 seconds
                //full stack: 640 ticks / 320 seconds 5.3 minutes

                //infinity seconds at 800
                //~320 seconds at default idle: 912
                //~60 seconds at blow idle: 1111

                //WARNING: CONVOLUTED EQUATION INBOUND
                //After spending much time and four
                //pages of paper deriving this formula
                //Results are deemed 'close enough'
                smeltingProgress+= (SMELTINGA * (Math.pow(SMELTINGB, (temp-800)/110.0))) - SMELTINGA;
                if(smeltingProgress>=100){
                    updateFlag=true;
                }

                CommonUtil.debug("\tProgress: "+smeltingProgress);
            }
            if(updateFlag) {
                markDirty();
                sendUpdate();
            }
        }
    }
}