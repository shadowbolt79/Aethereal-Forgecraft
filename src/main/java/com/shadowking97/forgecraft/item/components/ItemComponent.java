package com.shadowking97.forgecraft.item.components;

import com.google.common.collect.Multimap;
import com.shadowking97.forgecraft.capabilities.ArmorCapabilityProvider;
import com.shadowking97.forgecraft.capabilities.IArmorCapability;
import com.shadowking97.forgecraft.item.ItemBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Container item to store multipart component information
 * Allows parts to be removed in item form
 *
 * Created by Shadow Bolt on 7/19/2017.
 */
public class ItemComponent extends ItemBase {

    boolean isArmorComponent = false;

    public ItemComponent(String name) {
        super(name);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    public ItemComponent setArmor()
    {
        isArmorComponent=true;
        return this;
    }

    public boolean isArmorComponent()
    {
        return isArmorComponent;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        if(stack.getItem() instanceof ItemComponent) {
            if (((ItemComponent) stack.getItem()).isArmorComponent) {
                ArmorCapabilityProvider.Provider provider = new ArmorCapabilityProvider.Provider();
                provider.deserializeNBT(nbt);

                //forces the armor stats client-side
                if (provider.isDirty())
                    stack.setTagCompound(provider.serializeNBT());
                return provider;
            } else {
                //TODO: Tools
            }
        }
        return super.initCapabilities(stack, nbt);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return EnumActionResult.PASS;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        return new ActionResult(EnumActionResult.PASS, itemStackIn);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        //TODO: More advanced name
        return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
    }

    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in this item,
     * but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    public int getMaxDamage(ItemStack stack)
    {
        if(isArmorComponent)
        {
            IArmorCapability cap = stack.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY,EnumFacing.WEST);

            if(cap!=null)
            {
                cap.getMaxDamage();
            }
        }
        else
        {
            //TODO: Tools
        }

        return getMaxDamage();
    }
}