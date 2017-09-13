package com.shadowking97.forgecraft.proxy;

/**
 * Created by Shadow Bolt on 7/16/2017.
 */

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.client.ComponentTextureMap;
import com.shadowking97.forgecraft.client.MapHolder;
import com.shadowking97.forgecraft.client.TESRAnvil;
import com.shadowking97.forgecraft.events.ClientEventListeners;
import com.shadowking97.forgecraft.item.ItemBaseColorable;
import com.shadowking97.forgecraft.tiles.TileAnvil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class ClientProxy extends CommonProxy {

    @Override
    public void initEventListeners() {
        super.initEventListeners();
        MinecraftForge.EVENT_BUS.register(new ClientEventListeners());
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Forgecraft.MODID + ":" + id, "inventory"));
    }

    @Override
    public void registerBlockRenderer(ItemBlock item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Forgecraft.MODID + ":" + id, "inventory"));
    }

    @Override
    public void registerItemColor(Item item) {
        if(item instanceof ItemBaseColorable)
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((ItemBaseColorable)item).getMaterial(),item);
    }

    @Override
    public void init() {
        try {
            FMLLog.log(Level.INFO,"Creating Component Texture Map");
            MapHolder.textureMapComponents = new ComponentTextureMap("textures");

            MapHolder.textureMapComponents.setMipmapLevels(Minecraft.getMinecraft().gameSettings.mipmapLevels);
        }catch (Exception e){
            FMLLog.log(Level.FATAL,e,"FORGECRAFT: UNABLE TO GET TEXTURE SPRITE");
        }
        super.init();
        if(MapHolder.textureMapComponents!=null) {
            Minecraft.getMinecraft().getTextureManager().loadTickableTexture(MapHolder.LOCATION_COMPONENTS_TEXTURE, MapHolder.textureMapComponents);
            Minecraft.getMinecraft().getTextureManager().getTexture(MapHolder.LOCATION_COMPONENTS_TEXTURE).setBlurMipmap(false,false);
            MapHolder.textureMapComponents.setBlurMipmapDirect(false, Minecraft.getMinecraft().gameSettings.mipmapLevels > 0);
        }
    }

    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileAnvil.class, new TESRAnvil());
    }


    @Override
    public String localize(String unlocalized, Object... args)
    {
        return I18n.format(unlocalized,args);
    }
}
