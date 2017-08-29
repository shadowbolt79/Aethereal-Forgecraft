package com.shadowking97.forgecraft.item;

import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.capabilities.ArmorCapabilityProvider;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by Shadow Bolt on 8/15/2017.
 */
public class ItemBaseArmor extends ItemArmor implements ItemModelProvider{
    protected String name;

    public ItemBaseArmor(String name, EntityEquipmentSlot equipmentSlotIn) {
        super(ArmorMaterial.IRON, -1, equipmentSlotIn);
        this.name = name;

        setUnlocalizedName(name);
        setRegistryName(name);
    }

    @Override
    public void registerItemModel(Item item) {
        Forgecraft.proxy.registerItemRenderer(this,0,name);
    }

    @Override
    public ItemBaseArmor setCreativeTab(CreativeTabs tab){
        super.setCreativeTab(tab);
        return this;
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

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
    }
}
