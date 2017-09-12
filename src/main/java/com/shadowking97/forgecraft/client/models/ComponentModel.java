package com.shadowking97.forgecraft.client.models;

import com.shadowking97.forgecraft.client.ModelUtil;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shadow Bolt on 8/14/2017.
 */
public class ComponentModel {
    //minimum offset to prevent z-fighting
    private static final float ANTIZ = 0.05f/16.0f;


    HashMap<String,TextureAtlasSprite> baseTextures,highlightTextures,shadowTextures;

    ArrayList<ModelUtil.UnbakedQuad> quadsOpaque;
    ArrayList<ModelUtil.UnbakedQuad> quadsTransparent;

    public ComponentModel(String[] atlasNames, TextureAtlasSprite[] baseTextures, TextureAtlasSprite[] highlightTextures, TextureAtlasSprite[] shadowTextures)
    {
        this.baseTextures=new HashMap<>();
        this.highlightTextures= new HashMap<>();
        this.shadowTextures=new HashMap<>();

        quadsOpaque = new ArrayList<>();
        quadsTransparent = new ArrayList<>();


        for(int i = 0; i < atlasNames.length; i++)
        {
            this.baseTextures.put("#"+atlasNames[i],baseTextures[i]);
            if(highlightTextures[i]!=null)
                this.highlightTextures.put("#"+atlasNames[i],highlightTextures[i]);
            if(shadowTextures[i]!=null)
                this.shadowTextures.put("#"+atlasNames[i],shadowTextures[i]);
        }
    }

