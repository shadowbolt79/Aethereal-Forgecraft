package com.shadowking97.forgecraft.gui;

import com.shadowking97.forgecraft.item.components.ComponentDefinition;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * Created by Shadow Bolt on 9/3/2017.
 */
public class CraftingGUIContainer extends GuiScreen {

    private TileEntity te;
    private ItemStack componentItem;
    private ItemMaterial.MaterialType materialType;
    private EntityPlayer thePlayer;
    private GuiScrollableList<ComponentDefinition> guiScrollableList;

    private static final ResourceLocation CRAFTING_GUI_TEXTURES = new ResourceLocation("skforgecraft","textures/gui/craftinggui.png");


    //IF componentItem == null
    //Otherwise the same but with tabs and scrollbar removed

    /***
     * TAB * TAB * TAB * TAB * TAB * TAB * TAB *
     *       S
     * Item  C          Rendered 3d
     * Item  R              Item
     * Item  O
     * Item  L  Materail: <Material>
     * Item  L  Subcomponent: <Option><Material>
     * Item  B  Subcomponent: <Option><Material>
     * Item  A
     * Item  R         Requires: Stuff
     */

    public int tabOffset = 0;

    private GuiTab selectedTab;

    public boolean tabVisible(int xPosition)
    {
        int xPos = xPosition-(tabOffset*20);

        if(xPos<20)
            return false;
        if(xPos+10>(this.width>>1)-41)
            return false;

        return true;
    }

    int maxTabOffset = -1;
    int maxScrollOffset = -1;

    int scrollbarMin, scrollbarMax;

    public int maxTabOffset()
    {
        if(maxTabOffset !=-1)return maxTabOffset;

        int i = (this.width>>1)-51;

        maxTabOffset = 0;
        while((170-(maxTabOffset *20))>i&& maxTabOffset <8)
            maxTabOffset++;

        return maxTabOffset;
    }

    public int maxScrollOffset()
    {
        if(maxScrollOffset!=-1)return maxScrollOffset;

        //do stuff

        return maxScrollOffset;
    }

