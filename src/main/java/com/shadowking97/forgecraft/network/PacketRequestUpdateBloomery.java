package com.shadowking97.forgecraft.network;

import com.shadowking97.forgecraft.tiles.TileBloomery;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Shadow Bolt on 8/2/2017.
 */
public class PacketRequestUpdateBloomery implements IMessage {
    private BlockPos pos;
    private int dimension;

    public PacketRequestUpdateBloomery(BlockPos pos, int dimension)
    {
        this.pos = pos;
        this.dimension = dimension;
    }

    public PacketRequestUpdateBloomery(TileBloomery te)
    {
        this(te.getPos(),te.getWorld().provider.getDimension());
    }

    public PacketRequestUpdateBloomery() {}

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketRequestUpdateBloomery, PacketUpdateBloomery> {

        @Override
        public PacketUpdateBloomery onMessage(PacketRequestUpdateBloomery message, MessageContext ctx) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            TileBloomery te = (TileBloomery) world.getTileEntity(message.pos);
            if (te != null) {
                return new PacketUpdateBloomery(te);
            } else {
                return null;
            }
        }

    }
}
