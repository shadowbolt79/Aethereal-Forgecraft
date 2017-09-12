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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        private static Random r = new Random();

        boolean dirty = false;
        boolean needsRemodel = true;

        //Used for materials that have multiple colors, or plausibly custom.
        //Also used for icons for item components
        ItemStack colorItem = null;

        double damageReduction=-1;
        double toughness=-1;
        int materialAmount = -1;

        int damage = -1;
        int maxDamage = -1;

        List<ItemStack> subComponents;

        IArmorCapability parent = null;

        @Override
        public void setParent(IArmorCapability parent) {
            this.parent = parent;
        }

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
            //Should only ever be called on the highest level armor model
            //meaning only one instance of theModel should be created
            //the others should only be instances of components, added on during
            //remodels
            if(theModel==null) {
                theModel = new DynamicModelBiped();

                if(componentDefinition!=null&&componentMaterial!=null)
                {
                        ModelRenderer renderer = null;

                        switch (componentDefinition.getPart()) {
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

                        if (renderer != null)
                            theModel.addChild(renderer, componentDefinition.getMaterialModel(componentMaterial));
                }
            }

            if(needsRemodel)
            {
                needsRemodel=false;

                //reset the model
                theModel.resetModel();

                //add all applicable subcomponents
                if(subComponents!=null) {
                    for (ItemStack subComponent : subComponents) {
                        if (subComponent.getItemDamage() >= subComponent.getMaxDamage()) continue;

                        IArmorCapability cap = subComponent.getCapability(ARMOR_CAPABILITY, EnumFacing.WEST);
                        if (cap != null) {
                            cap.addSubcomponentModels(theModel);
                        }
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
        public double getDamageReduction() {
            if(damageReduction!=-1)return damageReduction;

            damageReduction=0;

            if(componentDefinition!=null&&componentMaterial!=null)
            {
                int d = Math.min(10,componentMaterial.getStrength());

                damageReduction = (d*componentDefinition.getMaterialAmount())/100.0;
            }

            if(subComponents!=null)
            {
                for(ItemStack subcomp : subComponents)
                {
                    IArmorCapability cap = subcomp.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);
                    damageReduction+=cap.getDamageReduction();
                }
            }

            return damageReduction;
        }

        @Override
        public double getToughness() {
            if(toughness!=-1)return toughness;

            toughness=0;

            if(componentDefinition!=null&&componentMaterial!=null)
            {
                int t = componentMaterial.getStrength()-8;
                if(t>0)
                    toughness = (t*componentDefinition.getMaterialAmount())/100.0;
            }

            if(subComponents!=null)
            {
                for(ItemStack subcomp : subComponents)
                {
                    IArmorCapability cap = subcomp.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);
                    toughness+=cap.getToughness();
                }
            }

            return toughness;
        }

        @Override
        public int getMaterialAmount() {
            if(materialAmount!=-1)return materialAmount;

            materialAmount=0;

            if(componentDefinition!=null&&componentMaterial!=null)
            {
                materialAmount = componentDefinition.getMaterialAmount();
            }

            if(subComponents!=null)
            {
                for(ItemStack subcomp : subComponents)
                {
                    IArmorCapability cap = subcomp.getCapability(ArmorCapabilityProvider.ARMOR_CAPABILITY, EnumFacing.WEST);
                    damageReduction+=cap.getMaterialAmount();
                }
            }
            return 0;
        }

        /***
         * Called when something changes. (Component broke, component added or removed, ect)
         */
        @Override
        public void invalidate() {
            materialAmount =-1;
            damageReduction=-1;
            toughness=-1;
            needsRemodel=true;
            maxDamage=-1;
            damage=-1;
            parent.invalidate();
            setDirty();
        }

        @Override
        public void addSubcomponentModels(DynamicModelBiped model)
        {
            if(componentDefinition!=null&&componentMaterial!=null)
            {
                    ModelRenderer renderer = null;

                    switch (componentDefinition.getPart()) {
                        case BIPED_HEAD:
                            renderer = model.bipedHead;
                            break;
                        case BIPED_CHEST:
                            renderer = model.bipedBody;
                            break;
                        case BIPED_ARM_LEFT:
                            renderer = model.bipedLeftArm;
                            break;
                        case BIPED_ARM_RIGHT:
                            renderer = model.bipedRightArm;
                            break;
                        case BIPED_LEG_LEFT:
                            renderer = model.bipedLeftLeg;
                            break;
                        case BIPED_LEG_RIGHT:
                            renderer = model.bipedRightLeg;
                    }

                    if (renderer != null)
                        model.addSubcomponent(renderer, componentDefinition.getMaterialModel(componentMaterial));
            }

            if(subComponents!=null) {
                for (ItemStack subComponent : subComponents) {
                    if (subComponent.getItemDamage() >= subComponent.getMaxDamage()) continue;

                    IArmorCapability cap = subComponent.getCapability(ARMOR_CAPABILITY, EnumFacing.WEST);
                    if (cap != null) {
                        cap.addSubcomponentModels(model);
                    }
                }
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            dirty=false;

            if(componentMaterial!=null&&componentDefinition!=null)
            {
                    compound.setString("component", componentDefinition.getName());
                    compound.setString("material", componentMaterial.getName());
            }

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


            if(nbt.hasKey("component"))
            {
                    componentDefinition =(ComponentGenerator.INSTANCE.getComponent(nbt.getString("component"+i)));
                    componentMaterial =(MaterialStore.INSTANCE.getMaterialByName(nbt.getString("material"+i)));
            }

            while(nbt.hasKey("subComponent"+(i)))
            {
                if(subComponents==null)subComponents=new ArrayList<>();

                subComponents.add(new ItemStack(nbt.getCompoundTag("subComponent"+i)));

                ++i;
            }

            if(nbt.hasKey("colorItem"))
            {
                colorItem = new ItemStack(nbt.getCompoundTag("colorItem"));
            }
        }

        /***
         * Should only be called on top level armor. Used to damage component itemstacks.
         * @param numDamage
         */
        @Override
        public void damageItem(int numDamage) {
            if(subComponents!=null)
            {
                int numComponents = subComponents.size();

                while(numDamage>0||this.getDamage()>=this.getMaxDamage())
                {
                    ItemStack component = subComponents.get(r.nextInt(numComponents));

                    if(component.getItemDamage()<component.getMaxDamage())
                    {
                        int toDamage = Math.min(numDamage,component.getMaxDamage()-component.getItemDamage());
                        component.setItemDamage(component.getItemDamage()+toDamage);

                        if(component.getItemDamage()>=component.getMaxDamage())invalidate();

                        numDamage-=toDamage;
                        damage+=toDamage;
                    }
                }
            }
        }

        /***
         * Should only be called on top level armor. Used to damage component itemstacks.
         * @param numDamage
         */
        @Override
        public void damageItem(int numDamage, DamageSource source) {
            if(subComponents!=null)
            {
                int numComponents = subComponents.size();

                while(numDamage>0||this.getDamage()>=this.getMaxDamage())
                {
                    ItemStack component = subComponents.get(r.nextInt(numComponents));

                    if(component.getItemDamage()<component.getMaxDamage())
                    {
                        int toDamage = Math.min(numDamage,component.getMaxDamage()-component.getItemDamage());
                        component.setItemDamage(component.getItemDamage()+toDamage);

                        if(component.getItemDamage()>=component.getMaxDamage())invalidate();

                        numDamage-=toDamage;
                        damage+=toDamage;
                    }
                }
            }
        }

        @Override
        public int getDamage()
        {
            if(damage!=-1)return damage;

            damage = 0;

            if(subComponents!=null)
            {
                for(ItemStack component: subComponents)
                    damage+=component.getItemDamage();
            }

            return damage;
        }

        @Override
        public int getMaxDamage() {
            if(maxDamage!=-1)return maxDamage;

            maxDamage = 0;

            if(componentDefinition!=null&&componentMaterial!=null)
            {
                maxDamage=componentDefinition.getMaterialAmount()*componentMaterial.getDurability();
            }

            if(subComponents!=null)
            {
                for(ItemStack component: subComponents)
                    maxDamage+=component.getItemDamage();
            }

            return maxDamage;
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