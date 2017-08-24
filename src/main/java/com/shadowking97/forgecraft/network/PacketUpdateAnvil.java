package com.shadowking97.forgecraft.network;

import com.shadowking97.forgecraft.tiles.TileAnvil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Shadow Bolt on 8/7/2017.
 */
public class PacketUpdateAnvil implements IMessage {
    private ItemStack itemStack;
    private BlockPos pos;

    public PacketUpdateAnvil(BlockPos pos, ItemStack stack)
    {
        this.itemStack=stack;
        this.pos = pos;
    }

    public PacketUpdateAnvil(TileAnvil tileAnvil) {
        this(tileAnvil.getPos(),tileAnvil.inventory.getStackInSlot(0));
    }

    public PacketUpdateAnvil(){}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        itemStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeItemStack(buf,itemStack);
    }

    public static class Handler implements IMessageHandler<PacketUpdateAnvil, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateAnvil message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                TileAnvil te = (TileAnvil)Minecraft.getMinecraft().theWorld.getTileEntity(message.pos);
                te.inventory.setStackInSlot(0, message.itemStack);
            });
            return null;
        }
    }
}
