package com.sxilverr.unchangedworld.world.biome;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public final class Biome164 {

    private static final Biome164[] TABLE = new Biome164[256];
    private static final Biome164 DEFAULT = new Biome164(0.1F, 0.3F, 0.5F, Blocks.grass, Blocks.dirt, false);

    static {
        put(BiomeGenBase.ocean, -1.0F, 0.4F, 0.5F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.plains, 0.1F, 0.3F, 0.8F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.desert, 0.1F, 0.2F, 2.0F, Blocks.sand, Blocks.sand, false);
        put(BiomeGenBase.extremeHills, 0.3F, 1.5F, 0.2F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.forest, 0.1F, 0.3F, 0.7F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.taiga, 0.1F, 0.4F, 0.05F, Blocks.grass, Blocks.dirt, true);
        put(BiomeGenBase.swampland, -0.2F, 0.1F, 0.8F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.river, -0.5F, 0.0F, 0.5F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.hell, 0.1F, 0.3F, 2.0F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.sky, 0.1F, 0.3F, 0.5F, Blocks.dirt, Blocks.dirt, false);
        put(BiomeGenBase.frozenOcean, -1.0F, 0.5F, 0.0F, Blocks.grass, Blocks.dirt, true);
        put(BiomeGenBase.frozenRiver, -0.5F, 0.0F, 0.0F, Blocks.grass, Blocks.dirt, true);
        put(BiomeGenBase.icePlains, 0.1F, 0.3F, 0.0F, Blocks.grass, Blocks.dirt, true);
        put(BiomeGenBase.iceMountains, 0.3F, 1.3F, 0.0F, Blocks.grass, Blocks.dirt, true);
        put(BiomeGenBase.mushroomIsland, 0.2F, 1.0F, 0.9F, Blocks.mycelium, Blocks.dirt, false);
        put(BiomeGenBase.mushroomIslandShore, -1.0F, 0.1F, 0.9F, Blocks.mycelium, Blocks.dirt, false);
        put(BiomeGenBase.beach, 0.0F, 0.1F, 0.8F, Blocks.sand, Blocks.sand, false);
        put(BiomeGenBase.desertHills, 0.3F, 0.8F, 2.0F, Blocks.sand, Blocks.sand, false);
        put(BiomeGenBase.forestHills, 0.3F, 0.7F, 0.7F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.taigaHills, 0.3F, 0.8F, 0.05F, Blocks.grass, Blocks.dirt, true);
        put(BiomeGenBase.extremeHillsEdge, 0.2F, 0.8F, 0.2F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.jungle, 0.2F, 0.4F, 1.2F, Blocks.grass, Blocks.dirt, false);
        put(BiomeGenBase.jungleHills, 1.8F, 0.5F, 1.2F, Blocks.grass, Blocks.dirt, false);
    }

    public final float minHeight;
    public final float maxHeight;
    public final float temperature;
    public final Block topBlock;
    public final Block fillerBlock;
    public final boolean enableSnow;

    private Biome164(float minHeight, float maxHeight, float temperature, Block topBlock, Block fillerBlock,
        boolean enableSnow) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.temperature = temperature;
        this.topBlock = topBlock;
        this.fillerBlock = fillerBlock;
        this.enableSnow = enableSnow;
    }

    private static void put(BiomeGenBase biome, float minHeight, float maxHeight, float temperature, Block topBlock,
        Block fillerBlock, boolean enableSnow) {
        TABLE[biome.biomeID] = new Biome164(minHeight, maxHeight, temperature, topBlock, fillerBlock, enableSnow);
    }

    public static Biome164 find(BiomeGenBase biome) {
        return biome == null ? null : TABLE[biome.biomeID];
    }

    public static Biome164 get(BiomeGenBase biome) {
        Biome164 values = biome == null ? null : TABLE[biome.biomeID];
        return values == null ? DEFAULT : values;
    }
}
