package com.shadowking97.forgecraft.gui;


import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import scala.collection.immutable.List;

/**
 * Created by Shadow Bolt on 9/6/2017.
 */
public class GuiScrollableList<T> {
    interface stringTransformer<T>{
        public String transform(T object);
    }

    private int xPosition, yPosition, width, height;
    private stringTransformer<T> transformer;
    private List<T> elements;

    public GuiScrollableList(int xPos, int yPos, int width, int height,stringTransformer<T> transformer)
    {

    }

    public T getSelected()
    {
        return null;
    }

    public void draw()
    {
        GL11.glScissor(xPosition,yPosition,width,height);

        //do stuff

        GL11.glScissor(0,0, Minecraft.getMinecraft().displayWidth,Minecraft.getMinecraft().displayHeight);
    }

}
