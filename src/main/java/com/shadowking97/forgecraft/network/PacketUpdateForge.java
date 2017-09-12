package com.shadowking97.forgecraft.network;

import com.shadowking97.forgecraft.block.Forge;
import com.shadowking97.forgecraft.tiles.TileForge;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Shadow Bolt on 9/3/2017.
 */
public class PacketUpdateForge implements IMessage {
    private BlockPos pos;

    private int forgeTemp;
    private int forgeCharcoal;

    public PacketUpdateForge(BlockPos pos, int temp, int charcoal)
    {
        this.pos=pos;
        this.forgeTemp=temp;
        this.forgeCharcoal=charcoal;
    }

    public PacketUpdateForge(TileForge tileForge) {
        this(tileForge.getPos(),tileForge.getTemp(),tileForge.getNumCharcoal());
    }

    public PacketUpdateForge(){}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        forgeTemp = buf.readInt();
        forgeCharcoal = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(forgeTemp);
        buf.writeInt(forgeCharcoal);
    }

    public static class Handler implements IMessageHandler<PacketUpdateForge, IMessage> {

        @Override
        public IMessage onMessage(final PacketUpdateForge message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                WorldClient client = Minecraft.getMinecraft().world;
                TileEntity tileEntity = client.getTileEntity(message.pos);
                if(tileEntity!=null&&tileEntity instanceof TileForge) {
                    TileForge te = (TileForge)tileEntity;
                    te.setTemp(message.forgeTemp);
                    te.setNumCharcoal(message.forgeCharcoal);

                    Forge.notifyAllBlocks(client,message.pos);
                }
            });
            return null;
        }

    }
}
