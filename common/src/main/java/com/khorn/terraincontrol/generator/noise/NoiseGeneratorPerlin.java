package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorPerlin
{
    private static final class Float2 {
        public final float x, y;

        public Float2(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Float3 {
        public final float x, y, z;

        public Float3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final Float2[] GRAD_2D = {
        new Float2(-1, -1), new Float2(1, -1), new Float2(-1, 1), new Float2(1, 1),
        new Float2(0, -1), new Float2(-1, 0), new Float2(0, 1), new Float2(1, 0),
    };

    private static final Float3[] GRAD_3D = {
        new Float3(1, 1, 0), new Float3(-1, 1, 0), new Float3(1, -1, 0), new Float3(-1, -1, 0),
        new Float3(1, 0, 1), new Float3(-1, 0, 1), new Float3(1, 0, -1), new Float3(-1, 0, -1),
        new Float3(0, 1, 1), new Float3(0, -1, 1), new Float3(0, 1, -1), new Float3(0, -1, -1),
        new Float3(1, 1, 0), new Float3(0, -1, 1), new Float3(-1, 1, 0), new Float3(0, -1, -1),
    };

    private int seed;

    public NoiseGeneratorPerlin(Random random)
    {
        seed = (int)random.nextLong();
    }

    private static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
    private static int fastFloor(float f) {
        return (f >= 0 ? (int) f : (int) f - 1);
    }
    private static float interpQuinticFunc(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // Hashing
    private final static int X_PRIME = 1619;
    private final static int Y_PRIME = 31337;
    private final static int Z_PRIME = 6971;
    //private final static int W_PRIME = 1013;

    private Float2 gradCoord2D(int x, int y) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        return GRAD_2D[hash & 7];
    }

    private static float gradDot(Float2 g, float x, float y)
    {
        return g.x * x + g.y * y;
    }

    private Float3 gradCoord3D(int x, int y, int z) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;
        hash ^= Z_PRIME * z;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        return GRAD_3D[hash & 15];
    }

    private static float gradDot(Float3 g, float x, float y, float z)
    {
        return g.x * x + g.y * y + g.z * z;
    }

    public void populateNoiseArray3D(double NoiseArray[], double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double noiseScale)
    {
        int index = 0;
        double invNoiseScale = 1.0D / noiseScale;

        int y0Last = 0;
        Float3 g000 = null;
        Float3 g001 = null;
        Float3 g010 = null;
        Float3 g011 = null;
        Float3 g100 = null;
        Float3 g101 = null;
        Float3 g110 = null;
        Float3 g111 = null;

        for (int ix = 0; ix < xSize; ix++)
        {
            float x = (float)(xOffset + ix * xScale);
            int x0 = fastFloor(x);
            int x1 = x0 + 1;
            float xd0 = x - x0;
            float xd1 = xd0 - 1;
            float xs = interpQuinticFunc(x - x0);

            for (int iz = 0; iz < zSize; iz++)
            {
                float z = (float)(zOffset + iz * zScale);
                int z0 = fastFloor(z);
                int z1 = z0 + 1;
                float zd0 = z - z0;
                float zd1 = zd0 - 1;
                float zs = interpQuinticFunc(z - z0);

                for (int iy = 0; iy < ySize; iy++)
                {
                    float y = (float)(yOffset + iy * yScale);
                    int y0 = fastFloor(y);
                    float ys = interpQuinticFunc(y - y0);
                    float yd0 = y - y0;
                    float yd1 = yd0 - 1;

                    if (iy == 0 || y0 != y0Last) {
                        y0Last = z0;
                        int y1 = y0 + 1;

                        g000 = gradCoord3D(x0, y0, z0);
                        g001 = gradCoord3D(x0, y0, z1);
                        g010 = gradCoord3D(x0, y1, z0);
                        g011 = gradCoord3D(x0, y1, z1);
                        g100 = gradCoord3D(x1, y0, z0);
                        g101 = gradCoord3D(x1, y0, z1);
                        g110 = gradCoord3D(x1, y1, z0);
                        g111 = gradCoord3D(x1, y1, z1);
                    }

                    float xf00 = lerp(gradDot(g000, xd0, yd0, zd0), gradDot(g100, xd1, yd0, zd0), xs);
                    float xf01 = lerp(gradDot(g001, xd0, yd0, zd1), gradDot(g101, xd1, yd0, zd1), xs);
                    float xf10 = lerp(gradDot(g010, xd0, yd1, zd0), gradDot(g110, xd1, yd1, zd0), xs);
                    float xf11 = lerp(gradDot(g011, xd0, yd1, zd1), gradDot(g111, xd1, yd1, zd1), xs);

                    float zf0 = lerp(xf00, xf01, zs);
                    float zf1 = lerp(xf10, xf11, zs);

                    NoiseArray[index++] += lerp(zf0, zf1, ys) * invNoiseScale;
                }
            }
        }
    }

    public void populateNoiseArray2D(double NoiseArray[], double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double noiseScale)
    {
        int index = 0;
        double invNoiseScale = 1.0D / noiseScale;

        int z0Last = 0;
        Float2 g00 = null;
        Float2 g01 = null;
        Float2 g10 = null;
        Float2 g11 = null;

        for (int ix = 0; ix < xSize; ix++)
        {
            float x = (float)(xOffset + ix * xScale);
            int x0 = fastFloor(x);
            int x1 = x0 + 1;
            float xd0 = x - x0;
            float xd1 = xd0 - 1;
            float xs = interpQuinticFunc(x - x0);

            for (int iz = 0; iz < zSize; iz++) {
                float z = (float) (zOffset + iz * zScale);
                int z0 = fastFloor(z);
                float zs = interpQuinticFunc(z - z0);
                float zd0 = z - z0;
                float zd1 = zd0 - 1;

                if (iz == 0 || z0 != z0Last){
                    z0Last = z0;
                    int z1 = z0 + 1;

                    g00 = gradCoord2D(x0, z0);
                    g01 = gradCoord2D(x0, z1);
                    g10 = gradCoord2D(x1, z0);
                    g11 = gradCoord2D(x1, z1);
                }

                float xf0 = lerp(gradDot(g00, xd0, zd0), gradDot(g10, xd1, zd0), xs);
                float xf1 = lerp(gradDot(g01, xd0, zd1), gradDot(g11, xd1, zd1), xs);

                NoiseArray[index++] += lerp(xf0, xf1, zs) * invNoiseScale;
            }
        }

    }
}
