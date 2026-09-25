package com.sxilverr.unchangedworld.world.biome;

import java.util.Random;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.sxilverr.unchangedworld.world.Settings164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenShrub164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTaiga2164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTrees164;

public class BiomeDecoratorIntegrated164 extends BiomeDecorator164 {

    private static final NoiseGeneratorPerlin PLANT_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
    private static final double FLOWER_FIELD_THRESHOLD = -0.8D;
    private static final int LILAC = 1;
    private static final int DOUBLE_GRASS = 2;
    private static final int LARGE_FERN = 3;
    private static final int ROSE_BUSH = 4;
    private static final int PEONY = 5;

    private final Settings164 options;
    private final WorldGenFlowers flowerGen = new WorldGenFlowers(Blocks.yellow_flower);
    private final WorldGenDoublePlant doublePlantGen = new WorldGenDoublePlant();
    private final WorldGenerator gravelGen = new WorldGenSand(Blocks.gravel, 6);
    private final WorldGenerator melonGen = new WorldGenMelon();
    private boolean flowerField;

    public BiomeDecoratorIntegrated164(Settings164 options) {
        this.options = options;
    }

    @Override
    protected void decorateBefore() {
        this.flowerField = false;

        switch (this.settings.kind) {
            case PLAINS:
                if (this.options.flowers1710) {
                    double noise = PLANT_NOISE.func_151601_a((this.chunkX + 8) / 200.0D, (this.chunkZ + 8) / 200.0D);
                    this.flowerField = noise < FLOWER_FIELD_THRESHOLD;
                }

                if (this.options.plainsDoubleGrass && !this.flowerField) {
                    this.doublePlants(DOUBLE_GRASS, 7);
                }

                break;
            case FOREST:
                if (!this.options.forestDoublePlants) {
                    break;
                }

                int patches = this.rand.nextInt(5) - 3;

                for (int i = 0; i < patches; ++i) {
                    int type = this.rand.nextInt(3);
                    this.doublePlantGen.func_150548_a(type == 0 ? LILAC : (type == 1 ? ROSE_BUSH : PEONY));

                    for (int attempt = 0; attempt < 5 && !this.doublePlant(); ++attempt) {
                        ;
                    }
                }

                break;
            case TAIGA:
                if (this.options.taigaLargeFerns) {
                    this.doublePlants(LARGE_FERN, 7);
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void decorateAfter() {
        if (this.settings.kind == Decoration164.Kind.JUNGLE && this.options.jungleMelons) {
            int x = this.chunkX + this.rand.nextInt(16) + 8;
            int z = this.chunkZ + this.rand.nextInt(16) + 8;
            int y = this.nextInt(this.world.getHeightValue(x, z) * 2);
            this.melonGen.generate(this.world, this.rand, x, y, z);
        }
    }

    @Override
    protected int treesPerChunk() {
        if (this.options.hillsTrees1710 && this.biome == BiomeGenBase.extremeHillsEdge) {
            return 3;
        }

        return super.treesPerChunk();
    }

    @Override
    protected int sandPerChunk() {
        return this.swampWithoutPatches() ? 0 : super.sandPerChunk();
    }

    @Override
    protected int sandPerChunk2() {
        return this.swampWithoutPatches() ? 0 : super.sandPerChunk2();
    }

    @Override
    protected int flowersPerChunk() {
        switch (this.settings.kind) {
            case PLAINS:
                return this.flowerField ? 15 : super.flowersPerChunk();
            case SWAMP:
                return this.options.flowers1710 ? 1 : super.flowersPerChunk();
            default:
                return super.flowersPerChunk();
        }
    }

    @Override
    protected int grassPerChunk() {
        switch (this.settings.kind) {
            case PLAINS:
                return this.flowerField ? 5 : super.grassPerChunk();
            case SWAMP:
                return this.options.swampGrass ? 5 : super.grassPerChunk();
            default:
                return super.grassPerChunk();
        }
    }

    @Override
    protected int mushroomsPerChunk() {
        if (this.settings.kind == Decoration164.Kind.TAIGA && this.options.taigaMushrooms) {
            return 1;
        }

        return super.mushroomsPerChunk();
    }

    @Override
    protected WorldGenerator secondSandGen() {
        return this.options.gravelPatches ? this.gravelGen : super.secondSandGen();
    }

    @Override
    protected boolean jungleVines() {
        return this.options.jungleVines;
    }

    @Override
    protected WorldGenerator getRandomWorldGenForTrees() {
        switch (this.settings.kind) {
            case FOREST:
                if (this.options.forestTrees1710) {
                    return this.rand.nextInt(5) != 0 ? this.treesGen : this.forestGen;
                }

                break;
            case HILLS:
                if (this.options.hillsTrees1710 && this.rand.nextInt(3) > 0) {
                    return new WorldGenTaiga2164();
                }

                break;
            case SNOW:
                if (this.options.snowTrees1710) {
                    return new WorldGenTaiga2164();
                }

                break;
            case JUNGLE:
                if (this.options.jungleTrees1710) {
                    return this.rand.nextInt(10) == 0 ? this.bigTree()
                        : (this.rand.nextInt(2) == 0 ? new WorldGenShrub164(3, 0)
                            : (this.rand.nextInt(3) == 0 ? new WorldGenMegaJungle(false, 10, 20, 3, 3)
                                : new WorldGenTrees164(false, 4 + this.rand.nextInt(7), 3, 3, true)));
                }

                break;
            default:
                break;
        }

        return super.getRandomWorldGenForTrees();
    }

    @Override
    protected void decorateFlowers() {
        if (!this.options.flowers1710) {
            super.decorateFlowers();
            return;
        }

        for (int count = 0, flowers = this.flowersPerChunk(); count < flowers; ++count) {
            int x = this.chunkX + this.rand.nextInt(16) + 8;
            int z = this.chunkZ + this.rand.nextInt(16) + 8;
            int y = this.nextInt(this.world.getHeightValue(x, z) + 32);
            String name = this.biome.func_150572_a(this.rand, x, y, z);
            BlockFlower flower = BlockFlower.func_149857_e(name);

            if (flower.getMaterial() != Material.air) {
                this.flowerGen.func_150550_a(flower, BlockFlower.func_149856_f(name));
                this.flowerGen.generate(this.world, this.rand, x, y, z);
            }
        }
    }

    @Override
    protected void decorateGrass() {
        for (int count = 0, grass = this.grassPerChunk(); count < grass; ++count) {
            int x = this.chunkX + this.rand.nextInt(16) + 8;
            int z = this.chunkZ + this.rand.nextInt(16) + 8;
            int y = this.nextInt(this.world.getHeightValue(x, z) * 2);
            this.grassGen()
                .generate(this.world, this.rand, x, y, z);
        }
    }

    private WorldGenerator grassGen() {
        if (this.settings.kind == Decoration164.Kind.TAIGA && !this.options.taigaFerns) {
            return new WorldGenTallGrass(Blocks.tallgrass, 1);
        }

        return this.biome.getRandomWorldGenForGrass(this.rand);
    }

    private boolean swampWithoutPatches() {
        return this.settings.kind == Decoration164.Kind.SWAMP && this.options.swampNoPatches;
    }

    private void doublePlants(int type, int count) {
        this.doublePlantGen.func_150548_a(type);

        for (int i = 0; i < count; ++i) {
            this.doublePlant();
        }
    }

    private boolean doublePlant() {
        int x = this.chunkX + this.rand.nextInt(16) + 8;
        int z = this.chunkZ + this.rand.nextInt(16) + 8;
        int y = this.rand.nextInt(this.world.getHeightValue(x, z) + 32);
        return this.doublePlantGen.generate(this.world, this.rand, x, y, z);
    }

    private int nextInt(int bound) {
        return bound <= 1 ? 0 : this.rand.nextInt(bound);
    }
}
