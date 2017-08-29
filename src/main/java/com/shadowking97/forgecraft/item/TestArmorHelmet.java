package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.capabilities.ArmorCapabilityProvider;
import com.shadowking97.forgecraft.client.models.ComponentModelRenderer;
import com.shadowking97.forgecraft.item.components.ComponentDefinition;
import com.shadowking97.forgecraft.item.components.ComponentGenerator;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Random;

/**
 * Created by Shadow Bolt on 8/19/2017.
 */
public class TestArmorHelmet extends ItemBaseArmor {
    public TestArmorHelmet() {
        super("testHelmet", EntityEquipmentSlot.HEAD);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        ArmorCapabilityProvider.Provider provider = new ArmorCapabilityProvider.Provider();
        provider.deserializeNBT(nbt);

        //forces the armor stats client-side
        if(provider.isDirty())
            stack.setTagCompound(provider.serializeNBT());
        return provider;
    }



    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "skforgecraft:textures/atlas/components.png";
    }

    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {

        return itemStack.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY,null).getModel(_default);
    }

}