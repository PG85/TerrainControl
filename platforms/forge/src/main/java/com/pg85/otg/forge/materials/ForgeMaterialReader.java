package com.pg85.otg.forge.materials;

import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.FifoMap;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterialTag;

public class ForgeMaterialReader implements IMaterialReader
{
	// TODO: Smaller caches should be ok, only most frequently used should be cached?
	private final FifoMap<String, LocalMaterialData> cachedMaterials = new FifoMap<>(4096);
	private final FifoMap<String, LocalMaterialTag> cachedTags = new FifoMap<>(4096);	
	
	@Override
	public LocalMaterialData readMaterial(String material) throws InvalidConfigException
	{
		if(material == null)
		{
			return null;
		}
		
		LocalMaterialData localMaterial = this.cachedMaterials.get(material);
		if(localMaterial != null)
		{
			return localMaterial;
		}
		else if(this.cachedMaterials.containsKey(material))
		{
			throw new InvalidConfigException("Cannot read block: " + material);
		}

		try
		{
			localMaterial = ForgeMaterialData.ofString(material);
		}
		catch(InvalidConfigException ex)
		{
			// Happens when a non existing block name is used.
			String breakpoint = "";
		}

		this.cachedMaterials.put(material, localMaterial);
		
		return localMaterial;
	}
	
	@Override
	public LocalMaterialTag readTag(String tag) throws InvalidConfigException
	{
		if(tag == null)
		{
			return null;
		}
		
		LocalMaterialTag localTag = this.cachedTags.get(tag);
		if(localTag != null)
		{
			return localTag;
		}

		localTag = ForgeMaterialTag.ofString(tag);
		this.cachedTags.put(tag, localTag);
		return localTag;
	}	
}
