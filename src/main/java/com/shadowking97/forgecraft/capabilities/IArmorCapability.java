package com.shadowking97.forgecraft.capabilities;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;


/**
 * Created by Shadow Bolt on 8/20/2017.
 */
public interface IArmorCapability extends INBTSerializable<NBTTagCompound> {


    boolean isDirty();
    void setDirty();

    ModelBiped getModel(ModelBiped defaultModel);
}
