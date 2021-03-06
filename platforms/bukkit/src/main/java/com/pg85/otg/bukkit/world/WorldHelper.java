package com.pg85.otg.bukkit.world;

import com.pg85.otg.OTG;
import com.pg85.otg.bukkit.biomes.OTGBiomeBase;
import com.pg85.otg.common.LocalWorld;

import net.minecraft.server.v1_12_R1.BiomeBase;

public abstract class WorldHelper
{
    /**
     * Returns the LocalWorld of the Minecraft world. Returns null if TC isn't
     * loaded for that world.
     *
     * @param world The world.
     * @return The LocalWorld, or null if there is none.
     */
    public static LocalWorld toLocalWorld(net.minecraft.server.v1_12_R1.World world)
    {
        return OTG.getWorld(world.getWorld().getName());
    }

    /**
     * Returns the LocalWorld of the CraftBukkit world. Returns null if TC
     * isn't loaded for that world.
     *
     * @param world The world.
     * @return The LocalWorld, or null if there is none.
     */
    public static LocalWorld toLocalWorld(org.bukkit.World world)
    {
        return OTG.getWorld(world.getName());
    }

    /**
     * Gets the generation id of the given biome. This is usually equal to the
     * id of the BiomeBase, but when using virtual biomes it may be different.
     *
     * @param biomeBase The biome to check.
     * @return The generation id.
     */
    public static int getOTGBiomeId(BiomeBase biomeBase)
    {
        if (biomeBase instanceof OTGBiomeBase)
        {
            return ((OTGBiomeBase) biomeBase).otgBiomeId;
        }
        return BiomeBase.a(biomeBase);
    }

    /**
     * Gets the saved id of the given biome.
     *
     * @param biomeBase The biome.
     * @return The id.
     */
    public static int getSavedId(BiomeBase biomeBase)
    {
        return BiomeBase.a(biomeBase);
    }

    private WorldHelper()
    {
    }
}
