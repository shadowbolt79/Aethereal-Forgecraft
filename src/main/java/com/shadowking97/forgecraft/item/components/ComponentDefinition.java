package com.shadowking97.forgecraft.item.components;

import com.shadowking97.forgecraft.capabilities.ArmorCapabilityProvider;
import com.shadowking97.forgecraft.client.models.ComponentModel;
import com.shadowking97.forgecraft.client.models.ComponentModelRenderer;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

/**
 * Stores information about components loaded into the system
 * Created by Shadow Bolt on 7/22/2017.
 */
public class ComponentDefinition {
    private final ComponentGenerator.ComponentSlot slot;
    private final ItemMaterial.MaterialType type;

    private final int materialAmount;
    private final HashMap<String,ComponentDefinition> availableSubComponents;
    private final String componentName;

    private final boolean subComponent;

    private ArmorCapabilityProvider.ModelPart part;

    @SideOnly(Side.CLIENT)
    private ResourceLocation baseTexture;
    @SideOnly(Side.CLIENT)
    private ResourceLocation highlightTexture;
    @SideOnly(Side.CLIENT)
    private ResourceLocation shadowTexture;

    @SideOnly(Side.CLIENT)
    private ComponentModel model;
    @SideOnly(Side.CLIENT)
    private HashMap<String,ComponentModelRenderer> itemMaterialModels;

    private static final ModelBase dummyBase = new ModelBase() {
        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {}
    };

    private final int renderLayer;

    public String getName(){return componentName;}

    public ComponentDefinition(String componentName, ComponentGenerator.ComponentSlot slot, ItemMaterial.MaterialType type, int materialAmount, HashMap<String,ComponentDefinition> availableSubComponents, int renderLayer, boolean isSubComponent)
    {
        this.componentName = componentName;
        this.slot = slot;
        this.type = type;
        this.materialAmount = materialAmount;
        this.availableSubComponents = availableSubComponents;
        this.subComponent=isSubComponent;
        this.renderLayer=renderLayer;

        switch (slot)
        {
            case HELMET_CAP:
            case HELMET_MASK:
                part = ArmorCapabilityProvider.ModelPart.BIPED_HEAD;
                if(availableSubComponents!=null)
                    for(ComponentDefinition subcomp: availableSubComponents.values())
                        subcomp.setPart(ArmorCapabilityProvider.ModelPart.BIPED_HEAD);
                break;
        }
    }

    private void setPart(ArmorCapabilityProvider.ModelPart part)
    {
        this.part = part;
        if(availableSubComponents!=null)
            for(ComponentDefinition subcomp: availableSubComponents.values())
                subcomp.setPart(part);
    }

    public ArmorCapabilityProvider.ModelPart getPart()
    {
        return part;
    }

    public ComponentDefinition getSubComponent(String name)
    {
        if(availableSubComponents!=null)
            return availableSubComponents.get(name);
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void addTextures(ResourceLocation baseTexture, ResourceLocation highlightTexture, ResourceLocation shadowTexture)
    {
        this.baseTexture=baseTexture;
        this.highlightTexture=highlightTexture;
        this.shadowTexture=shadowTexture;
    }

    @SideOnly(Side.CLIENT)
    public void setModel(ComponentModel model)
    {
        this.model=model;
        itemMaterialModels=new HashMap<>();
    }

    @SideOnly(Side.CLIENT)
    public ComponentModelRenderer getMaterialModel(ItemMaterial material)
    {
        if(material==null)return null;
        if(model==null)return null;

        if(itemMaterialModels.containsKey(material.getName()))return itemMaterialModels.get(material.getName());

        ComponentModelRenderer renderer = new ComponentModelRenderer(dummyBase).setModel(model).setMaterial(material);
        itemMaterialModels.put(material.getName(),renderer);

        return renderer;
    }

    @SideOnly(Side.CLIENT)
    public ComponentModel getModel()
    {
        return model;
    }

    public ComponentGenerator.ComponentSlot getSlot() {return slot;}
    public ItemMaterial.MaterialType getType() {return type;}
    public int getMaterialAmount() {return materialAmount;}
    public boolean isSubComponent(){return subComponent;}

    @SideOnly(Side.CLIENT)
    public ResourceLocation getBaseTexture(){return baseTexture;}
    @SideOnly(Side.CLIENT)
    public ResourceLocation getHighlightTexture(){return highlightTexture;}
    @SideOnly(Side.CLIENT)
    public ResourceLocation getShadowTexture(){return shadowTexture;}

    public int getRenderLayer(){return renderLayer;}

    public HashMap<String,ComponentDefinition> getAvailableSubComponents()
    {
        return availableSubComponents;
    }
    public boolean canAttach(ComponentDefinition otherComponent) { return (availableSubComponents!=null?availableSubComponents.containsKey(otherComponent.componentName):false); }

    @Override
    public int hashCode()
    {
        return componentName.hashCode();
    }
}