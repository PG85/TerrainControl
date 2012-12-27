package com.khorn.terraincontrol.forge.structuregens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentVillageRoadPiece;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;

class StructureVillageStart extends StructureStart
{
    /** well ... thats what it does */
    private boolean hasMoreThanTwoComponents = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public StructureVillageStart(World world, Random random, int chunkX, int chunkZ, int size)
    {      
        ArrayList var6 = StructureVillagePieces.getStructureVillageWeightedPieceList(random, size);
        ComponentVillageStartPiece var7 = new ComponentVillageStartPiece(world.getWorldChunkManager(), 0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2, var6, size);
        this.components.add(var7);
        var7.buildComponent(var7, this.components, random);
        ArrayList var8 = var7.field_74930_j;
        ArrayList var9 = var7.field_74932_i;
        int var10;

        while (!var8.isEmpty() || !var9.isEmpty())
        {
            StructureComponent var11;

            if (var8.isEmpty())
            {
                var10 = random.nextInt(var9.size());
                var11 = (StructureComponent)var9.remove(var10);
                var11.buildComponent(var7, this.components, random);
            }
            else
            {
                var10 = random.nextInt(var8.size());
                var11 = (StructureComponent)var8.remove(var10);
                var11.buildComponent(var7, this.components, random);
            }
        }

        this.updateBoundingBox();
        var10 = 0;
        Iterator var13 = this.components.iterator();

        while (var13.hasNext())
        {
            StructureComponent var12 = (StructureComponent)var13.next();

            if (!(var12 instanceof ComponentVillageRoadPiece))
            {
                ++var10;
            }
        }

        this.hasMoreThanTwoComponents = var10 > 2;
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return this.hasMoreThanTwoComponents;
    }
}
