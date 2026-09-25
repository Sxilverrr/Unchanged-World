package com.sxilverr.unchangedworld.world;

import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.sxilverr.unchangedworld.world.biome.BiomeDecorator164;
import com.sxilverr.unchangedworld.world.biome.BiomeDecoratorIntegrated164;

public class WorldTypeIntegrated164 extends WorldTypeDefault164 {

    public static final String NAME = "integrated_1_6_4";

    public WorldTypeIntegrated164() {
        super(NAME);
    }

    @Override
    public boolean climate164() {
        return !this.settings().climate1710;
    }

    @Override
    public boolean lighting164() {
        return !this.settings().lighting1710;
    }

    @Override
    public WorldChunkManager getChunkManager(World world) {
        return this.settings().layout1710 ? new WorldChunkManager(world) : super.getChunkManager(world);
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
        if (this.settings().terrain1710) {
            return new ChunkProviderGenerate(
                world,
                world.getSeed(),
                world.getWorldInfo()
                    .isMapFeaturesEnabled());
        }

        return super.getChunkGenerator(world, generatorOptions);
    }

    @Override
    protected Settings164 settings() {
        return IntegratedConfig164.settings();
    }

    @Override
    protected BiomeDecorator164 createDecorator() {
        return new BiomeDecoratorIntegrated164(this.settings());
    }
}
