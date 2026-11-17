package com.sxilverr.unchangedworld.world.biome;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public final class Biome164 {

    private static final Biome164[] TABLE = new Biome164[BiomeGenBase.getBiomeGenArray().length];
    private static final Biome164[] DERIVED = new Biome164[TABLE.length];
    private static final Biome164[][] HEIGHTS = { TABLE, null, null };
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

        for (int version = 1; version < HEIGHTS.length; ++version) {
            HEIGHTS[version] = TABLE.clone();
            heights(version, BiomeGenBase.desertHills, 0.2F, 0.7F);
            heights(version, BiomeGenBase.forestHills, 0.2F, 0.6F);
            heights(version, BiomeGenBase.taigaHills, 0.2F, 0.7F);
            heights(version, BiomeGenBase.jungleHills, 1.8F, 0.2F);
        }

        heights(1, BiomeGenBase.extremeHills, 0.2F, 1.3F);
        heights(1, BiomeGenBase.iceMountains, 0.2F, 1.2F);
        heights(2, BiomeGenBase.ocean, -1.0F, 0.5F);
        heights(2, BiomeGenBase.extremeHills, 0.2F, 1.8F);
        heights(2, BiomeGenBase.iceMountains, 0.2F, 1.8F);
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

    private static void heights(int version, BiomeGenBase biome, float minHeight, float maxHeight) {
        Biome164 base = TABLE[biome.biomeID];
        HEIGHTS[version][biome.biomeID] = new Biome164(
            minHeight,
            maxHeight,
            base.temperature,
            base.topBlock,
            base.fillerBlock,
            base.enableSnow);
    }

    public static Biome164 get(BiomeGenBase biome, int heights) {
        Biome164 values = biome == null ? null : HEIGHTS[heights][biome.biomeID];
        return values != null ? values : get(biome);
    }

    public static Biome164 find(BiomeGenBase biome) {
        return biome == null ? null : TABLE[biome.biomeID];
    }

    public static Biome164 get(BiomeGenBase biome) {
        if (biome == null) {
            return DEFAULT;
        }

        if (TABLE[biome.biomeID] != null) {
            return TABLE[biome.biomeID];
        }

        if (DERIVED[biome.biomeID] == null) {
            DERIVED[biome.biomeID] = new Biome164(
                biome.rootHeight,
                biome.heightVariation * 2.0F + 0.1F / 0.9F,
                biome.temperature,
                biome.topBlock,
                biome.fillerBlock,
                biome.getEnableSnow());
        }

        return DERIVED[biome.biomeID];
    }
}