    /**
     * Draws a textured rectangle at the current z-value.
     */
    public void drawTexturedImage(int x, int y, int textureX, int textureY, int width, int height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) /128), (double)((float)(textureY + height) /64)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) /128), (double)((float)(textureY + height) /64)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) /128), (double)((float)(textureY + 0) /64)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) /128), (double)((float)(textureY + 0) /64)).endVertex();
        tessellator.draw();
    }

    public class GuiTab extends GuiButton
    {
        int uX, uY, hX, hY;

        public GuiTab(int buttonId, int x, int y, String buttonText, int unhighlightX, int unhighlightY, int highlightX, int highlightY) {
            super(buttonId, x, y, 16,16, buttonText);
            uX=unhighlightX<<4;
            uY=unhighlightY<<4;
            hX=highlightX<<4;
            hY=highlightY<<4;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (tabVisible(xPosition))
            {
                mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= (this.xPosition - (tabOffset * 20)) && mouseY >= this.yPosition && mouseX < (this.xPosition - (tabOffset * 20)) + this.width && mouseY < this.yPosition + this.height;

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                if(selectedTab==this)
                {
                    drawTexturedImage(this.xPosition - (tabOffset * 20), this.yPosition+16, 96, 0, this.width, this.height);
                }
                else{
                    drawTexturedImage(this.xPosition - (tabOffset * 20)-5, this.yPosition+16, 91, 16, this.width+10, this.height);
                }

                if(hovered||selectedTab==this) {
                    drawTexturedImage(this.xPosition - (tabOffset * 20), this.yPosition, hX, hY, this.width, this.height);
                }
                else
                    drawTexturedImage(this.xPosition-(tabOffset*20), this.yPosition, uX, uY, this.width, this.height);
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return tabVisible(this.xPosition)&&super.mousePressed(mc, mouseX + (tabOffset * 20), mouseY);
        }
    }

    public CraftingGUIContainer(TileEntity te, ItemMaterial.MaterialType materialType, EntityPlayer thePlayer){
        this.te=te;
        //this.componentItem=componentItem;
        this.materialType=materialType;
        this.thePlayer = thePlayer;
    }

    public class GuiImageButton extends GuiButton
    {
        int uX, uY, hX, hY;

        public GuiImageButton(int buttonId, int x, int y, int unhighlightX, int unhighlightY, int highlightX, int highlightY) {
            super(buttonId, x, y, 16,16, "");
            uX=unhighlightX<<4;
            uY=unhighlightY<<4;
            hX=highlightX<<4;
            hY=highlightY<<4;
        }

        public GuiImageButton(int buttonId, int x, int y, int unhighlightX, int unhighlightY, int highlightX, int highlightY, int widthIn, int heightIn) {
            super(buttonId, x, y, widthIn,heightIn, "");
            uX=unhighlightX;
            uY=unhighlightY;
            hX=highlightX;
            hY=highlightY;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                if(hovered) {
                    drawTexturedImage(this.xPosition, this.yPosition, hX, hY, this.width, this.height);
                }
                else
                    drawTexturedImage(this.xPosition, this.yPosition, uX, uY, this.width, this.height);
            }
        }
    }


    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawGradientRect(0, (this.height>>2)*3, this.width, (this.height)-9, -1072689136, -804253680);
        this.drawGradientRect(2, 0, (this.width>>1)-9, this.height, -1072689136, -804253680);
        this.drawHorizontalLine(0,this.width,((this.height>>2)*3)+3,0x77ffffff);
        this.drawHorizontalLine(0,this.width,((this.height))-12,0x77ffffff);
        this.drawVerticalLine(5,0,this.height,0x77ffffff);
        this.drawVerticalLine((this.width>>1)-12,0,this.height,0x77ffffff);

        if(selectedTab!=null)
            this.drawString(this.fontRendererObj,selectedTab.displayString,8,5,14737632);

        mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedImage(14,31,80,16,16,16);
        int xPos = (width>>1)-36;
        this.drawTexturedImage(xPos, 31, 112, 16, 16, 16);
        int max = ((8- maxTabOffset())*20)+26;
        while((xPos>max)) {
            xPos-=16;
            if(xPos<max)xPos=max;
            this.drawTexturedImage(xPos, 31, 96, 16, 16, 16);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    GuiTab weapons,tools,helmet,chest,legs,boots,jewelry,misc;
    GuiImageButton previous, next, scrollUp, scrollDown;

    @Override
    public void initGui() {
        int i = 0;
        int k = 0;

        guiScrollableList = new GuiScrollableList<>(25,53,(width>>1)-50,((height>>2)*3)-50,(ComponentDefinition o)-> o.getName());

        maxTabOffset =-1;
        if(selectedTab!=null)
        {
            if(selectedTab==weapons)k=1;
            if(selectedTab==tools)k=2;
            if(selectedTab==helmet)k=3;
            if(selectedTab==chest)k=4;
            if(selectedTab==legs)k=5;
            if(selectedTab==boots)k=6;
            if(selectedTab==jewelry)k=7;
            if(selectedTab==misc)k=8;
        }

        this.buttonList.add(this.weapons = new GuiTab(i++, 30, 15, "Weapons",0,2,0,3));

        if(tabOffset> maxTabOffset())tabOffset= maxTabOffset();
        this.buttonList.add(this.tools = new GuiTab(i++, 50, 15, "Tools",1,2,1,3));
        this.buttonList.add(this.helmet = new GuiTab(i++, 70, 15, "Helms",2,2,2,3));
        this.buttonList.add(this.chest = new GuiTab(i++, 90, 15, "Chest",3,2,3,3));
        this.buttonList.add(this.legs = new GuiTab(i++, 110, 15, "Leggings",4,2,4,3));
        this.buttonList.add(this.boots = new GuiTab(i++, 130, 15, "Boots",5,2,5,3));
        this.buttonList.add(this.jewelry = new GuiTab(i++, 150, 15, "Jewelry",6,2,6,3));
        this.buttonList.add(this.misc = new GuiTab(i++, 170, 15, "Miscellaneous",7,2,7,3));
        this.buttonList.add(this.previous = new GuiImageButton(i++,8,15,0,0,0,1));
        this.buttonList.add(this.next = new GuiImageButton(i++,(this.width>>1)-30,15,1,0,1,1));

        if(selectedTab==null)
            selectedTab = this.weapons;
        else if(k!=0)
        {
            switch(k)
            {
                case 1:
                    selectedTab=this.weapons;
                    break;
                case 2:
                    selectedTab=this.tools;
                    break;
                case 3:
                    selectedTab=this.helmet;
                    break;
                case 4:
                    selectedTab=this.chest;
                    break;
                case 5:
                    selectedTab = this.legs;
                    break;
                case 6:
                    selectedTab=this.boots;
                    break;
                case 7:
                    selectedTab=this.jewelry;
                    break;
                case 8:
                    selectedTab=this.misc;
                    break;
            }
        }

        this.buttonList.add(this.scrollUp = new GuiImageButton(i++,(this.width>>1)-25,53,52,9,68,9, 9,7));
        this.buttonList.add(this.scrollDown = new GuiImageButton(i++,(this.width>>1)-25,((this.height>>2)*3)-10,52,16,68,16, 9,7));

        scrollbarMin=60;
        scrollbarMax=((this.height>>2)*3)-15;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button instanceof GuiTab)
        {
            selectedTab=(GuiTab)button;
        }
        else
        {
            if(button==previous)
            {
                if(tabOffset>0)tabOffset--;
            }
            else if(button==next)
            {
                if(tabOffset< maxTabOffset())tabOffset++;
            }
        }
    }
}