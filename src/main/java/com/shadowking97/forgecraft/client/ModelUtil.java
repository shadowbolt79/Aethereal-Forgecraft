package com.shadowking97.forgecraft.client;

import com.google.common.primitives.Ints;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Shadow Bolt on 8/15/2017.
 */
public class ModelUtil {
    public static class UnbakedQuad {
        public Vec3d p1, p2, p3, p4;
        public float u1, v1, u2, v2, u3, v3, u4, v4;

        public EnumFacing side;
        public int conditionID;

        public TextureAtlasSprite sprite;

        public int tintIndex;

        public UnbakedQuad(int condition,
                           Vec3d p1, float u1, float v1,
                           Vec3d p2, float u2, float v2,
                           Vec3d p3, float u3, float v3,
                           Vec3d p4, float u4, float v4,
                           EnumFacing side, TextureAtlasSprite sprite, int tintIndex) {

            this.conditionID = condition;

            this.p1 = p1;
            this.u1 = u1;
            this.v1 = v1;

            this.p2 = p2;
            this.u2 = u2;
            this.v2 = v2;

            this.p3 = p3;
            this.u3 = u3;
            this.v3 = v3;

            this.p4 = p4;
            this.u4 = u4;
            this.v4 = v4;

            this.side = side;

            this.sprite=sprite;
            this.tintIndex=tintIndex;
        }

        public UnbakedQuad(int condition,
                           Vec3d p1, float u1, float v1,
                           Vec3d p2, float u2, float v2,
                           Vec3d p3, float u3, float v3,
                           Vec3d p4, float u4, float v4,
                           EnumFacing side, TextureAtlasSprite sprite) {

            this(condition,p1,u1,v1,p2,u2,v2,p3,u3,v3,p4,u4,v4,side,sprite,-1);
        }

        public BakedQuad bake()
        {
            return bake(1.0f);
        }

        public BakedQuad bake(float scale) {
            return new BakedQuad(Ints.concat(
                    vertexToInts(p1.x*scale, p1.y*scale, p1.z*scale, u1, v1, sprite),
                    vertexToInts(p2.x*scale, p2.y*scale, p2.z*scale, u2, v2, sprite),
                    vertexToInts(p3.x*scale, p3.y*scale, p3.z*scale, u3, v3, sprite),
                    vertexToInts(p4.x*scale, p4.y*scale, p4.z*scale, u4, v4, sprite)
            ), tintIndex, side, sprite, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
        }
    }

    public static int[] vertexToInts(double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
        return new int[] {
                Float.floatToRawIntBits((float) x),
                Float.floatToRawIntBits((float) y),
                Float.floatToRawIntBits((float) z),
                -1,
                Float.floatToRawIntBits(sprite.getInterpolatedU(u)),
                Float.floatToRawIntBits(sprite.getInterpolatedV(v)),
                0
        };
    }
}
