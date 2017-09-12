package com.shadowking97.forgecraft.tiles;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.interfaces.IHammerableItem;
import com.shadowking97.forgecraft.network.PacketRequestUpdateAnvil;
import com.shadowking97.forgecraft.network.PacketUpdateAnvil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * Created by Shadow Bolt on 8/4/2017.
 */
public class TileAnvil extends TileEntity {
    public ItemStackHandler inventory = new ItemStackHandler(1) {

        //Sanity checks to make sure hoppers can't do weird things
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(stack!=null)
            {
                if(stack.getItem() instanceof IHammerableItem)
                    return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if(stack!=null)
            {
                if(stack.getItem() instanceof IHammerableItem)
                    super.setStackInSlot(slot, stack);
            }
            else
                super.setStackInSlot(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot)
        {

            if(!world.isRemote)
            {
                Forgecraft.network.sendToAllAround(new PacketUpdateAnvil(TileAnvil.this), new NetworkRegistry.TargetPoint(world.provider.getDimension(),pos.getX(), pos.getY(), pos.getZ(), 64));
                TileAnvil.this.markDirty();
            }
        }
    };

    @Override
    public void onLoad() {
        if (world.isRemote) {
            Forgecraft.network.sendToServer(new PacketRequestUpdateAnvil(this));
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos(), getPos().add(1, 1.1, 1));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability,facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T)inventory : super.getCapability(capability,facing);
    }
}
