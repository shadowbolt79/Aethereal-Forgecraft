package com.shadowking97.forgecraft.network;

import com.shadowking97.forgecraft.tiles.TileForge;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Shadow Bolt on 9/3/2017.
 */
public class PacketRequestUpdateForge implements IMessage {
    private BlockPos pos;
    private int dimension;

    public PacketRequestUpdateForge(BlockPos pos, int dimension)
    {
        this.pos = pos;
        this.dimension = dimension;
    }

    public PacketRequestUpdateForge(TileForge tileForge) {
        this(tileForge.getPos(),tileForge.getWorld().provider.getDimension());
    }

    public PacketRequestUpdateForge(){}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
    }

    public static class Handler implements IMessageHandler<PacketRequestUpdateForge, PacketUpdateForge> {

        @Override
        public PacketUpdateForge onMessage(PacketRequestUpdateForge message, MessageContext ctx) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.dimension);
            TileForge te = (TileForge) world.getTileEntity(message.pos);
            if (te != null) {
                return new PacketUpdateForge(te);
            } else {
                return null;
            }
        }

    }
}
