package com.pg85.otg.generator.resource;

import com.pg85.otg.common.LocalMaterialData;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

public class BoulderGen extends Resource
{
    private MaterialSet sourceBlocks;
    private int minAltitude;
    private int maxAltitude;
    private boolean bLoaded = false;

    public BoulderGen(BiomeConfig config, List<String> args) throws InvalidConfigException
    {
        super(config);
        assureSize(6, args);

        material = readMaterial(args.get(0));
        frequency = readInt(args.get(1), 1, 5000);
        rarity = readRarity(args.get(2));
        minAltitude = readInt(args.get(3), PluginStandardValues.WORLD_DEPTH,
                PluginStandardValues.WORLD_HEIGHT);
        maxAltitude = readInt(args.get(4), minAltitude,
                PluginStandardValues.WORLD_HEIGHT);
        sourceBlocks = readMaterials(args, 5);
    }

    @Override
    public void spawn(LocalWorld world, Random random, boolean villageInChunk, int x, int z)
    {
        int y = world.getHighestBlockYAt(x, z);
        if (y < this.minAltitude || y > this.maxAltitude) {
            return;
        }
        
        if (!bLoaded ) {
            parseMaterials(world, material, sourceBlocks);
            bLoaded = true;
        }

        while (y > 3)
        {
            LocalMaterialData material = world.getMaterial(x, y - 1, z, false);
            if (sourceBlocks.contains(material)) {
                break;
            }
            y--;
        }
        if (y <= 3)
        {
            return;
        }

        int i = 0;
        int j = 0;
        while ((i >= 0) && (j < 3))
        {
            int k = i + random.nextInt(2);
            int m = i + random.nextInt(2);
            int n = i + random.nextInt(2);
            float f1 = (k + m + n) * 0.333F + 0.5F;
            for (int i1 = x - k; i1 <= x + k; i1++)
            {
                for (int i2 = z - n; i2 <= z + n; i2++)
                {
                    for (int i3 = y - m; i3 <= y + m; i3++)
                    {
                        float f2 = i1 - x;
                        float f3 = i2 - z;
                        float f4 = i3 - y;
                        if (f2 * f2 + f3 * f3 + f4 * f4 <= f1 * f1)
                        {
                            world.setBlock(i1, i3, i2, this.material, null, false);
                        }
                    }
                }
            }
            x += random.nextInt(2 + i * 2) - 1 - i;
            z += random.nextInt(2 + i * 2) - 1 - i;
            y -= random.nextInt(2);
            j++;
        }
    }

    @Override
    public String toString()
    {
        return "Boulder(" + material + "," + frequency + "," + rarity + "," + minAltitude + "," + maxAltitude + makeMaterials(sourceBlocks) + ")";
    }

    @Override
    public int getPriority()
    {
        return -22;
    }

}
