package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorNewOctaves {

    private NoiseGeneratorNew[] noiseOctaves;
    private int octaves;

    public NoiseGeneratorNewOctaves(Random random, int i) {
        octaves = i;
        noiseOctaves = new NoiseGeneratorNew[i];

        for (int j = 0; j < i; ++j) {
            noiseOctaves[j] = new NoiseGeneratorNew(random);
        }
    }

    public double a(double x, double y) {
        double sum = 0.0D;
        double amp = 1.0D;
        double lacunarity = 2.0D;

        for (int i = 0; i < octaves; ++i) {
            sum += noiseOctaves[i].a(x, y) * amp;
            amp *= 0.5D;
            x *= lacunarity;
            y *= lacunarity;
        }

        return sum;
    }

    public double[] a(double[] doubleArray, double xStart, double yStart, int xSize, int zSize, double xScale, double yScale, double d4) {
        return this.a(doubleArray, xStart, yStart, xSize, zSize, xScale, yScale, d4, 0.5D);
    }

    public double[] a(double[] doubleArray, double xStart, double yStart, int xSize, int zSize, double xScale, double yScale, double d4, double noiseScale) {
        if (doubleArray != null && doubleArray.length >= xSize * zSize) {
            for (int k = 0; k < doubleArray.length; ++k) {
                doubleArray[k] = 0.0D;
            }
        } else {
            doubleArray = new double[xSize * zSize];
        }

        double currentScale = 1.0D;
        double d7 = 1.0D;

        for (int l = 0; l < this.octaves; ++l) {
            this.noiseOctaves[l].a(doubleArray, xStart, yStart, xSize, zSize, xScale * d7 * currentScale, yScale * d7 * currentScale, 0.55D / currentScale);
            d7 *= d4;
            currentScale *= noiseScale;
        }

        return doubleArray;
    }
}