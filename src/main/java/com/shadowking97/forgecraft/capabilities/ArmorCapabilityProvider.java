package com.shadowking97.forgecraft.capabilities;

import com.shadowking97.forgecraft.client.models.DynamicModelBiped;
import com.shadowking97.forgecraft.item.components.ComponentDefinition;
import com.shadowking97.forgecraft.item.components.ComponentGenerator;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.*;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shadow Bolt on 8/20/2017.
 */
public class ArmorCapabilityProvider{

    public static void init()
    {
        CapabilityManager.INSTANCE.register(IArmorCapability.class, new Capability.IStorage<IArmorCapability>(){
            @Override
            public NBTBase writeNBT(Capability<IArmorCapability> capability, IArmorCapability instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IArmorCapability> capability, IArmorCapability instance, EnumFacing side, NBTBase nbt) {
                if(nbt instanceof NBTTagCompound)
                    instance.deserializeNBT((NBTTagCompound)nbt);
            }
        },DefaultImpl.class);
    }


    @CapabilityInject(IArmorCapability.class)
    public static Capability<IArmorCapability> ARMOR_CAPABILITY = null; // This would probably be somewhere else

    public enum ModelPart
    {
        BIPED_HEAD,
        BIPED_CHEST,
        BIPED_ARM_LEFT,
        BIPED_ARM_RIGHT,
        BIPED_LEG_LEFT,
        BIPED_LEG_RIGHT
    }


    private static class DefaultImpl implements IArmorCapability
    {
        DynamicModelBiped theModel = null;

        protected ComponentDefinition componentDefinition = null;
        protected ItemMaterial componentMaterial = null;

        boolean dirty = false;
        boolean needsRemodel = true;

        //Used for materials that have multiple colors, or plausibly custom.
        ItemStack colorItem = null;

        List<ItemStack> subComponents;

        @Override
        public boolean isDirty() {
            return dirty;
        }

        @Override
        public void setDirty() {
            dirty=true;
        }

        @Override
        public ModelBiped getModel(ModelBiped defaultModel) {
            if(theModel==null) {
                theModel = new DynamicModelBiped();

                if(componentDefinition!=null&&componentMaterial!=null)
                {
                    ModelRenderer renderer = null;

                    switch(componentDefinition.getPart())
                    {
                        case BIPED_HEAD:
                            renderer = theModel.bipedHead;
                            break;
                        case BIPED_CHEST:
                            renderer = theModel.bipedBody;
                            break;
                        case BIPED_ARM_LEFT:
                            renderer = theModel.bipedLeftArm;
                            break;
                        case BIPED_ARM_RIGHT:
                            renderer = theModel.bipedRightArm;
                            break;
                        case BIPED_LEG_LEFT:
                            renderer = theModel.bipedLeftLeg;
                            break;
                        case BIPED_LEG_RIGHT:
                            renderer = theModel.bipedRightLeg;
                    }

                    if(renderer!=null)
                        theModel.addChild(renderer,componentDefinition.getMaterialModel(componentMaterial).copyOf(theModel));
                }
            }

            if(needsRemodel)
            {
                needsRemodel=false;
                theModel.resetModel();

                for(ItemStack subComponent: subComponents)
                {
                    if(subComponent.getItemDamage()>=subComponent.getMaxDamage())continue;

                    IArmorCapability cap = subComponent.getCapability(ARMOR_CAPABILITY,EnumFacing.WEST);
                    if(cap!=null)
                    {
                        theModel.combineWith(cap.getModel(null));
                    }
                }
            }

            if(defaultModel!=null) {
                theModel.setModelAttributes(defaultModel);
                theModel.bipedHead.rotateAngleX=defaultModel.bipedHeadwear.rotateAngleX;
                theModel.bipedHead.rotateAngleY=defaultModel.bipedHeadwear.rotateAngleY;
                theModel.bipedHead.rotateAngleZ=defaultModel.bipedHeadwear.rotateAngleZ;
            }

            return theModel;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            dirty=false;

            compound.setString("component",componentDefinition.getName());
            compound.setString("material",componentMaterial.getName());

            if(subComponents!=null) {
                for (int i = 0; i < subComponents.size(); i++) {
                    compound.setTag("subComponent" + i, subComponents.get(i).writeToNBT(new NBTTagCompound()));
                }
            }

            if(colorItem !=null)
            {
                compound.setTag("colorItem", colorItem.writeToNBT(new NBTTagCompound()));
            }
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            if(nbt==null) {
                //generateRandomArmor();
                return;
            }
            int i = 0;
            componentDefinition = ComponentGenerator.INSTANCE.getComponent(nbt.getString("component"));
            componentMaterial = MaterialStore.INSTANCE.getMaterialByName(nbt.getString("material"));

            while(nbt.hasKey("subComponent"+(i)))
            {
                if(subComponents==null)subComponents=new ArrayList<>();

                subComponents.add(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("subComponent"+i)));

                ++i;
            }

            if(nbt.hasKey("colorItem"))
            {
                colorItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("colorItem"));
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound>
    {
        private IArmorCapability cap = new DefaultImpl();

        public boolean isDirty()
        {
            return cap.isDirty();
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == ARMOR_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if(capability==ARMOR_CAPABILITY)
                return (T)cap;
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return cap.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            cap.deserializeNBT(nbt);
        }
    }
}