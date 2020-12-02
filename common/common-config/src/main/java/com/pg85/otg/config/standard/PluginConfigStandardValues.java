package com.pg85.otg.config.standard;

import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.logging.Logger.LogLevels;

public class PluginConfigStandardValues extends Settings
{
    // Plugin Defaults
    
    public static final Setting<LogLevels> LogLevel = enumSetting("LogLevel", LogLevels.Standard);
    public static final Setting<Boolean> SPAWN_LOG = booleanSetting("SpawnLog", false);
    public static final Setting<Boolean> DEVELOPER_MODE = booleanSetting("DeveloperMode", false);
    public static final Setting<Integer> PREGENERATOR_MAX_CHUNKS_PER_TICK = intSetting("PregeneratorMaxChunksPerTick", 5, 1, 10);
}