package com.shadowking97.forgecraft.gui;


import com.sun.istack.internal.NotNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import scala.collection.immutable.List;

import java.util.ArrayList;

/**
 * Created by Shadow Bolt on 9/6/2017.
 */
public class GuiScrollableList<T> extends Gui {

    private Minecraft mc = Minecraft.getMinecraft();

    interface stringTransformer<T>{
        public String transform(T object);
    }
    interface HoverDelegate<T>{
        public void onHover(T hovered);
    }
    interface SelectedDelegate<T>{
        public void onSelected(T selected);
    }

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
                mc.getTextureManager().bindTexture(textures);
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

    public class ScrollbarNub extends GuiImageButton
    {


        public ScrollbarNub(int buttonId, int x, int y, int unhighlightX, int unhighlightY, int highlightX, int highlightY) {
            super(buttonId, x, y, unhighlightX, unhighlightY, highlightX, highlightY, 7, 3);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, int scrollbarOffset) {
            yPosition+=scrollbarOffset;
            super.drawButton(mc,mouseX,mouseY);
            yPosition-=scrollbarOffset;
        }
    }

    private int xPosition, yPosition, width, height;
    private stringTransformer<T> transformer;
    private HoverDelegate hoverDelegate;
    private SelectedDelegate<T> selectedDelegate;
    private ArrayList<T> elements;
    private ResourceLocation textures;

    private int listOffset = 0;
    private int maxOffset = 0;

    GuiImageButton scrollUp, scrollDown;
    ScrollbarNub scrollbarNub;

    public boolean visible = true;

    public GuiScrollableList(int xPos, int yPos, int width, int height, @NotNull stringTransformer<T> transformer, HoverDelegate hoverDelegate, SelectedDelegate<T> selectedDelegate, ResourceLocation textures)
    {
        this.xPosition=xPos;
        this.yPosition=yPos;
        this.width=width;
        this.height=height;
        this.transformer=transformer;
        this.hoverDelegate=hoverDelegate;
        this.selectedDelegate=selectedDelegate;
        this.textures=textures;

        int i = 0;
        this.scrollUp = new GuiImageButton(i++,xPos+width-25,yPos,52,9,68,9, 9,7);
        this.scrollDown = new GuiImageButton(i++,xPos+width-25,yPos+this.height-7,52,16,68,16, 9,7);
        this.scrollbarNub = new ScrollbarNub(i++, scrollUp.xPosition+1,scrollUp.yPosition+7,36,7,84,7);
    }

    public void setList(ArrayList<T> elements)
    {
        this.elements=elements;

        maxOffset=(elements.size()*16) - height;
        if(maxOffset<0)maxOffset=0;
        if(listOffset>maxOffset)listOffset=maxOffset;
    }

    private boolean shouldDrawScrollbar()
    {
        return maxOffset>0;
    }

    public ArrayList<T> getList()
    {
        return elements;
    }

    private int sciMinX = -1, sciMinY = -1, sciMaxX = -1, sciMaxY = -1;

    private void doScissor()
    {
        if(sciMinX==-1) {
            Minecraft mc = Minecraft.getMinecraft();
            int scaleFactor = 1;
            int k = mc.gameSettings.guiScale;

            if (k == 0) k = 1000;

            while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240)
                ++scaleFactor;

            sciMinX = xPosition*scaleFactor;
            sciMinY = mc.displayHeight - (yPosition+height)*scaleFactor;
            sciMaxX = width*scaleFactor;
            sciMaxY = height*scaleFactor;
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(sciMinX,sciMinY, sciMaxX,sciMaxY);
    }

    private void clearScissor()
    {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0,0,mc.displayWidth,mc.displayHeight);
    }

    public boolean inBoundries(int x, int y)
    {
        if(x<xPosition||x>xPosition+width)return false;
        if(y<yPosition||y>yPosition+height)return false;
        return true;
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedMouseButton)
    {
        if(visible)
        {

        }
    }

    public void mouseReleased()
    {
        if(visible)
        {

        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(visible)
        {

        }
    }

    private int scrollbarOffset(int minY, int maxY)
    {
        return (int)Math.floor((maxY-minY)*(((float)listOffset)/maxOffset));
    }

    public void draw(int mouseX, int mouseY)
    {
        if(visible) {
            doScissor();
            if(elements!=null)
            {
                int i = 0;
                for(T element: elements)
                {
                    if((i<<4)-listOffset+yPosition<-15)continue;

                    if((i<<4)-listOffset>height);

                    int y = (i<<4)-listOffset+yPosition;

                    if(mouseY>=y&&mouseY<y+16)
                    {
                        if(mouseX>=xPosition+5&&mouseX<xPosition+width-16)
                        {
                            this.drawGradientRect(xPosition, y, xPosition+this.width-20, y+16, -1072689136, -804253680);

                            this.drawHorizontalLine(xPosition,xPosition+this.width-20,y,0x77ffffff);
                            this.drawHorizontalLine(xPosition,xPosition+this.width-20,y+16,0x77ffffff);
                            this.drawVerticalLine(xPosition,y,y+16,0x77ffffff);

                            if(hoverDelegate!=null)hoverDelegate.onHover(element);
                        }
                    }

                    String s = transformer.transform(element);
                    drawString(mc.fontRendererObj,s,xPosition+3,y+5,14737632);

                    ++i;
                }
            }


            if(shouldDrawScrollbar()){
                scrollUp.drawButton(this.mc,mouseX,mouseY);
                scrollDown.drawButton(this.mc,mouseX,mouseY);

                int x = scrollUp.xPosition+3;
                int y = scrollUp.yPosition+7;
                drawTexturedImage(x,y,63,3,3,8);
                int maxY = scrollDown.yPosition-8;
                while(y<maxY){
                    y+=8;
                    if(y>maxY)y=maxY;
                    drawTexturedImage(x,y,63,3,3,8);
                }

                scrollbarNub.drawButton(this.mc,mouseX,mouseY,scrollbarOffset(scrollUp.xPosition+3,maxY+5));
            }

            clearScissor();
        }
    }

}
