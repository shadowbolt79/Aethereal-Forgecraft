package com.shadowking97.forgecraft.capabilities;

import com.shadowking97.forgecraft.client.models.ComponentModelRenderer;
import com.shadowking97.forgecraft.item.ModItems;
import com.shadowking97.forgecraft.item.components.ComponentDefinition;
import com.shadowking97.forgecraft.item.components.ComponentGenerator;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.*;


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

    private static class DefaultImpl implements IArmorCapability
    {
        ModelBiped theModel = null;
        List<String> componentNames = new ArrayList<>();
        List<String> materialNames = new ArrayList<>();
        boolean hasGems = false;

        boolean dirty = false;

        ItemStack furColor = null;


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
            if(theModel==null)
            {
                theModel = new ModelBiped(){
                    @Override
                    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                        if(entityIn instanceof EntityArmorStand)
                        {
                            netHeadYaw=0;
                        }
                        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    }
                };

                if(componentNames.isEmpty())
                {
                    componentNames.add("metalCap");
                    Random r = new Random();
                    hasGems = r.nextBoolean();
                    if(hasGems) {
                        componentNames.add("sockets");
                        componentNames.add("leftGem");
                        componentNames.add("rightGem");
                    }
                    switch(r.nextInt(3))
                    {
                        case 0:
                            materialNames.add("Iron");
                            if(hasGems) {
                                materialNames.add("Iron");
                            }
                            break;
                        case 1:
                            materialNames.add("Gold");
                            if(hasGems) {
                                materialNames.add("Gold");
                            }
                            break;
                        default:
                            materialNames.add("Steel");
                            if(hasGems) {
                                materialNames.add("Steel");
                            }
                    }
                    if(hasGems)
                    {
                        switch(r.nextInt(7))
                        {
                            case 0:
                                materialNames.add("Diamond");
                                materialNames.add("Diamond");
                                break;
                            case 1:
                                materialNames.add("Emerald");
                                materialNames.add("Emerald");
                                break;
                            case 2:
                                materialNames.add("Lapis Lazuli");
                                materialNames.add("Lapis Lazuli");
                                break;
                            case 3:
                                materialNames.add("Prismarine");
                                materialNames.add("Prismarine");
                                break;
                            case 4:
                                materialNames.add("Quartz");
                                materialNames.add("Quartz");
                                break;
                            case 5:
                                materialNames.add("Redstone");
                                materialNames.add("Redstone");
                                break;
                            default:
                                materialNames.add("Glowstone");
                                materialNames.add("Glowstone");
                        }
                    }

                }

                ComponentDefinition mainComponent = ComponentGenerator.INSTANCE.getComponent(componentNames.get(0));

                theModel.bipedHead = new ComponentModelRenderer(theModel,"test").setMaterial(MaterialStore.INSTANCE.getMaterialByName(materialNames.get(0))).setModel(mainComponent.getModel());

                if(hasGems) {
                    theModel.bipedHead.addChild(new ComponentModelRenderer(theModel, "test").setMaterial(MaterialStore.INSTANCE.getMaterialByName(materialNames.get(1))).setModel(mainComponent.getSubComponent(componentNames.get(1)).getModel()));
                    theModel.bipedHead.addChild(new ComponentModelRenderer(theModel, "test").setMaterial(MaterialStore.INSTANCE.getMaterialByName(materialNames.get(2))).setModel(mainComponent.getSubComponent(componentNames.get(2)).getModel()));
                    theModel.bipedHead.addChild(new ComponentModelRenderer(theModel, "test").setMaterial(MaterialStore.INSTANCE.getMaterialByName(materialNames.get(3))).setModel(mainComponent.getSubComponent(componentNames.get(3)).getModel()));
                }

                if(furColor!=null)
                {
                    theModel.bipedHead.addChild(new ComponentModelRenderer(theModel, "test").setMaterial(MaterialStore.INSTANCE.getMaterialByName("Fur")).setModel(mainComponent.getSubComponent("furLining").getModel()).setRenderStack(furColor));
                }

                theModel.bipedHeadwear.isHidden=true;
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
            for(int i = 0; i <componentNames.size(); i++)
            {
                compound.setString("component"+i,componentNames.get(i));
                compound.setString("material"+i,materialNames.get(i));
            }
            compound.setBoolean("hasGems",hasGems);
            if(furColor!=null)
            {
                compound.setInteger("furColor",furColor.getMetadata());
            }
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            if(nbt==null) {
                if(componentNames.isEmpty())
                {
                    setDirty();
                    componentNames.add("metalCap");
                    Random r = new Random();
                    hasGems = r.nextBoolean();
                    if(hasGems) {
                        componentNames.add("sockets");
                        componentNames.add("leftGem");
                        componentNames.add("rightGem");
                    }
                    switch(r.nextInt(3))
                    {
                        case 0:
                            materialNames.add("Iron");
                            if(hasGems) {
                                materialNames.add("Iron");
                            }
                            break;
                        case 1:
                            materialNames.add("Gold");
                            if(hasGems) {
                                materialNames.add("Gold");
                            }
                            break;
                        default:
                            materialNames.add("Steel");
                            if(hasGems) {
                                materialNames.add("Steel");
                            }
                    }
                    if(hasGems)
                    {
                        switch(r.nextInt(7))
                        {
                            case 0:
                                materialNames.add("Diamond");
                                materialNames.add("Diamond");
                                break;
                            case 1:
                                materialNames.add("Emerald");
                                materialNames.add("Emerald");
                                break;
                            case 2:
                                materialNames.add("Lapis Lazuli");
                                materialNames.add("Lapis Lazuli");
                                break;
                            case 3:
                                materialNames.add("Prismarine");
                                materialNames.add("Prismarine");
                                break;
                            case 4:
                                materialNames.add("Quartz");
                                materialNames.add("Quartz");
                                break;
                            case 5:
                                materialNames.add("Redstone");
                                materialNames.add("Redstone");
                                break;
                            default:
                                materialNames.add("Glowstone");
                                materialNames.add("Glowstone");
                        }
                    }
                    furColor = new ItemStack(ModItems.furSheet,1,r.nextInt(15));
                }
                return;
            }
            int i = 0;
            while(nbt.hasKey("component"+(i)))
            {
                componentNames.add(nbt.getString("component"+i));
                materialNames.add(nbt.getString("material"+i));
                ++i;
            }

            hasGems=nbt.getBoolean("hasGems");

            if(nbt.hasKey("furColor"))
            {
                furColor = new ItemStack(ModItems.furSheet,1, nbt.getInteger("furColor"));
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