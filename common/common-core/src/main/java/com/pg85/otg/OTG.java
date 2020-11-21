package com.pg85.otg;

import com.pg85.otg.logging.LogMarker;
import java.util.List;

public class OTG
{		
    private static OTGEngine Engine;
	
    private OTG() { }
	
    // Engine
    	
    public static OTGEngine getEngine()
    {
        return Engine;
    }
    
    public static void startEngine(OTGEngine engine)
    {
        if (OTG.Engine != null)
        {
            throw new IllegalStateException("Engine is already set.");
        }

        OTG.Engine = engine;
        engine.onStart();
    }

    public static void stopEngine()
    {
        Engine.onShutdown();
        Engine = null;
    }
    
    // Logging
    
    public static void log(LogMarker level, List<String> messages)
    {
        Engine.getLogger().log(level, messages);
    }

    public static void log(LogMarker level, String message, Object... params)
    {
        Engine.getLogger().log(level, message, params);
    }

    public static void printStackTrace(LogMarker level, Throwable e)
    {
        printStackTrace(level, e, Integer.MAX_VALUE);
    }

    public static void printStackTrace(LogMarker level, Throwable e, int maxDepth)
    {
        Engine.getLogger().log(level, e, maxDepth);
    }
}