package com.shadowking97.forgecraft.network;

import com.shadowking97.forgecraft.tiles.TileAnvil;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Shadow Bolt on 8/7/2017.
 */
public class PacketRequestUpdateAnvil implements IMessage {
    private BlockPos pos;
    private int dimension;

    public PacketRequestUpdateAnvil(BlockPos pos, int dimension)
    {
        this.pos = pos;
        this.dimension=dimension;
    }

    public PacketRequestUpdateAnvil(TileAnvil tileAnvil) {
        this(tileAnvil.getPos(),tileAnvil.getWorld().provider.getDimension());
    }

    public PacketRequestUpdateAnvil(){}

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

    public static class Handler implements IMessageHandler<PacketRequestUpdateAnvil, PacketUpdateAnvil> {

        @Override
        public PacketUpdateAnvil onMessage(PacketRequestUpdateAnvil message, MessageContext ctx) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            TileAnvil te = (TileAnvil) world.getTileEntity(message.pos);
            if (te != null)
                return new PacketUpdateAnvil(te);
            else
                return null;

        }

    }
}
