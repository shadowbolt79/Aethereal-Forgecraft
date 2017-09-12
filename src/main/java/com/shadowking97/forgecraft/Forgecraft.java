package com.shadowking97.forgecraft;

import com.shadowking97.forgecraft.block.ModBlocks;
import com.shadowking97.forgecraft.item.ModItems;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import com.shadowking97.forgecraft.network.*;
import com.shadowking97.forgecraft.proxy.CommonProxy;
import com.shadowking97.forgecraft.util.CommonUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Shadow Bolt on 7/16/2017.
 */

@Mod(modid = Forgecraft.MODID, name = Forgecraft.NAME,version = Forgecraft.VERSION, acceptedMinecraftVersions = "[1.12.1]")
public class Forgecraft {
    public static final String MODID = "skforgecraft";
    public static final String NAME = "Aetherial Forge";
    public static final String VERSION = "0.1a";

    @SidedProxy(serverSide = "com.shadowking97.forgecraft.proxy.CommonProxy",clientSide = "com.shadowking97.forgecraft.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static Forgecraft instance;
    public static SimpleNetworkWrapper network;

    int networkDiscriminator = 0;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CommonUtil.setLogger(event.getModLog());

        CommonUtil.info(NAME + " is loading!");

        ModItems.init();
        ModBlocks.init();
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(new PacketUpdateBloomery.Handler(), PacketUpdateBloomery.class, networkDiscriminator++, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateBloomery.Handler(), PacketRequestUpdateBloomery.class, networkDiscriminator++, Side.SERVER);
        network.registerMessage(new PacketUpdateAnvil.Handler(), PacketUpdateAnvil.class, networkDiscriminator++, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateAnvil.Handler(), PacketRequestUpdateAnvil.class, networkDiscriminator++, Side.SERVER);
        network.registerMessage(new PacketUpdateForge.Handler(), PacketUpdateForge.class, networkDiscriminator++, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateForge.Handler(),PacketRequestUpdateForge.class,networkDiscriminator++,Side.SERVER);

        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        proxy.initEventListeners();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MaterialStore.INSTANCE.init();
    }
}