package com.pg85.otg.logging;

import com.pg85.otg.util.helpers.StringHelper;

import java.util.List;

public abstract class Logger
{

    protected LogMarker minimumLevel = LogMarker.INFO;

    public void setLevel(LogMarker level)
    {
        minimumLevel = level;
    }

    /**
     * Logs the message(s) with the given importance. Message will be prefixed
     * with [OpenTerrainGenerator], so don't do that yourself.
     *
     * @param level   The severity of the message
     * @param message The messages to log
     */
    public void log(LogMarker level, List<String> message)
    {
        log(level, "{}", (Object) StringHelper.join(message, " "));
    }

    /**
     * Logs a format string message with the given importance. Message will be
     * prefixed with [TerrainControl], so don't do that yourself.
     *
     * @param level   The severity of the message
     * @param message The messages to log formatted similar to Logger.log() with
     *                the same args.
     * @param params  The parameters belonging to {0...} in the message string
     */
    public abstract void log(LogMarker level, String message, Object... params);
}
