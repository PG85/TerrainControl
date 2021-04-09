package com.pg85.otg.forge.commands;

import com.pg85.otg.OTG;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.customobject.CustomObject;
import com.pg85.otg.customobject.structures.CustomStructureCache;
import com.pg85.otg.forge.gen.ForgeWorldGenRegion;
import com.pg85.otg.forge.gen.OTGNoiseChunkGenerator;
import com.pg85.otg.logging.LogMarker;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.bo3.Rotation;
import com.pg85.otg.util.gen.LocalWorldGenRegion;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.nio.file.Path;
import java.util.Random;

public class SpawnCommand
{
	public static int execute(CommandSource source, String presetName, String objectName, BlockPos blockPos)
	{
		try
		{
			CustomObject objectToSpawn = getObject(objectName, presetName);

			if (objectToSpawn == null)
			{
				source.sendFeedback(new StringTextComponent("Could not find an object by the name " + objectName + " in either " + presetName + " or Global objects"), false);
				return 0;
			}

			Preset preset;
			if (presetName.equalsIgnoreCase("global"))
			{
				preset = OTG.getEngine().getPresetLoader().getPresetByName(OTG.getEngine().getPresetLoader().getDefaultPresetName());
			}
			else {
				preset = OTG.getEngine().getPresetLoader().getPresetByName(presetName);
				if (preset == null)
				{
					source.sendFeedback(new StringTextComponent("Could not find preset "+presetName), false);
					return 0;
				}
			}

			LocalWorldGenRegion region = new ForgeWorldGenRegion(preset.getName(), preset.getWorldConfig(), source.getWorld(),
				source.getWorld().getChunkProvider().getChunkGenerator());

			/*
			if (objectToSpawn instanceof BO3 && ((BO3)objectToSpawn).getBranches(Rotation.NORTH).length != 0)
			{
				// This is a structure, spawn it as such
				BO3CustomStructureCoordinate coord = (BO3CustomStructureCoordinate) ((BO3) objectToSpawn).makeCustomStructureCoordinate(presetName, new Random(), blockPos.getX(), blockPos.getZ());
				BO3CustomStructure structureToSpawn = new BO3CustomStructure(region, coord, OTG.getEngine().getOTGRootFolder(),
					OTG.getEngine().getPluginConfig().getSpawnLogEnabled(), OTG.getEngine().getLogger(), OTG.getEngine().getCustomObjectManager(),
					OTG.getEngine().getMaterialReader(),OTG.getEngine().getCustomObjectResourcesManager(), OTG.getEngine().getModLoadedChecker());

				structureToSpawn.spawnInChunk(((OTGNoiseChunkGenerator) source.getWorld().getChunkProvider().getChunkGenerator()).getStructureCache(),
					region, ChunkCoordinate.fromBlockCoords(blockPos.getX(), blockPos.getZ()),
					OTG.getEngine().getOTGRootFolder(),
					OTG.getEngine().getPluginConfig().getSpawnLogEnabled(), OTG.getEngine().getLogger(), OTG.getEngine().getCustomObjectManager(),
					OTG.getEngine().getMaterialReader(),OTG.getEngine().getCustomObjectResourcesManager(), OTG.getEngine().getModLoadedChecker());
				source.sendFeedback(new StringTextComponent("Spawned structure " + objectName + " at " + blockPos.toString()), false);
				source.sendFeedback(new StringTextComponent("If pieces are missing, that likely means you're using blockchecks. Try to avoid those."), false);
				return 0;
			}*/

			CustomStructureCache cache =  source.getWorld().getChunkProvider().getChunkGenerator() instanceof OTGNoiseChunkGenerator ?
										  ((OTGNoiseChunkGenerator) source.getWorld().getChunkProvider().getChunkGenerator()).getStructureCache() :
										  null;

			// Cache is only null in non-OTG worlds
			if (cache == null && objectToSpawn.doReplaceBlocks())
			{
				source.sendFeedback(new StringTextComponent("Cannot spawn objects with DoReplaceBlocks in on-OTG worlds"), false);
				return 0;
			}

			if (objectToSpawn.spawnForced(
				cache,
				region,
				new Random(),
				Rotation.NORTH,
				blockPos.getX(),
				blockPos.getY(),
				blockPos.getZ()
			))
			{
				source.sendFeedback(new StringTextComponent("Spawned object " + objectName + " at " + blockPos.toString()), false);
			}
			else
			{
				source.sendFeedback(new StringTextComponent("Failed to spawn object " + objectName + ". Is it a BO4?"), false);
			}
		}
		catch (Exception e)
		{
			OTG.log(LogMarker.INFO, e.toString());
			for (StackTraceElement s :
				e.getStackTrace())
			{
				OTG.log(LogMarker.INFO, s.toString());
			}
		}

		return 0;
	}

	public static CustomObject getObject(String objectName, String presetName)
	{
		return OTG.getEngine().getCustomObjectManager().getGlobalObjects().getObjectByName(
			objectName,
			presetName,
			OTG.getEngine().getOTGRootFolder(),
			false,
			OTG.getEngine().getLogger(),
			OTG.getEngine().getCustomObjectManager(),
			OTG.getEngine().getMaterialReader(),
			OTG.getEngine().getCustomObjectResourcesManager(),
			OTG.getEngine().getModLoadedChecker());
	}
}
