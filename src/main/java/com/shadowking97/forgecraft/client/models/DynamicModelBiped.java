package com.shadowking97.forgecraft.client.models;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds the ability to combine model bipeds into a single biped
 * For use with armors to make complex components
 * Created by Shadow Bolt on 8/9/2017.
 */
public class DynamicModelBiped extends ModelBiped {
    private List<ModelRenderer> componentBody;
    private List<ModelRenderer> componentHead;
    private List<ModelRenderer> componentHeadwear;
    private List<ModelRenderer> componentLeftArm;
    private List<ModelRenderer> componentRightArm;
    private List<ModelRenderer> componentLeftLeg;
    private List<ModelRenderer> componentRightLeg;

    public DynamicModelBiped()
    {
        this(0.0F);
    }

    public DynamicModelBiped(float modelSize)
    {
        this(modelSize, 0.0F, 64, 32);
    }

    public DynamicModelBiped(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn)
    {
        super(modelSize, p_i1149_2_, textureWidthIn, textureHeightIn);
    }

    /**
     * Combines a models children with this one's for easy rendering.
     * @param other
     */
    public void combineWith(ModelBiped other)
    {
        addChildren(this.bipedBody,other.bipedBody.childModels);
        addChildren(this.bipedHead,other.bipedHead.childModels);
        addChildren(this.bipedHeadwear,other.bipedHeadwear.childModels);
        addChildren(this.bipedLeftArm,other.bipedLeftArm.childModels);
        addChildren(this.bipedRightArm,other.bipedRightArm.childModels);
        addChildren(this.bipedLeftLeg,other.bipedLeftLeg.childModels);
        addChildren(this.bipedRightLeg,other.bipedRightLeg.childModels);
    }

    private static void addChildren(ModelRenderer bodyPart, List<ModelRenderer> children) {
        if(children==null||children.isEmpty())return;

        if (bodyPart.childModels == null)
        {
            bodyPart.childModels = Lists.<ModelRenderer>newArrayList();
        }

        bodyPart.childModels.addAll(children);
    }

    /**
     * Adds a child to the internal code. Is cached to survive resets.
     *
     * @param bodyPart one of the body parts within this model
     * @param child the child to add
     */
    public void addChild(ModelRenderer bodyPart, ModelRenderer child) {
        if(bodyPart==null||child==null)return;

        if(bodyPart==this.bipedBody){
            bipedBody.addChild(child);
            if(componentBody==null)componentBody=new ArrayList<>();
            componentBody.add(child);
        }
        else if(bodyPart==this.bipedHead){
            bipedHead.addChild(child);
            if(componentHead==null)componentHead=new ArrayList<>();
            componentHead.add(child);
        }
        else if(bodyPart==this.bipedHeadwear){
            bipedHeadwear.addChild(child);
            if(componentHeadwear==null)componentHeadwear=new ArrayList<>();
            componentHeadwear.add(child);
        }
        else if(bodyPart==this.bipedLeftArm){
            bipedLeftArm.addChild(child);
            if(componentLeftArm==null)componentLeftArm=new ArrayList<>();
            componentLeftArm.add(child);
        }
        else if(bodyPart==this.bipedRightArm){
            bipedRightArm.addChild(child);
            if(componentRightArm==null)componentRightArm=new ArrayList<>();
            componentRightArm.add(child);
        }
        else if(bodyPart==this.bipedLeftLeg){
            bipedLeftLeg.addChild(child);
            if(componentLeftLeg==null)componentLeftLeg=new ArrayList<>();
            componentLeftLeg.add(child);
        }
        else if(bodyPart==this.bipedRightLeg){
            bipedRightLeg.addChild(child);
            if(componentRightLeg==null)componentRightLeg=new ArrayList<>();
            componentRightLeg.add(child);
        }
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();

        if (this.isChild)
        {
            float f = 2.0F;
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            //this.bipedHead.render(scale);

            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            //this.bipedBody.render(scale);
            //this.bipedRightArm.render(scale);
            //this.bipedLeftArm.render(scale);
            //this.bipedRightLeg.render(scale);
            //this.bipedLeftLeg.render(scale);
            //this.bipedHeadwear.render(scale);
        }
        else
        {
            if (entityIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            //this.bipedHead.render(scale);
            //this.bipedBody.render(scale);
            //this.bipedRightArm.render(scale);
            //this.bipedLeftArm.render(scale);
            //this.bipedRightLeg.render(scale);
            //this.bipedLeftLeg.render(scale);
            //this.bipedHeadwear.render(scale);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Used to reset the model to just the current component
     *
     * called when the sub components change
     */
    public void resetModel() {
        bipedBody.childModels.clear();
        addChildren(bipedBody,componentBody);
        bipedHead.childModels.clear();
        addChildren(bipedHead,componentHead);
        bipedHeadwear.childModels.clear();
        addChildren(bipedHeadwear,componentHeadwear);
        bipedLeftArm.childModels.clear();
        addChildren(bipedLeftArm,componentLeftArm);
        bipedRightArm.childModels.clear();
        addChildren(bipedRightArm,componentRightArm);
        bipedLeftLeg.childModels.clear();
        addChildren(bipedLeftLeg,componentLeftLeg);
        bipedRightLeg.childModels.clear();
        addChildren(bipedRightLeg,componentRightLeg);
    }
}
