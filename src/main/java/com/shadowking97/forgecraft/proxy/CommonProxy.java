package com.shadowking97.forgecraft.proxy;

/**
 * Created by Shadow Bolt on 7/16/2017.
 */
import com.shadowking97.forgecraft.item.components.ComponentGenerator;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class CommonProxy {
    public void initEventListeners() {
    }

    public void init(){
        ComponentGenerator.INSTANCE.generateComponents();
    }

    public void registerItemRenderer(Item item, int meta, String id) {
    }

    public void registerBlockRenderer(ItemBlock item, int meta, String id) {
    }

    public void registerItemColor(Item item) {
    }

    public void registerRenderers(){}

    public String localize(String unlocalized, Object... args){
        return I18n.translate(unlocalized,args);
    }
}
