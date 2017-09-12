package com.shadowking97.forgecraft.capabilities;

import com.shadowking97.forgecraft.client.models.DynamicModelBiped;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.INBTSerializable;


/**
 * Created by Shadow Bolt on 8/20/2017.
 */
public interface IArmorCapability extends INBTSerializable<NBTTagCompound> {


    boolean isDirty();
    void setDirty();

    ModelBiped getModel(ModelBiped defaultModel);

    void addSubcomponentModels(DynamicModelBiped biped);

    double getDamageReduction();
    double getToughness();
    int getMaterialAmount();

    void invalidate();

    void setParent(IArmorCapability parent);

    void damageItem(int numDamage);
    void damageItem(int numDamage, DamageSource source);

    int getMaxDamage();
    int getDamage();
}
