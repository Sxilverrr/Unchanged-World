package com.sxilverr.unchangedworld.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

import com.sxilverr.unchangedworld.world.biome.BiomeDecorator164;
import com.sxilverr.unchangedworld.world.biome.WorldChunkManager164;
import com.sxilverr.unchangedworld.world.gen.ChunkProvider164;

public class WorldTypeDefault164 extends WorldType {

    public static final String NAME = "default_1_6_4";

    private static boolean current;

    public WorldTypeDefault164() {
        this(NAME);
    }

    protected WorldTypeDefault164(String name) {
        super(name);
    }

    public static boolean isCurrent() {
        return current;
    }

    public static void setCurrent(boolean value) {
        current = value;
    }

    public static boolean isClimate164(WorldType type) {
        return type instanceof WorldTypeDefault164 && ((WorldTypeDefault164) type).climate164();
    }

    public static boolean isLighting164(WorldType type) {
        return type instanceof WorldTypeDefault164 && ((WorldTypeDefault164) type).lighting164();
    }

    public boolean climate164() {
        return true;
    }

    public boolean lighting164() {
        return true;
    }

    @Override
    public WorldChunkManager getChunkManager(World world) {
        return new WorldChunkManager164(world, this.settings());
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
        return new ChunkProvider164(
            world,
            world.getSeed(),
            world.getWorldInfo()
                .isMapFeaturesEnabled(),
            this.settings(),
            this.createDecorator());
    }

    protected Settings164 settings() {
        return new Settings164();
    }

    protected BiomeDecorator164 createDecorator() {
        return new BiomeDecorator164();
    }
}