    public void addElement(float x1, float y1, float z1, float x2, float y2, float z2, String[] textures, float[] minU, float[] minV, float[] maxU, float[] maxV, RotationAxis axis, float rotationAngle, float originX, float originY, float originZ, int[] uvRot)
    {
        float delta = ANTIZ;

        for(int i = 0; i < 3; i++) {
            float posX1 = x1 - delta;
            float posY1 = y1 + delta;
            float posZ1 = z1 + delta;
            float posX2 = x2 + delta;
            float posY2 = y2 - delta;
            float posZ2 = z2 - delta;

            Vec3d positiontexturevertex7 = new Vec3d(posX1, posY1, posZ1);
            Vec3d positiontexturevertex  = new Vec3d(posX2, posY1, posZ1);
            Vec3d positiontexturevertex1 = new Vec3d(posX2, posY2, posZ1);
            Vec3d positiontexturevertex2 = new Vec3d(posX1, posY2, posZ1);
            Vec3d positiontexturevertex3 = new Vec3d(posX1, posY1, posZ2);
            Vec3d positiontexturevertex4 = new Vec3d(posX2, posY1, posZ2);
            Vec3d positiontexturevertex5 = new Vec3d(posX2, posY2, posZ2);
            Vec3d positiontexturevertex6 = new Vec3d(posX1, posY2, posZ2);



            if(rotationAngle!=0&&axis!= RotationAxis.AXIS_NONE)
            {
                Vec3d off = new Vec3d(originX,originY,originZ);

                //rotationAngle*=-1;

                for(int j = 0; j < 8; j++)
                {
                    Vec3d ptv;
                    switch(j)
                    {
                        case 0:
                            ptv = positiontexturevertex;
                            break;
                        case 1:
                            ptv = positiontexturevertex1;
                            break;
                        case 2:
                            ptv = positiontexturevertex2;
                            break;
                        case 3:
                            ptv = positiontexturevertex3;
                            break;
                        case 4:
                            ptv = positiontexturevertex4;
                            break;
                        case 5:
                            ptv = positiontexturevertex5;
                            break;
                        case 6:
                            ptv = positiontexturevertex6;
                            break;
                        default:
                            ptv = positiontexturevertex7;
                            break;
                    }


                    ptv = ptv.subtract(off);

                    double x_, y_, z_;
                    switch(axis)
                    {
                        case AXIS_X:
                            y_ = (ptv.y* MathHelper.cos(rotationAngle))+(ptv.z* MathHelper.sin(rotationAngle));
                            z_ = (-ptv.y* MathHelper.sin(rotationAngle))+(ptv.z* MathHelper.cos(rotationAngle));
                            ptv = new Vec3d(ptv.x,y_,z_);
                            break;
                        case AXIS_Y:
                            z_ = (ptv.z* MathHelper.cos(rotationAngle))+(ptv.x* MathHelper.sin(rotationAngle));
                            x_ = (-ptv.z* MathHelper.sin(rotationAngle))+(ptv.x* MathHelper.cos(rotationAngle));
                            ptv = new Vec3d(x_,ptv.y,z_);
                            break;
                        case AXIS_Z:
                            x_ = (ptv.x* MathHelper.cos(rotationAngle))+(ptv.y* MathHelper.sin(rotationAngle));
                            y_ = (-ptv.x* MathHelper.sin(rotationAngle))+(ptv.y* MathHelper.cos(rotationAngle));
                            ptv = new Vec3d(x_,y_,ptv.z);
                            break;
                    }

                    ptv = ptv.add(off);

                    switch(j)
                    {
                        case 0:
                            positiontexturevertex = ptv;
                            break;
                        case 1:
                            positiontexturevertex1 = ptv;
                            break;
                        case 2:
                            positiontexturevertex2 = ptv;
                            break;
                        case 3:
                            positiontexturevertex3 = ptv;
                            break;
                        case 4:
                            positiontexturevertex4 = ptv;
                            break;
                        case 5:
                            positiontexturevertex5 = ptv;
                            break;
                        case 6:
                            positiontexturevertex6 = ptv;
                            break;
                        default:
                            positiontexturevertex7 = ptv;
                            break;
                    }
                }
            }

            ArrayList<ModelUtil.UnbakedQuad> quads;
            if(i==0)
                quads=quadsOpaque;
            else
                quads=quadsTransparent;


            //Z- north
            if (textures[0] != null && (i==0?baseTextures.containsKey(textures[0]):(i==1)?highlightTextures.containsKey(textures[0]):shadowTextures.containsKey(textures[0]))) {
                int uvr = uvRot[0];
                if(uvr==0)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex,maxU[0],minV[0],
                            positiontexturevertex7,minU[0],minV[0],
                            positiontexturevertex2,minU[0],maxV[0],
                            positiontexturevertex1,maxU[0],maxV[0], EnumFacing.NORTH,
                            (i==0?baseTextures.get(textures[0]):(i==1)?highlightTextures.get(textures[0]):shadowTextures.get(textures[0])),i));
                else if(uvr==90)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex,minU[0],minV[0],
                            positiontexturevertex7,minU[0],maxV[0],
                            positiontexturevertex2,maxU[0],maxV[0],
                            positiontexturevertex1,maxU[0],minV[0], EnumFacing.NORTH,
                            (i==0?baseTextures.get(textures[0]):(i==1)?highlightTextures.get(textures[0]):shadowTextures.get(textures[0])),i));
                else if(uvr==180)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex,minU[0],maxV[0],
                            positiontexturevertex7,maxU[0],maxV[0],
                            positiontexturevertex2,maxU[0],minV[0],
                            positiontexturevertex1,minU[0],minV[0], EnumFacing.NORTH,
                            (i==0?baseTextures.get(textures[0]):(i==1)?highlightTextures.get(textures[0]):shadowTextures.get(textures[0])),i));
                else
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex,maxU[0],maxV[0],
                            positiontexturevertex7,maxU[0],minV[0],
                            positiontexturevertex2,minU[0],minV[0],
                            positiontexturevertex1,minU[0],maxV[0], EnumFacing.NORTH,
                            (i==0?baseTextures.get(textures[0]):(i==1)?highlightTextures.get(textures[0]):shadowTextures.get(textures[0])),i));
            }
            //X+ east
            if (textures[1] != null && (i==0?baseTextures.containsKey(textures[1]):(i==1)?highlightTextures.containsKey(textures[1]):shadowTextures.containsKey(textures[1]))) {
                int uvr = uvRot[1];
                if(uvr==0)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,maxU[1],minV[1],
                            positiontexturevertex,minU[1],minV[1],
                            positiontexturevertex1,minU[1],maxV[1],
                            positiontexturevertex5,maxU[1],maxV[1], EnumFacing.EAST,
                            (i==0?baseTextures.get(textures[1]):(i==1)?highlightTextures.get(textures[1]):shadowTextures.get(textures[1])),i));
                else if(uvr==90)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,minU[1],minV[1],
                            positiontexturevertex,minU[1],maxV[1],
                            positiontexturevertex1,maxU[1],maxV[1],
                            positiontexturevertex5,maxU[1],minV[1], EnumFacing.EAST,
                            (i==0?baseTextures.get(textures[1]):(i==1)?highlightTextures.get(textures[1]):shadowTextures.get(textures[1])),i));
                else if(uvr==180)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,minU[1],maxV[1],
                            positiontexturevertex,maxU[1],maxV[1],
                            positiontexturevertex1,maxU[1],minV[1],
                            positiontexturevertex5,minU[1],minV[1], EnumFacing.EAST,
                            (i==0?baseTextures.get(textures[1]):(i==1)?highlightTextures.get(textures[1]):shadowTextures.get(textures[1])),i));
                else
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,maxU[1],maxV[1],
                            positiontexturevertex,maxU[1],minV[1],
                            positiontexturevertex1,minU[1],minV[1],
                            positiontexturevertex5,minU[1],maxV[1], EnumFacing.EAST,
                            (i==0?baseTextures.get(textures[1]):(i==1)?highlightTextures.get(textures[1]):shadowTextures.get(textures[1])),i));
            }
            //Z+ south
            if (textures[2] != null && (i==0?baseTextures.containsKey(textures[2]):(i==1)?highlightTextures.containsKey(textures[2]):shadowTextures.containsKey(textures[2]))) {
                int uvr = uvRot[2];
                if(uvr==0)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex3,maxU[2],minV[2],
                            positiontexturevertex4,minU[2],minV[2],
                            positiontexturevertex5,minU[2],maxV[2],
                            positiontexturevertex6,maxU[2],maxV[2], EnumFacing.SOUTH,
                            (i==0?baseTextures.get(textures[2]):(i==1)?highlightTextures.get(textures[2]):shadowTextures.get(textures[2])),i));
                else if(uvr==90)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex3,minU[2],minV[2],
                            positiontexturevertex4,minU[2],maxV[2],
                            positiontexturevertex5,maxU[2],maxV[2],
                            positiontexturevertex6,maxU[2],minV[2], EnumFacing.SOUTH,
                            (i==0?baseTextures.get(textures[2]):(i==1)?highlightTextures.get(textures[2]):shadowTextures.get(textures[2])),i));
                else if(uvr==180)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex3,minU[2],maxV[2],
                            positiontexturevertex4,maxU[2],maxV[2],
                            positiontexturevertex5,maxU[2],minV[2],
                            positiontexturevertex6,minU[2],minV[2], EnumFacing.SOUTH,
                            (i==0?baseTextures.get(textures[2]):(i==1)?highlightTextures.get(textures[2]):shadowTextures.get(textures[2])),i));
                else
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex3,maxU[2],maxV[2],
                            positiontexturevertex4,maxU[2],minV[2],
                            positiontexturevertex5,minU[2],minV[2],
                            positiontexturevertex6,minU[2],maxV[2], EnumFacing.SOUTH,
                            (i==0?baseTextures.get(textures[2]):(i==1)?highlightTextures.get(textures[2]):shadowTextures.get(textures[2])),i));
            }
            //X- west
            if (textures[3] != null && (i==0?baseTextures.containsKey(textures[3]):(i==1)?highlightTextures.containsKey(textures[3]):shadowTextures.containsKey(textures[3]))) {
                int uvr = uvRot[3];
                if(uvr==0)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex7,maxU[3],minV[3],
                            positiontexturevertex3,minU[3],minV[3],
                            positiontexturevertex6,minU[3],maxV[3],
                            positiontexturevertex2,maxU[3],maxV[3], EnumFacing.WEST,
                            (i==0?baseTextures.get(textures[3]):(i==1)?highlightTextures.get(textures[3]):shadowTextures.get(textures[3])),i));
                else if(uvr==90)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex7,minU[3],minV[3],
                            positiontexturevertex3,minU[3],maxV[3],
                            positiontexturevertex6,maxU[3],maxV[3],
                            positiontexturevertex2,maxU[3],minV[3], EnumFacing.WEST,
                            (i==0?baseTextures.get(textures[3]):(i==1)?highlightTextures.get(textures[3]):shadowTextures.get(textures[3])),i));
                else if(uvr==180)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex7,minU[3],maxV[3],
                            positiontexturevertex3,maxU[3],maxV[3],
                            positiontexturevertex6,maxU[3],minV[3],
                            positiontexturevertex2,minU[3],minV[3], EnumFacing.WEST,
                            (i==0?baseTextures.get(textures[3]):(i==1)?highlightTextures.get(textures[3]):shadowTextures.get(textures[3])),i));
                else
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex7,maxU[3],maxV[3],
                            positiontexturevertex3,maxU[3],minV[3],
                            positiontexturevertex6,minU[3],minV[3],
                            positiontexturevertex2,minU[3],maxV[3], EnumFacing.WEST,
                            (i==0?baseTextures.get(textures[3]):(i==1)?highlightTextures.get(textures[3]):shadowTextures.get(textures[3])),i));
            }
            //Y+ up
            if (textures[4] != null && (i==0?baseTextures.containsKey(textures[4]):(i==1)?highlightTextures.containsKey(textures[4]):shadowTextures.containsKey(textures[4]))) {
                int uvr = uvRot[4];
                if(uvr==0)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex1,maxU[4],minV[4],
                            positiontexturevertex2,minU[4],minV[4],
                            positiontexturevertex6,minU[4],maxV[4],
                            positiontexturevertex5,maxU[4],maxV[4], EnumFacing.UP,
                            (i==0?baseTextures.get(textures[4]):(i==1)?highlightTextures.get(textures[4]):shadowTextures.get(textures[4])),i));
                else if(uvr==90)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex1,minU[4],minV[4],
                            positiontexturevertex2,minU[4],maxV[4],
                            positiontexturevertex6,maxU[4],maxV[4],
                            positiontexturevertex5,maxU[4],minV[4], EnumFacing.UP,
                            (i==0?baseTextures.get(textures[4]):(i==1)?highlightTextures.get(textures[4]):shadowTextures.get(textures[4])),i));
                else if(uvr==180)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex1,minU[4],maxV[4],
                            positiontexturevertex2,maxU[4],maxV[4],
                            positiontexturevertex6,maxU[4],minV[4],
                            positiontexturevertex5,minU[4],minV[4], EnumFacing.UP,
                            (i==0?baseTextures.get(textures[4]):(i==1)?highlightTextures.get(textures[4]):shadowTextures.get(textures[4])),i));
                else
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex1,maxU[4],maxV[4],
                            positiontexturevertex2,maxU[4],minV[4],
                            positiontexturevertex6,minU[4],minV[4],
                            positiontexturevertex5,minU[4],maxV[4], EnumFacing.UP,
                            (i==0?baseTextures.get(textures[4]):(i==1)?highlightTextures.get(textures[4]):shadowTextures.get(textures[4])),i));
            }
            //Y- down
            if (textures[5] != null && (i==0?baseTextures.containsKey(textures[5]):(i==1)?highlightTextures.containsKey(textures[5]):shadowTextures.containsKey(textures[5]))) {
                int uvr = uvRot[5];
                if(uvr==0)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,maxU[5],minV[5],
                            positiontexturevertex3,minU[5],minV[5],
                            positiontexturevertex7,minU[5],maxV[5],
                            positiontexturevertex,maxU[5],maxV[5], EnumFacing.DOWN,
                            (i==0?baseTextures.get(textures[5]):(i==1)?highlightTextures.get(textures[5]):shadowTextures.get(textures[5])),i));
                else if(uvr==90)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,minU[5],minV[5],
                            positiontexturevertex3,minU[5],maxV[5],
                            positiontexturevertex7,maxU[5],maxV[5],
                            positiontexturevertex,maxU[5],minV[5], EnumFacing.DOWN,
                            (i==0?baseTextures.get(textures[5]):(i==1)?highlightTextures.get(textures[5]):shadowTextures.get(textures[5])),i));
                else if(uvr==180)
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,minU[5],maxV[5],
                            positiontexturevertex3,maxU[5],maxV[5],
                            positiontexturevertex7,maxU[5],minV[5],
                            positiontexturevertex,minU[5],minV[5], EnumFacing.DOWN,
                            (i==0?baseTextures.get(textures[5]):(i==1)?highlightTextures.get(textures[5]):shadowTextures.get(textures[5])),i));
                else
                    quads.add(new ModelUtil.UnbakedQuad(-1,
                            positiontexturevertex4,maxU[5],maxV[5],
                            positiontexturevertex3,maxU[5],minV[5],
                            positiontexturevertex7,minU[5],minV[5],
                            positiontexturevertex,minU[5],maxV[5], EnumFacing.DOWN,
                            (i==0?baseTextures.get(textures[5]):(i==1)?highlightTextures.get(textures[5]):shadowTextures.get(textures[5])),i));
            }

            if(i==0)
                delta+=ANTIZ;
        }
    }


    public List<BakedQuad> bakeOpaque(float scale) {
        ArrayList<BakedQuad> bakedQuads = new ArrayList<>();

        for (ModelUtil.UnbakedQuad quad : quadsOpaque) {
            bakedQuads.add(quad.bake(scale));
        }

        return bakedQuads;
    }

    public List<BakedQuad> bakeTransparent(float scale) {
        ArrayList<BakedQuad> bakedQuads = new ArrayList<>();

        for (ModelUtil.UnbakedQuad quad : quadsTransparent) {
            bakedQuads.add(quad.bake(scale));
        }

        return bakedQuads;
    }

    public enum RotationAxis {
        AXIS_X,
        AXIS_Y,
        AXIS_Z,
        AXIS_NONE
    }
}
