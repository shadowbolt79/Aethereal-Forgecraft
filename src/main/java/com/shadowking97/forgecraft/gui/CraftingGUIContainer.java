package com.shadowking97.forgecraft.gui;

import com.shadowking97.forgecraft.client.MapHolder;
import com.shadowking97.forgecraft.client.models.ComponentModelRenderer;
import com.shadowking97.forgecraft.item.components.ComponentDefinition;
import com.shadowking97.forgecraft.item.components.ComponentGenerator;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import com.shadowking97.forgecraft.util.CommonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Shadow Bolt on 9/3/2017.
 */
public class CraftingGUIContainer extends GuiScreen {

    private TileEntity te;
    private ItemStack componentItem;
    private ItemMaterial.MaterialType materialType;
    private EntityPlayer thePlayer;
    private GuiScrollableList<ComponentDefinition> guiScrollableList;

    private ArrayList<ComponentDefinition> temp;
    private ComponentModelRenderer tempModel = ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm").getMaterialModel(MaterialStore.INSTANCE.getMaterialByName("Iron"));

    private static final ResourceLocation CRAFTING_GUI_TEXTURES = new ResourceLocation("skforgecraft","textures/gui/crafting_gui.png");


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

    public int maxTabOffset()
    {
        if(maxTabOffset !=-1)return maxTabOffset;

        int i = (this.width>>1)-51;

        maxTabOffset = 0;
        while((170-(maxTabOffset *20))>i&& maxTabOffset <8)
            maxTabOffset++;

        return maxTabOffset;
    }

    /**
     * Draws a textured rectangle at the current z-value.
     */
    public void drawTexturedImage(int x, int y, int textureX, int textureY, int width, int height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) /128), (double)((float)(textureY + height) /64)).endVertex();
        bufferBuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) /128), (double)((float)(textureY + height) /64)).endVertex();
        bufferBuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) /128), (double)((float)(textureY + 0) /64)).endVertex();
        bufferBuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) /128), (double)((float)(textureY + 0) /64)).endVertex();
        tessellator.draw();
    }

    //Variables for tracking object rotation
    Object selectedObject;
    int mouseX, mouseY;

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getDWheel();

        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;;

        if(wheel!=0)scroll(-wheel,x,y);
    }

    public void scroll(int wheel, int x, int y)
    {
        if (guiScrollableList.inBoundries(x, y)) {
            selectedObject = guiScrollableList;

            if (guiScrollableList.visible) {
                guiScrollableList.scroll(wheel);
                return;
            }
        }
        if (x < (width >> 1) && y < 36){
            if(wheel>0&&tabOffset<maxTabOffset){
                tabOffset+=wheel/100;
                tabOffset=Math.min(tabOffset,maxTabOffset);
            }
            else if(wheel<0&&tabOffset>0){
                tabOffset+=wheel/100;
                tabOffset=Math.max(tabOffset,0);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        if(mouseButton==0) {
            if (guiScrollableList.inBoundries(mouseX, mouseY)) {
                selectedObject = guiScrollableList;

                if (guiScrollableList.visible)
                    guiScrollableList.mouseClicked(mouseX, mouseY, mouseButton);
            } else if (mouseX > (width >> 1) && mouseY < (height >> 2) * 3)
                selectedObject = tempModel;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        if(guiScrollableList.visible&&guiScrollableList==selectedObject&&mouseButton==0)
            guiScrollableList.mouseClickMove(mouseX,mouseY,mouseButton,timeSinceLastClick);

        else if(tempModel==selectedObject&&tempModel!=null)
        {
            modelRotX+=(this.mouseX-mouseX)<<1;

            modelRotY-=(this.mouseY-mouseY);
            if(modelRotY>90)modelRotY=90;
            else if(modelRotY<-90)modelRotY=-90;
        }

        this.mouseX=mouseX;
        this.mouseY=mouseY;

        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if(guiScrollableList.visible)
            guiScrollableList.mouseReleased();

        selectedObject = null;

        super.mouseReleased(mouseX, mouseY, state);
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
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (tabVisible(x))
            {
                mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= (this.x - (tabOffset * 20)) && mouseY >= this.y && mouseX < (this.x - (tabOffset * 20)) + this.width && mouseY < this.y + this.height;

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                if(selectedTab==this)
                {
                    drawTexturedImage(this.x - (tabOffset * 20), this.y+16, 96, 0, this.width, this.height);
                }
                else{
                    drawTexturedImage(this.x - (tabOffset * 20)-5, this.y+16, 91, 16, this.width+10, this.height);
                }

                if(hovered||selectedTab==this) {
                    drawTexturedImage(this.x - (tabOffset * 20), this.y, hX, hY, this.width, this.height);
                }
                else
                    drawTexturedImage(this.x-(tabOffset*20), this.y, uX, uY, this.width, this.height);
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return tabVisible(this.x)&&super.mousePressed(mc, mouseX + (tabOffset * 20), mouseY);
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
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                if(hovered) {
                    drawTexturedImage(this.x, this.y, hX, hY, this.width, this.height);
                }
                else
                    drawTexturedImage(this.x, this.y, uX, uY, this.width, this.height);
            }
        }
    }


    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    float modelRotX =25 , modelRotY =16;

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static void drawModelOnScreen(int posX, int posY, float modelRotX, float modelRotY, int scale, int mouseY, ComponentModelRenderer renderer)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180, 0, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableBlend();

        GlStateManager.rotate(modelRotX, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(modelRotY, 1.0F, 0.0F, 0.0F);

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        //rendermanager.setRenderShadow(false);

        GL11.glCallList(renderer.getDisplayList());

        //rendermanager.setRenderShadow(true);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
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
            this.drawString(this.fontRenderer,selectedTab.displayString,8,5,14737632);

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

        guiScrollableList.draw(mouseX,mouseY,partialTicks);

        mc.getTextureManager().bindTexture(MapHolder.LOCATION_COMPONENTS_TEXTURE);
        drawModelOnScreen((this.width>>2)*3,(this.height>>1)+5, modelRotX, modelRotY,8,mouseY,tempModel);
        mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    GuiTab weapons,tools,helmet,chest,legs,boots,jewelry,misc;
    GuiImageButton previous, next;

    @Override
    public void initGui() {
        int i = 0;
        int k = 0;

        guiScrollableList = new GuiScrollableList<>(25, 48, (width >> 1) - 28, ((height >> 2) * 3) - 51,
                (ComponentDefinition o) -> o.getName(), //String transformer
                (GuiScrollableList.HoverDelegate<ComponentDefinition>) hovered -> tempModel = hovered.getMaterialModel(MaterialStore.INSTANCE.getMaterialByName("Iron")),
                selected -> CommonUtil.info("Clicked Element: "+selected.getName()),
                CRAFTING_GUI_TEXTURES);

        if(temp==null){
            temp=new ArrayList<>();
            if(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm")==null)
                System.out.println("AAAAAAAA");
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
            temp.add(ComponentGenerator.INSTANCE.getComponent("Nordic Carved Helm"));
        }

        guiScrollableList.setList(temp);

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