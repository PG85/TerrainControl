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

    private static float Lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
    private static int FastFloor(float f) {
        return (f >= 0 ? (int) f : (int) f - 1);
    }
    private static float InterpQuinticFunc(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // Hashing
    private final static int X_PRIME = 1619;
    private final static int Y_PRIME = 31337;
    private final static int Z_PRIME = 6971;
    //private final static int W_PRIME = 1013;

    private static float GradCoord2D(int seed, int x, int y, float xd, float yd) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        Float2 g = GRAD_2D[hash & 7];

        return xd * g.x + yd * g.y;
    }

    private static float GradCoord3D(int seed, int x, int y, int z, float xd, float yd, float zd) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;
        hash ^= Z_PRIME * z;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        Float3 g = GRAD_3D[hash & 15];

        return xd * g.x + yd * g.y + zd * g.z;
    }

    public void populateNoiseArray3D(double NoiseArray[], double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double noiseScale)
    {
        int index = 0;
        double invNoiseScale = 1.0D / noiseScale;

        int y0Last = 0;
        float xf00, xf10, xf01, xf11;
        float zf0 = 0;
        float zf1 = 0;

        for (int ix = 0; ix < xSize; ix++)
        {
            float x = (float)(xOffset + ix * xScale);
            int x0 = FastFloor(x);
            int x1 = x0 + 1;
            float xd0 = x - x0;
            float xd1 = xd0 - 1;
            float xs = InterpQuinticFunc(x - x0);

            for (int iz = 0; iz < zSize; iz++)
            {
                float z = (float)(zOffset + iz * zScale);
                int z0 = FastFloor(z);
                int z1 = z0 + 1;
                float zd0 = z - z0;
                float zd1 = zd0 - 1;
                float zs = InterpQuinticFunc(z - z0);

                for (int iy = 0; iy < ySize; iy++)
                {
                    float y = (float)(yOffset + iy * yScale);
                    int y0 = FastFloor(y);
                    float ys = InterpQuinticFunc(y - y0);

                    if (iy == 0 || y0 != y0Last) {
                        y0Last = y0;
                        int y1 = y0 + 1;
                        float yd0 = y - y0;
                        float yd1 = yd0 - 1;

                        xf00 = Lerp(GradCoord3D(seed, x0, y0, z0, xd0, yd0, zd0), GradCoord3D(seed, x1, y0, z0, xd1, yd0, zd0), xs);
                        xf01 = Lerp(GradCoord3D(seed, x0, y0, z1, xd0, yd0, zd1), GradCoord3D(seed, x1, y0, z1, xd1, yd0, zd1), xs);
                        xf10 = Lerp(GradCoord3D(seed, x0, y1, z0, xd0, yd1, zd0), GradCoord3D(seed, x1, y1, z0, xd1, yd1, zd0), xs);
                        xf11 = Lerp(GradCoord3D(seed, x0, y1, z1, xd0, yd1, zd1), GradCoord3D(seed, x1, y1, z1, xd1, yd1, zd1), xs);

                        zf0 = Lerp(xf00, xf01, zs);
                        zf1 = Lerp(xf10, xf11, zs);
                    }

                    NoiseArray[index++] += Lerp(zf0, zf1, ys) * invNoiseScale;
                }
            }
        }
    }

    public void populateNoiseArray2D(double NoiseArray[], double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double noiseScale)
    {
        int index = 0;
        double invNoiseScale = 1.0D / noiseScale;

        int z0Last = 0;
        float xf0 = 0;
        float xf1 = 0;

        for (int ix = 0; ix < xSize; ix++)
        {
            float x = (float)(xOffset + ix * xScale);
            int x0 = FastFloor(x);
            int x1 = x0 + 1;
            float xd0 = x - x0;
            float xd1 = xd0 - 1;
            float xs = InterpQuinticFunc(x - x0);

            for (int iz = 0; iz < zSize; iz++)
            {
                float z = (float)(zOffset + iz * zScale);
                int z0 = FastFloor(z);
                float zs = InterpQuinticFunc(z - z0);

                if (iz == 0 || z0 != z0Last)
                {
                    z0Last = z0;
                    int z1 = z0 + 1;
                    float zd0 = z - z0;
                    float zd1 = zd0 - 1;

                    xf0 = Lerp(GradCoord2D(seed, x0, z0, xd0, zd0), GradCoord2D(seed, x1, z0, xd1, zd0), xs);
                    xf1 = Lerp(GradCoord2D(seed, x0, z1, xd0, zd1), GradCoord2D(seed, x1, z1, xd1, zd1), xs);
                }

                NoiseArray[index++] += Lerp(xf0, xf1, zs) * invNoiseScale;
            }
        }

    }
}
