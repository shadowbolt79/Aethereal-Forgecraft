package com.shadowking97.forgecraft.client.models;

import java.util.List;
import com.shadowking97.forgecraft.client.MapHolder;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


/**
 * Created by Shadow Bolt on 8/9/2017.
 */
public class ComponentModelRenderer extends ModelRenderer {

    private ItemMaterial material;
    private ComponentModel model;
    private boolean compiled=false;
    private int displayList;

    private ItemStack renderStack = null;


    public ComponentModelRenderer(ModelBase model, String boxNameIn)
    {
        super(model,boxNameIn);
    }

    public ComponentModelRenderer(ModelBase model)
    {
        this(model,null);
    }

    public ComponentModelRenderer setMaterial(ItemMaterial material)
    {
        this.material=material;
        return this;
    }

    public ComponentModelRenderer setModel(ComponentModel model)
    {
        this.model=model;
        return this;
    }

    public ComponentModelRenderer setRenderStack(ItemStack stack)
    {
        renderStack=stack;
        return this;
    }

    /**
     * Returns a new instance of this renderer with the display list precompiled, for saving memory
     * @param base new model base to attach to
     * @return
     */
    public ComponentModelRenderer copyOf(ModelBase base)
    {
        ComponentModelRenderer renderer = new ComponentModelRenderer(base, this.boxName).setModel(this.model).setMaterial(this.material);
        renderer.setDisplayList(this.getDisplayList());

        return renderer;
    }

    public ComponentModelRenderer scalableCopyOf(ModelBase base)
    {
        return new ComponentModelRenderer(base, this.boxName).setModel(this.model).setMaterial(this.material);
    }


    public void finalizeTextures()
    {
        this.setTextureSize(MapHolder.textureMapComponents.getTextureWidth(),MapHolder.textureMapComponents.getTextureHeight());
    }

    @SideOnly(Side.CLIENT)
    public void render(float scale)
    {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!compiled) {
                    this.compileDisplayList(scale);
                }

                //GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                super.render(scale);
            }
        }
    }

    /**
     * Compiles a GL display list for this model
     */
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float scale)
    {
        displayList = GLAllocation.generateDisplayLists(1);
        ObfuscationReflectionHelper.setPrivateValue(ModelRenderer.class,this,displayList,"displayList");
        GlStateManager.glNewList(displayList, 4864);
        VertexBuffer vertexBuffer = Tessellator.getInstance().getBuffer();
        vertexBuffer.begin(7,DefaultVertexFormats.ITEM);

        //Minecraft.getMinecraft().getTextureManager().bindTexture(MapHolder.LOCATION_COMPONENTS_TEXTURE);
        //Minecraft.getMinecraft().getTextureManager().getTexture(MapHolder.LOCATION_COMPONENTS_TEXTURE).setBlurMipmap(false,false);

        GlStateManager.pushMatrix();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //GlStateManager.enableNormalize();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);

        if(material.getType()== ItemMaterial.MaterialType.Crystal)
            GlStateManager.disableLighting();

        renderQuads(vertexBuffer, model.bakeOpaque(scale));
        Tessellator.getInstance().draw();

        vertexBuffer.begin(7,DefaultVertexFormats.ITEM);
        //GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);


        GlStateManager.pushMatrix();

        renderQuads(vertexBuffer, model.bakeTransparent(scale));
        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();

        //GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.depthMask(true);


        if(material.getType()== ItemMaterial.MaterialType.Crystal)
            GlStateManager.enableLighting();

        GlStateManager.color(1,1,1,1);

        //GlStateManager.disableNormalize();
        GlStateManager.popMatrix();

        GlStateManager.glEndList();
        ObfuscationReflectionHelper.setPrivateValue(ModelRenderer.class,this,true,"compiled");
        compiled=true;
    }

    @SideOnly(Side.CLIENT)
    private int getDisplayList()
    {
        if(!compiled)
            compileDisplayList(1);

        return displayList;
    }

    @SideOnly(Side.CLIENT)
    private void setDisplayList(int displayList)
    {
        this.displayList = displayList;
        compiled=true;
        ObfuscationReflectionHelper.setPrivateValue(ModelRenderer.class,this,displayList,"displayList");
        ObfuscationReflectionHelper.setPrivateValue(ModelRenderer.class,this,true,"compiled");
    }


    private void renderQuads(VertexBuffer renderer, List<BakedQuad> quads)
    {
        boolean flag = material != null;
        int i = 0;

        for (int j = quads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = quads.get(i);
            int k = -1;

            if (flag && bakedquad.hasTintIndex())
            {
                k = this.material.getColorFromItemstack(renderStack, bakedquad.getTintIndex());

                if (EntityRenderer.anaglyphEnable)
                {
                    k = TextureUtil.anaglyphColor(k);
                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }
}
