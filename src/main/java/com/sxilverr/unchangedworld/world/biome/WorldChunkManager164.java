package com.sxilverr.unchangedworld.world.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import com.sxilverr.unchangedworld.world.Settings164;
import com.sxilverr.unchangedworld.world.gen.layer.GenLayer164;

public class WorldChunkManager164 extends WorldChunkManager {

    private final GenLayer genBiomes;
    private final GenLayer biomeIndexLayer;
    private final BiomeCache biomeCache;
    private final List<BiomeGenBase> biomesToSpawnIn;

    public WorldChunkManager164(World world, Settings164 settings) {
        this(world.getSeed(), settings);
    }

    public WorldChunkManager164(long seed, Settings164 settings) {
        super();
        GenLayer[] layers = GenLayer164.initializeAllBiomeGenerators(seed, settings);
        this.genBiomes = layers[0];
        this.biomeIndexLayer = layers[1];
        this.biomeCache = new BiomeCache(this);
        this.biomesToSpawnIn = new ArrayList<BiomeGenBase>(
            Arrays.asList(
                BiomeGenBase.forest,
                BiomeGenBase.plains,
                BiomeGenBase.taiga,
                BiomeGenBase.taigaHills,
                BiomeGenBase.forestHills,
                BiomeGenBase.jungle,
                BiomeGenBase.jungleHills));

        if (settings.moddedBiomes) {
            this.biomesToSpawnIn.addAll(ModdedBiomes164.among(WorldChunkManager.allowedBiomes));
        }
    }

    @Override
    public List<BiomeGenBase> getBiomesToSpawnIn() {
        return this.biomesToSpawnIn;
    }

    @Override
    public BiomeGenBase getBiomeGenAt(int x, int z) {
        return this.biomeCache.getBiomeGenAt(x, z);
    }

    @Override
    public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new float[width * length];
        }

        int[] ints = this.biomeIndexLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; ++i) {
            float rainfall = (float) BiomeGenBase.getBiome(ints[i])
                .getIntRainfall() / 65536.0F;

            if (rainfall > 1.0F) {
                rainfall = 1.0F;
            }

            listToReuse[i] = rainfall;
        }

        return listToReuse;
    }

    @Override
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] listToReuse, int x, int z, int width, int length) {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new BiomeGenBase[width * length];
        }

        int[] ints = this.genBiomes.getInts(x, z, width, length);

        for (int i = 0; i < width * length; ++i) {
            listToReuse[i] = BiomeGenBase.getBiome(ints[i]);
        }

        return listToReuse;
    }

    @Override
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] listToReuse, int x, int z, int width, int length) {
        return this.getBiomeGenAt(listToReuse, x, z, width, length, true);
    }

    @Override
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length,
        boolean useCache) {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new BiomeGenBase[width * length];
        }

        if (useCache && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
            BiomeGenBase[] cached = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(cached, 0, listToReuse, 0, width * length);
            return listToReuse;
        }

        int[] ints = this.biomeIndexLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; ++i) {
            listToReuse[i] = BiomeGenBase.getBiome(ints[i]);
        }

        return listToReuse;
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<BiomeGenBase> allowed) {
        IntCache.resetIntCache();
        int minX = x - radius >> 2;
        int minZ = z - radius >> 2;
        int maxX = x + radius >> 2;
        int maxZ = z + radius >> 2;
        int width = maxX - minX + 1;
        int length = maxZ - minZ + 1;
        int[] ints = this.genBiomes.getInts(minX, minZ, width, length);

        for (int i = 0; i < width * length; ++i) {
            BiomeGenBase biome = BiomeGenBase.getBiome(ints[i]);

            if (!allowed.contains(biome)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ChunkPosition findBiomePosition(int x, int z, int radius, List<BiomeGenBase> allowed, Random random) {
        IntCache.resetIntCache();
        int minX = x - radius >> 2;
        int minZ = z - radius >> 2;
        int maxX = x + radius >> 2;
        int maxZ = z + radius >> 2;
        int width = maxX - minX + 1;
        int length = maxZ - minZ + 1;
        int[] ints = this.genBiomes.getInts(minX, minZ, width, length);
        ChunkPosition position = null;
        int found = 0;

        for (int i = 0; i < width * length; ++i) {
            int blockX = minX + i % width << 2;
            int blockZ = minZ + i / width << 2;
            BiomeGenBase biome = BiomeGenBase.getBiome(ints[i]);

            if (allowed.contains(biome) && (position == null || random.nextInt(found + 1) == 0)) {
                position = new ChunkPosition(blockX, 0, blockZ);
                ++found;
            }
        }

        return position;
    }

    @Override
    public void cleanupCache() {
        this.biomeCache.cleanupCache();
    }
}
