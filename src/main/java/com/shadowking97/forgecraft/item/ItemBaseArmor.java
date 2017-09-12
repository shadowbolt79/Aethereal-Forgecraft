package com.shadowking97.forgecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.shadowking97.forgecraft.Forgecraft;
import com.shadowking97.forgecraft.capabilities.ArmorCapabilityProvider;
import com.shadowking97.forgecraft.capabilities.IArmorCapability;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.UUID;

/**
 * Created by Shadow Bolt on 8/15/2017.
 */
public class ItemBaseArmor extends ItemArmor implements ItemModelProvider, ISpecialArmor {
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
        IArmorCapability cap = stack.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);

        if(cap!=null&&damage>stack.getItemDamage())
        {
            cap.damageItem(damage-stack.getItemDamage());

            if(cap.isDirty())stack.setTagCompound(cap.serializeNBT());
        }
        else
            super.setDamage(stack, damage);
    }

    @Override
    public int getDamage(ItemStack stack) {
        IArmorCapability cap = stack.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);

        if(cap!=null)
        {
            return cap.getDamage();
        }

        return super.getDamage(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        IArmorCapability cap = stack.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);

        if(cap!=null)
        {
            return cap.getMaxDamage();
        }

        return super.getMaxDamage(stack);
    }

    private static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

        Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();

        if(slot == this.armorType)
        {
            IArmorCapability cap = stack.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);
            if(cap==null) {
                multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor modifier", (double) this.damageReduceAmount, 0));
                multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor toughness", (double) this.toughness, 0));
            }else{
                multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor modifier",  cap.getDamageReduction(), 0));
                multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor toughness", cap.getToughness(), 0));
            }
        }

        return multimap;
    }

    //ISpecialArmor properties

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {

        IArmorCapability capability = armor.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);

        if(capability!=null) {
            double drModified = capability.getDamageReduction();
            drModified = Math.max(drModified/5,drModified - (damage/(2+(capability.getToughness()/4))))/25D;

            return new ArmorProperties(capability.getMaterialAmount(),drModified, (int) (capability.getMaterialAmount()*(drModified)));
        }

        return new ArmorProperties(0, ((ItemArmor)armor.getItem()).damageReduceAmount / 25D, Integer.MAX_VALUE);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        IArmorCapability capability = armor.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);

        if(capability!=null) {
            return (int) capability.getDamageReduction();
        }

        return 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        IArmorCapability capability = stack.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);

        if(capability!=null) {
            capability.damageItem(damage, source);
        }
    }
}
