package com.pg85.otg.customobjects.structures;

import java.util.Arrays;

public class PlottedChunksRegion
{
	private boolean requiresSave = false;
	private boolean[][] plottedChunks = new boolean[CustomStructureCache.REGION_SIZE][CustomStructureCache.REGION_SIZE];

	public PlottedChunksRegion() { }

	public PlottedChunksRegion(boolean[][] plottedChunks)
	{
		this.plottedChunks = plottedChunks;
	}

	public boolean requiresSave()
	{
		return this.requiresSave;
	}

	public void markSaved()
	{
		this.requiresSave = false;
	}

	public boolean getChunk(int internalX, int internalZ)
	{
		return this.plottedChunks[internalX][internalZ];
	}

	public void setChunk(int internalX, int internalZ)
	{
		this.plottedChunks[internalX][internalZ] = true;
		this.requiresSave = true;
	}

	public boolean[][] getArray()
	{
		return this.plottedChunks;
	}

	public static PlottedChunksRegion getFilledRegion()
	{
		boolean[][] plottedChunks = new boolean[CustomStructureCache.REGION_SIZE][CustomStructureCache.REGION_SIZE];
		for(int i = 0; i < CustomStructureCache.REGION_SIZE; i++)
		{
			Arrays.fill(plottedChunks[i], true);
		}
		return new PlottedChunksRegion(plottedChunks);
	}
}
