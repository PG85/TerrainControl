package com.pg85.otg.customobjects.structures;

import com.pg85.otg.config.customobjects.CustomObjectResourcesManager;
import com.pg85.otg.customobjects.CustomObjectManager;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.bo3.Rotation;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IModLoadedChecker;
import com.pg85.otg.util.interfaces.IPresetNameProvider;

import java.nio.file.Path;
import java.util.Random;

/**
 * Represents a branch of a CustomObject.
 *
 */
public interface Branch
{
    /**
     * Makes a CustomObjectCoordinate out of this branch. Is allowed
     * to return null if based on the random number generator no
     * branch should spawn here.
     * 
     * @param world  The world.
     * @param random The random number generator.
     * @param rotation Rotation of the origin of the object.
     * @param x      X coordinate of the origin of the object.
     * @param y      Y coordinate of the origin of the object.
     * @param z      Z coordinate of the origin of the object.
     * @return The CustomObjectCoordinate of this branch.
     */
    public CustomStructureCoordinate toCustomObjectCoordinate(String worldName, Random random, Rotation rotation, int x, int y, int z, String startBO3Name, Path otgRootFolder, boolean spawnLog, ILogger logger, CustomObjectManager customObjectManager, IPresetNameProvider presetNameProvider, IMaterialReader materialReader, CustomObjectResourcesManager manager, IModLoadedChecker modLoadedChecker);
}