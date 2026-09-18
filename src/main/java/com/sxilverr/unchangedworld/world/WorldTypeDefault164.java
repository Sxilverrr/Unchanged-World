package com.sxilverr.unchangedworld.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

import com.sxilverr.unchangedworld.world.biome.WorldChunkManager164;
import com.sxilverr.unchangedworld.world.gen.ChunkProvider164;

public class WorldTypeDefault164 extends WorldType {

    public static final String NAME = "default_1_6_4";

    private static boolean current;

    public WorldTypeDefault164() {
        super(NAME);
    }

    public static boolean isCurrent() {
        return current;
    }

    public static void setCurrent(boolean value) {
        current = value;
    }

    @Override
    public WorldChunkManager getChunkManager(World world) {
        return new WorldChunkManager164(world);
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
        return new ChunkProvider164(
            world,
            world.getSeed(),
            world.getWorldInfo()
                .isMapFeaturesEnabled());
    }
}
