package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorNew {

    private static final class Float2 {
        public final float x, y;

        public Float2(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final Float2[] GRAD_2D = {
        new Float2(-1, -1), new Float2(1, -1), new Float2(-1, 1), new Float2(1, 1),
        new Float2(0, -1), new Float2(-1, 0), new Float2(0, 1), new Float2(1, 0),
    };

    private int seed;

    public NoiseGeneratorNew() {
        this(new Random());
    }

    public NoiseGeneratorNew(Random random) {

        seed = (int)random.nextLong();
    }

    private static int fastFloor(float f) {
        return (f >= 0 ? (int) f : (int) f - 1);
    }

    // Hashing
    private final static int X_PRIME = 1619;
    private final static int Y_PRIME = 31337;
    //private final static int Z_PRIME = 6971;
    //private final static int W_PRIME = 1013;

    private static float gradCoord2D(int seed, int x, int y, float xd, float yd) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        Float2 g = GRAD_2D[hash & 7];

        return xd * g.x + yd * g.y;
    }

    private final static float SQRT3 = 1.7320508075f;
    private final static float F2 = 0.5f * (SQRT3 - 1.0f);
    private final static float G2 = (3.0f - SQRT3) / 6.0f;

    public double a(double x, double y) {
        float xf = (float)x;
        float yf = (float)y;

        float f = (xf + yf) * F2;
        int i = fastFloor(xf + f);
        int j = fastFloor(yf + f);

        float t = (i + j) * G2;
        float X0 = i - t;
        float Y0 = j - t;

        float x0 = xf - X0;
        float y0 = yf - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        float x1 = x0 - i1 + G2;
        float y1 = y0 - j1 + G2;
        float y2 = y0 - 1 + 2 * G2;
        float x2 = x0 - 1 + 2 * G2;

        float n0, n1, n2;

        t = (float) 0.5 - x0 * x0 - y0 * y0;
        if (t < 0) n0 = 0;
        else {
            t *= t;
            n0 = t * t * gradCoord2D(seed, i, j, x0, y0);
        }

        t = (float) 0.5 - x1 * x1 - y1 * y1;
        if (t < 0) n1 = 0;
        else {
            t *= t;
            n1 = t * t * gradCoord2D(seed, i + i1, j + j1, x1, y1);
        }

        t = (float) 0.5 - x2 * x2 - y2 * y2;
        if (t < 0) n2 = 0;
        else {
            t *= t;
            n2 = t * t * gradCoord2D(seed, i + 1, j + 1, x2, y2);
        }

        return 70f * (n0 + n1 + n2);
    }

    public void a(double[] doubleArray, double xStart, double yStart, int xSize, int ySize, double xScale, double yScale, double noiseScale) {
        int index = 0;

        for (int iy = 0; iy < ySize; ++iy) {
            float yf = (float)((yStart + iy) * yScale);

            for (int ix = 0; ix < xSize; ++ix) {
                float xf = (float)((xStart + ix) * xScale);

                float f = (xf + yf) * F2;
                int i = fastFloor(xf + f);
                int j = fastFloor(yf + f);

                float t = (i + j) * G2;
                float X0 = i - t;
                float Y0 = j - t;

                float x0 = xf - X0;
                float y0 = yf - Y0;

                int i1, j1;
                if (x0 > y0) {
                    i1 = 1;
                    j1 = 0;
                } else {
                    i1 = 0;
                    j1 = 1;
                }

                float x1 = x0 - i1 + G2;
                float y1 = y0 - j1 + G2;
                float y2 = y0 - 1 + 2 * G2;
                float x2 = x0 - 1 + 2 * G2;

                float n0, n1, n2;

                t = (float) 0.5 - x0 * x0 - y0 * y0;
                if (t < 0) n0 = 0;
                else {
                    t *= t;
                    n0 = t * t * gradCoord2D(seed, i, j, x0, y0);
                }

                t = (float) 0.5 - x1 * x1 - y1 * y1;
                if (t < 0) n1 = 0;
                else {
                    t *= t;
                    n1 = t * t * gradCoord2D(seed, i + i1, j + j1, x1, y1);
                }

                t = (float) 0.5 - x2 * x2 - y2 * y2;
                if (t < 0) n2 = 0;
                else {
                    t *= t;
                    n2 = t * t * gradCoord2D(seed, i + 1, j + 1, x2, y2);
                }

                doubleArray[index++] += 70.0D * (n0 + n1 + n2) * noiseScale;
            }
        }
    }
}