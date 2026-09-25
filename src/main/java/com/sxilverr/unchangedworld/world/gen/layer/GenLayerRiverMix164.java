package com.sxilverr.unchangedworld.world.gen.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import com.sxilverr.unchangedworld.world.biome.BiomeIds164;
import com.sxilverr.unchangedworld.world.biome.BiomesOPlenty164;
import com.sxilverr.unchangedworld.world.biome.ModdedBiomes164;

public class GenLayerRiverMix164 extends GenLayer {

    private final GenLayer biomePatternGeneratorChain;
    private final GenLayer riverPatternGeneratorChain;
    private final int[] moddedRivers = new int[BiomeIds164.count()];

    public GenLayerRiverMix164(long seed, GenLayer biomeChain, GenLayer riverChain, boolean moddedBiomes) {
        super(seed);
        this.biomePatternGeneratorChain = biomeChain;
        this.riverPatternGeneratorChain = riverChain;
        Arrays.fill(this.moddedRivers, -1);

        List<BiomeGenBase> generating = moddedBiomes ? BiomesOPlenty164.generating() : new ArrayList<BiomeGenBase>();

        for (BiomeGenBase biome : generating) {
            if (!ModdedBiomes164.isModded(biome)) {
                continue;
            }

            BiomeGenBase river = BiomesOPlenty164.riverBiome(biome.biomeID);

            if (biome.getEnableSnow()) {
                this.moddedRivers[biome.biomeID] = BiomeGenBase.frozenRiver.biomeID;
            } else if (river != null) {
                this.moddedRivers[biome.biomeID] = river.biomeID;
            } else {
                this.moddedRivers[biome.biomeID] = BiomeGenBase.river.biomeID;
            }
        }
    }

    @Override
    public void initWorldGenSeed(long seed) {
        this.biomePatternGeneratorChain.initWorldGenSeed(seed);
        this.riverPatternGeneratorChain.initWorldGenSeed(seed);
        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] biomes = this.biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] rivers = this.riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaWidth * areaHeight; ++i) {
            if (biomes[i] == BiomeGenBase.ocean.biomeID) {
                out[i] = biomes[i];
            } else if (rivers[i] >= 0) {
                int modded = biomes[i] >= 0 && biomes[i] < this.moddedRivers.length ? this.moddedRivers[biomes[i]] : -1;

                if (modded >= 0) {
                    out[i] = modded;
                } else if (biomes[i] == BiomeGenBase.icePlains.biomeID) {
                    out[i] = BiomeGenBase.frozenRiver.biomeID;
                } else if (biomes[i] != BiomeGenBase.mushroomIsland.biomeID
                    && biomes[i] != BiomeGenBase.mushroomIslandShore.biomeID) {
                        out[i] = rivers[i];
                    } else {
                        out[i] = BiomeGenBase.mushroomIslandShore.biomeID;
                    }
            } else {
                out[i] = biomes[i];
            }
        }

        return out;
    }
}
