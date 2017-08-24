package com.shadowking97.forgecraft.network;

import com.shadowking97.forgecraft.block.ModBlocks;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import com.shadowking97.forgecraft.tiles.TileBloomery;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Shadow Bolt on 8/2/2017.
 */
public class PacketUpdateBloomery implements IMessage {
    private BlockPos pos;

    private int bloomeryTemp;
    private int bloomeryCharcoal;

    private String materialSmelting;
    private float smeltingProgress;
    private int numSmelting;

    private boolean isOpen;

    public PacketUpdateBloomery(BlockPos pos, int temp, int charcoal, String smelting, float progress, int amount, boolean open)
    {
        this.pos=pos;
        this.bloomeryTemp=temp;
        this.bloomeryCharcoal=charcoal;
        this.materialSmelting=smelting;
        this.smeltingProgress=progress;
        this.numSmelting=amount;
        this.isOpen=open;
    }

    public PacketUpdateBloomery(TileBloomery te)
    {
        this(te.getPos(),te.getTemp(),te.getNumCharcoal(),te.getMaterialSmelting()==null?"null":te.getMaterialSmelting().getName(),te.getSmeltingProgress(),te.getNumSmelting(),te.isOpen());
    }

    public PacketUpdateBloomery(){}


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(bloomeryTemp);
        buf.writeInt(bloomeryCharcoal);
        char[] charArray = materialSmelting.toCharArray();
        buf.writeInt(charArray.length);
        for(char c:charArray)
            buf.writeChar(c);
        buf.writeFloat(smeltingProgress);
        buf.writeInt(numSmelting);
        buf.writeBoolean(isOpen);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        bloomeryTemp = buf.readInt();
        bloomeryCharcoal = buf.readInt();
        int i = buf.readInt();
        materialSmelting="";
        for(int j = 0; j<i;j++)
            materialSmelting+=buf.readChar();
        smeltingProgress=buf.readFloat();
        numSmelting=buf.readInt();
        isOpen=buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketUpdateBloomery, IMessage> {

        @Override
        public IMessage onMessage(final PacketUpdateBloomery message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                WorldClient client = Minecraft.getMinecraft().theWorld;
                TileEntity tileEntity = client.getTileEntity(message.pos);
                if(tileEntity!=null&&tileEntity instanceof TileBloomery) {
                    TileBloomery te = (TileBloomery)tileEntity;
                    te.setTemp(message.bloomeryTemp);
                    te.setNumCharcoal(message.bloomeryCharcoal);
                    te.setMaterialSmelting(MaterialStore.INSTANCE.getMaterialByName(message.materialSmelting));
                    te.setSmeltingProgress(message.smeltingProgress);
                    te.setNumSmelting(message.numSmelting);
                    te.setOpen(message.isOpen);

                    IBlockState state = client.getBlockState(message.pos.down());
                    client.notifyBlockUpdate(message.pos.down(),state, ModBlocks.bloomery.getActualState(state,client,message.pos.down()),3);
                }
            });
            return null;
        }

    }
}
