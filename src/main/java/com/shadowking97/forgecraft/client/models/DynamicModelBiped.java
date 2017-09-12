package com.shadowking97.forgecraft.client.models;

import com.google.common.collect.Lists;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

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
    public void addChild(ModelRenderer bodyPart, ComponentModelRenderer child) {
        if(bodyPart==null||child==null)return;

        child = child.copyOf(this);

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
    /**
     * Adds a child to the model. Not chached to survive
     */
    public void addSubcomponent(ModelRenderer bodyPart, ComponentModelRenderer child) {
        if(bodyPart==null||child==null)return;

        child=child.copyOf(this);

        if(bodyPart==this.bipedBody){
            bipedBody.addChild(child);
        }
        else if(bodyPart==this.bipedHead){
            bipedHead.addChild(child);
        }
        else if(bodyPart==this.bipedLeftArm){
            bipedLeftArm.addChild(child);
        }
        else if(bodyPart==this.bipedRightArm){
            bipedRightArm.addChild(child);
        }
        else if(bodyPart==this.bipedLeftLeg){
            bipedLeftLeg.addChild(child);
        }
        else if(bodyPart==this.bipedRightLeg){
            bipedRightLeg.addChild(child);
        }
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(entityIn instanceof EntityArmorStand)
        {
            netHeadYaw=0;
        }

        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();

        if (this.isChild)
        {
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            renderChildren(this.bipedHead,scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            renderChildren(this.bipedBody,scale);
            renderChildren(this.bipedRightArm,scale);
            renderChildren(this.bipedLeftArm,scale);
            renderChildren(this.bipedRightLeg,scale);
            renderChildren(this.bipedLeftLeg,scale);
            renderChildren(this.bipedBody,scale);
        }
        else
        {
            if (entityIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            renderChildren(this.bipedHead,scale);
            renderChildren(this.bipedBody,scale);
            renderChildren(this.bipedRightArm,scale);
            renderChildren(this.bipedLeftArm,scale);
            renderChildren(this.bipedRightLeg,scale);
            renderChildren(this.bipedLeftLeg,scale);
            renderChildren(this.bipedBody,scale);
        }

        GlStateManager.popMatrix();
    }

    /***
     * Renders the ModelRenderer's children without rendering the model itself.
     *
     * Yes, I'm cheating.
     * @param renderer
     * @param scale
     */
    public void renderChildren(ModelRenderer renderer, float scale) {
        if(renderer.childModels==null||renderer.childModels.size()==0)
            return;
        if (!renderer.isHidden) {
            if (renderer.showModel) {
                GlStateManager.translate(renderer.offsetX, renderer.offsetY, renderer.offsetZ);

                if (renderer.rotateAngleX == 0.0F && renderer.rotateAngleY == 0.0F && renderer.rotateAngleZ == 0.0F) {
                    if (renderer.rotationPointX == 0.0F && renderer.rotationPointY == 0.0F && renderer.rotationPointZ == 0.0F) {

                        for (int k = 0; k < renderer.childModels.size(); ++k) {
                            (renderer.childModels.get(k)).render(scale);
                        }

                    } else {
                        GlStateManager.translate(renderer.rotationPointX * scale, renderer.rotationPointY * scale, renderer.rotationPointZ * scale);

                        for (int j = 0; j < renderer.childModels.size(); ++j) {
                            (renderer.childModels.get(j)).render(scale);

                        }

                        GlStateManager.translate(-renderer.rotationPointX * scale, -renderer.rotationPointY * scale, -renderer.rotationPointZ * scale);
                    }
                } else {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(renderer.rotationPointX * scale, renderer.rotationPointY * scale, renderer.rotationPointZ * scale);

                    if (renderer.rotateAngleZ != 0.0F) {
                        GlStateManager.rotate(renderer.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (renderer.rotateAngleY != 0.0F) {
                        GlStateManager.rotate(renderer.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (renderer.rotateAngleX != 0.0F) {
                        GlStateManager.rotate(renderer.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    for (int i = 0; i < renderer.childModels.size(); ++i) {
                        (renderer.childModels.get(i)).render(scale);
                    }

                    GlStateManager.popMatrix();
                }

                GlStateManager.translate(-renderer.offsetX, -renderer.offsetY, -renderer.offsetZ);
            }
        }
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
