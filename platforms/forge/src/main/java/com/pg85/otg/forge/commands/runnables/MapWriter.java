package com.pg85.otg.forge.commands.runnables;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pg85.otg.OTG;
import com.pg85.otg.common.LocalBiome;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.forge.biomes.OTGBiome;
import com.pg85.otg.forge.commands.BaseCommand;
import com.pg85.otg.forge.util.WorldHelper;
import com.pg85.otg.logging.LogMarker;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;;

public class MapWriter implements Runnable
{
    public static final int[] defaultColors = {0x3333FF, 0x999900, 0xFFCC33, 0x333300, 0x00FF00, 0x007700, 0x99cc66, 0x00CCCC, 0, 0, 0xFFFFFF, 0x66FFFF, 0xCCCCCC, 0xCC9966, 0xFF33cc, 0xff9999, 0xFFFF00, 0x996600, 0x009900, 0x003300, 0x666600};

    public static boolean isWorking = false;

    private World world;
    private int size;
    private ICommandSender sender;
    private Angle angle;
    private int offsetX;
    private int offsetZ;
    private String label;

    public enum Angle
    {
        d0,
        d90,
        d180,
        d270
    }

    public MapWriter(World mcWorld, int _size, Angle _angle, ICommandSender _sender, int _offsetX, int _offsetZ, String _label)
    {
        this.world = mcWorld;
        this.size = _size;
        this.sender = _sender;
        this.angle = _angle;
        this.offsetX = _offsetX;
        this.offsetZ = _offsetZ;
        this.label = _label;
    }

    /**
     * Gets the colors of all biomes, indexed by biome id.
     * 
     * @param world2 The world to get the colors from. Doesn't have to be managed by
     *               Open Terrain Generator
     * @return The colors, indexed by biome id.
     */
    private int[] getColors(World world2)
    {
        // OTG.log(LogMarker.DEBUG, "BukkitWorld::UUID:: {}",
        // world.getWorldInfo().geti;
        LocalWorld bukkitWorld = OTG.getWorld(WorldHelper.getName(world2));
        if (bukkitWorld == null)
        {
            // OTG.log(LogMarker.ERROR, "BukkitWorld is null :: Make sure you
            // add `{}` to
            // bukkit.yml", (Object) WorldHelper.get;
            return defaultColors;
        }

        LocalBiome[] biomes = bukkitWorld.getConfigs().getBiomeArrayByOTGId();
        int[] colors = new int[biomes.length];
        OTG.log(LogMarker.DEBUG, "BukkitWorld settings biomes.length::{}", biomes.length);

        for (LocalBiome biome : biomes)
        {
            if (biome != null)
            {
                BiomeConfig biomeConfig = biome.getBiomeConfig();
                int color = biomeConfig.biomeColor;
                colors[biome.getIds().getOTGBiomeId()] = color;
            }
        }
        return colors;
    }

