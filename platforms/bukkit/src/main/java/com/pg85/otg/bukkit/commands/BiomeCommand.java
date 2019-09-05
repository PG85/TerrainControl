package com.pg85.otg.bukkit.commands;

import com.pg85.otg.bukkit.OTGPerm;
import com.pg85.otg.bukkit.OTGPlugin;
import com.pg85.otg.common.BiomeIds;
import com.pg85.otg.common.LocalBiome;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.exception.BiomeNotFoundException;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BiomeCommand extends BaseCommand
{
    BiomeCommand(OTGPlugin _plugin)
    {
        super(_plugin);
        name = "biome";
        perm = OTGPerm.CMD_BIOME.node;
        usage = "biome [-f] [-s]";
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args)
    {
        Location location = this.getLocation(sender);
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        LocalWorld world = this.getWorld(sender, "");
        
        if (world == null)
        {
            sender.sendMessage(ERROR_COLOR + "Plugin is not enabled for this world.");
            return true;
        }

        LocalBiome biome = world.getBiome(x, z);
        BiomeIds biomeIds = biome.getIds();

        sender.sendMessage(MESSAGE_COLOR + "According to the biome generator, you are in the " + VALUE_COLOR + biome.getName() + MESSAGE_COLOR + " biome, with id " + VALUE_COLOR
                + biomeIds.getOTGBiomeId());

        if (args.contains("-f"))
        {
            sender.sendMessage(MESSAGE_COLOR + "The base temperature of this biome is " + VALUE_COLOR + biome.getBiomeConfig().biomeTemperature + MESSAGE_COLOR + ", \nat your height it is " + VALUE_COLOR
                    + biome.getTemperatureAt(x, y, z));
        }

        if (args.contains("-s"))
        {
            try
            {
                String savedBiomeName = world.getSavedBiomeName(x, z);
                sender.sendMessage(MESSAGE_COLOR + "According to the world save files, you are in the " + VALUE_COLOR
                        + savedBiomeName + MESSAGE_COLOR + " biome, with id " + VALUE_COLOR
                        + biomeIds.getSavedId());
            } catch (BiomeNotFoundException e)
            {
                sender.sendMessage(ERROR_COLOR + "An unknown biome (" + e.getBiomeName() + ") was saved to the save files here.");
            }
        }

        return true;
    }
}