    @Override
    public void run()
    {
        if (MapWriter.isWorking)
        {
            sender.sendMessage(
                    new TextComponentString(BaseCommand.ERROR_COLOR + "Another instance of map writer is running"));
            return;
        }

        MapWriter.isWorking = true;
        int height = size;
        int width = size;
        LocalWorld localWorld = OTG.getWorld(WorldHelper.getName(world));

        int[] colors = this.getColors(world);

        sender.sendMessage(new TextComponentString(BaseCommand.MESSAGE_COLOR + "Generating map..."));
        sender.sendMessage(
                new TextComponentString(BaseCommand.MESSAGE_COLOR + "You may notice a short performance impact during this process."));

        Biome[] biomeBuffer = new Biome[256];
        // long time = System.currentTimeMillis();

        BufferedImage biomeImage = new BufferedImage(height * 16, width * 16, BufferedImage.TYPE_INT_RGB);
        BufferedImage temperatureImage = new BufferedImage(height * 16, width * 16, BufferedImage.TYPE_INT_RGB);

        int imageX = 0;
        int imageY = 0;

        for (int x = -height / 2; x < height / 2; x++)
        {
            for (int z = -width / 2; z < width / 2; z++)
            {
                // Forge currently generates the map all at once, so this isn't
                // needed.

                /*
                 * long time2 = System.currentTimeMillis();
                 * 
                 * if (time2 < time) { time = time2; }
                 * 
                 * if (time2 > time + 2000L) { sender.sendMessage(new
                 * TextComponentString( BaseCommand.MESSAGE_COLOR.toString() +
                 * ((x + height / 2) * 100 / height) + "%")); time = time2; }
                 */

                biomeBuffer = world.getBiomeProvider().getBiomesForGeneration(biomeBuffer, offsetX + x * 16,
                        offsetZ + z * 16, 16, 16);
                for (int x1 = 0; x1 < 16; x1++)
                {
                    for (int z1 = 0; z1 < 16; z1++)
                    {

                        switch (this.angle)
                        {
                            case d0:
                                imageX = (x + height / 2) * 16 + x1;
                                imageY = (z + width / 2) * 16 + z1;
                                break;
                            case d90:
                                imageX = width * 16 - ((z + width / 2) * 16 + z1 + 1);
                                imageY = (x + height / 2) * 16 + x1;
                                break;
                            case d180:
                                imageX = height * 16 - ((x + height / 2) * 16 + x1 + 1);
                                imageY = width * 16 - ((z + width / 2) * 16 + z1 + 1);
                                break;
                            case d270:
                                imageX = (z + width / 2) * 16 + z1;
                                imageY = height * 16 - ((x + height / 2) * 16 + x1 + 1);
                                break;
                        }

                        int arrayPosition = x1 + 16 * z1;
                        int biomeId = OTGBiome.getIdForBiome(biomeBuffer[arrayPosition]);
                        try {
                            // Biome color
                            biomeImage.setRGB(imageX, imageY, colors[biomeId]);

                            // Temperature
                            Color temperatureColor = getBiomeTemperatureColor(biomeBuffer[arrayPosition], localWorld);
                            temperatureImage.setRGB(imageX, imageY, temperatureColor.getRGB());
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            OTG.log(LogMarker.DEBUG, "BiomeBuff Idx::{}<{}x/{}z>, Len::{}, ID::{} | Colors Len::{}",
                                    new Object[] {arrayPosition, x1, z1, biomeBuffer.length, OTGBiome.getIdForBiome(
                                            biomeBuffer[arrayPosition]), colors.length});
                        }
                    }
                }
            }
        }

        sender.sendMessage(new TextComponentString(BaseCommand.MESSAGE_COLOR + "Writing images..."));

        try {
            // Write biome colors
            ImageIO.write(biomeImage, "png", new File(label + world.getWorldInfo().getWorldName() + "_biome.png"));

            // Write temperatures
            ImageIO.write(temperatureImage, "png",
                    new File(label + world.getWorldInfo().getWorldName() + "_temperature.png"));

            sender.sendMessage(new TextComponentString(BaseCommand.MESSAGE_COLOR + "Done"));
        } catch (IOException e) {
            sender.sendMessage(
                    new TextComponentString(BaseCommand.ERROR_COLOR + "Exception while writing images: " + e.getLocalizedMessage()));
            OTG.log(LogMarker.ERROR, "Failed to write image.");
            OTG.printStackTrace(LogMarker.ERROR, e);
        }

        MapWriter.isWorking = false;
    }

    /**
     * Gets the temperature color of a single biome. Starts at blue, goes to green,
     * red and darker red for increasing temperatures.
     * 
     * @param biome The biome to get the temperature from.
     * @param world The world the biome is in. May be null if the world isn't
     *              managed by Open Terrain Generator
     * @return The temperature color.
     */
    private Color getBiomeTemperatureColor(Biome biome, LocalWorld world)
    {
        float temperature;
        if (world != null)
        {
            temperature = world.getBiomeByOTGIdOrNull(OTGBiome.getIdForBiome(biome)).getBiomeConfig().biomeTemperature;
        } else {
            temperature = biome.getDefaultTemperature();
        }

        // Prevents us from going around the color wheel twice or getting into
        // the purple colors
        float cappedTemperature = Math.min(1.0f, temperature);

        return Color.getHSBColor(0.7f - cappedTemperature * 0.7f, 0.9f, temperature * 0.7f + 0.3f);
    }

}
