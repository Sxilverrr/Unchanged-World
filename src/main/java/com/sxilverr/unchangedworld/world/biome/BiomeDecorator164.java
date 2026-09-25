package com.sxilverr.unchangedworld.world.biome;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDesertWells;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import com.sxilverr.unchangedworld.world.gen.feature.WorldGenBigTree164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenDeadBush164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenFlowers164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenForest164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenHugeTrees164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenShrub164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenSwamp164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTaiga164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTaiga2164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTallGrass164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenTrees164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenWaterlily164;

public class BiomeDecorator164 {

    private final WorldGenerator clayGen = new WorldGenClay(4);
    private final WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);
    private final WorldGenerator dirtGen = new WorldGenMinable(Blocks.dirt, 32);
    private final WorldGenerator gravelGen = new WorldGenMinable(Blocks.gravel, 32);
    private final WorldGenerator coalGen = new WorldGenMinable(Blocks.coal_ore, 16);
    private final WorldGenerator ironGen = new WorldGenMinable(Blocks.iron_ore, 8);
    private final WorldGenerator goldGen = new WorldGenMinable(Blocks.gold_ore, 8);
    private final WorldGenerator redstoneGen = new WorldGenMinable(Blocks.redstone_ore, 7);
    private final WorldGenerator diamondGen = new WorldGenMinable(Blocks.diamond_ore, 7);
    private final WorldGenerator lapisGen = new WorldGenMinable(Blocks.lapis_ore, 6);
    private final WorldGenerator silverfishGen = new WorldGenMinable(Blocks.monster_egg, 8);
    private final WorldGenerator plantYellowGen = new WorldGenFlowers164(Blocks.yellow_flower);
    private final WorldGenerator plantRedGen = new WorldGenFlowers164(Blocks.red_flower);
    private final WorldGenerator mushroomBrownGen = new WorldGenFlowers164(Blocks.brown_mushroom);
    private final WorldGenerator mushroomRedGen = new WorldGenFlowers164(Blocks.red_mushroom);
    private final WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
    private final WorldGenerator reedGen = new WorldGenReed();
    private final WorldGenerator cactusGen = new WorldGenCactus();
    private final WorldGenerator waterlilyGen = new WorldGenWaterlily164();
    private final WorldGenerator treesGen = new WorldGenTrees164(false);
    private final WorldGenBigTree164[] bigTreeGens = new WorldGenBigTree164[BiomeIds164.count()];
    private final WorldGenerator forestGen = new WorldGenForest164(false);
    private final WorldGenerator swampGen = new WorldGenSwamp164();

    private World world;
    private Random rand;
    private int chunkX;
    private int chunkZ;
    private Decoration164 settings;
    private BiomeGenBase biome;

    public void decorate(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
        if (this.world != null) {
            throw new RuntimeException("Already decorating!!");
        }

        this.world = world;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.settings = Decoration164.get(biome);
        this.biome = biome;
        this.decorate();
        this.decorateExtra();
        this.world = null;
        this.rand = null;
        this.settings = null;
        this.biome = null;
    }

    private void decorate() {
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(this.world, this.rand, this.chunkX, this.chunkZ));
        this.generateOres();
        int count;
        int x;
        int y;
        int z;

        boolean generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.SAND);
        for (count = 0; generate && count < this.settings.sandPerChunk2; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.sandGen.generate(this.world, this.rand, x, this.world.getTopSolidOrLiquidBlock(x, z), z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.CLAY);
        for (count = 0; generate && count < this.settings.clayPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.clayGen.generate(this.world, this.rand, x, this.world.getTopSolidOrLiquidBlock(x, z), z);
        }

        generate = TerrainGen.decorate(
            this.world,
            this.rand,
            this.chunkX,
            this.chunkZ,
            DecorateBiomeEvent.Decorate.EventType.SAND_PASS2);
        for (count = 0; generate && count < this.settings.sandPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.sandGen.generate(this.world, this.rand, x, this.world.getTopSolidOrLiquidBlock(x, z), z);
        }

        int trees = this.settings.treesPerChunk;

        if (this.rand.nextInt(10) == 0) {
            ++trees;
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.TREE);
        for (count = 0; generate && count < trees; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            WorldGenerator tree = this.getRandomWorldGenForTrees();
            tree.setScale(1.0D, 1.0D, 1.0D);
            tree.generate(this.world, this.rand, x, this.world.getHeightValue(x, z), z);
        }

        generate = TerrainGen.decorate(
            this.world,
            this.rand,
            this.chunkX,
            this.chunkZ,
            DecorateBiomeEvent.Decorate.EventType.BIG_SHROOM);
        for (count = 0; generate && count < this.settings.bigMushroomsPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.bigMushroomGen.generate(this.world, this.rand, x, this.world.getHeightValue(x, z), z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.FLOWERS);
        for (count = 0; generate && count < this.settings.flowersPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.plantYellowGen.generate(this.world, this.rand, x, y, z);

            if (this.rand.nextInt(4) == 0) {
                x = this.chunkX + this.rand.nextInt(16) + 8;
                y = this.rand.nextInt(128);
                z = this.chunkZ + this.rand.nextInt(16) + 8;
                this.plantRedGen.generate(this.world, this.rand, x, y, z);
            }
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.GRASS);
        for (count = 0; generate && count < this.settings.grassPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            WorldGenerator grass = this.getRandomWorldGenForGrass();
            grass.generate(this.world, this.rand, x, y, z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH);
        for (count = 0; generate && count < this.settings.deadBushPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            (new WorldGenDeadBush164()).generate(this.world, this.rand, x, y, z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.LILYPAD);
        for (count = 0; generate && count < this.settings.waterlilyPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            z = this.chunkZ + this.rand.nextInt(16) + 8;

            for (y = this.rand.nextInt(128); y > 0 && this.world.getBlock(x, y - 1, z) == Blocks.air; --y) {
                ;
            }

            this.waterlilyGen.generate(this.world, this.rand, x, y, z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.SHROOM);
        for (count = 0; generate && count < this.settings.mushroomsPerChunk; ++count) {
            if (this.rand.nextInt(4) == 0) {
                x = this.chunkX + this.rand.nextInt(16) + 8;
                z = this.chunkZ + this.rand.nextInt(16) + 8;
                y = this.world.getHeightValue(x, z);
                this.mushroomBrownGen.generate(this.world, this.rand, x, y, z);
            }

            if (this.rand.nextInt(8) == 0) {
                x = this.chunkX + this.rand.nextInt(16) + 8;
                z = this.chunkZ + this.rand.nextInt(16) + 8;
                y = this.rand.nextInt(128);
                this.mushroomRedGen.generate(this.world, this.rand, x, y, z);
            }
        }

        if (generate && this.rand.nextInt(4) == 0) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.mushroomBrownGen.generate(this.world, this.rand, x, y, z);
        }

        if (generate && this.rand.nextInt(8) == 0) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.mushroomRedGen.generate(this.world, this.rand, x, y, z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.REED);
        for (count = 0; generate && count < this.settings.reedsPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            this.reedGen.generate(this.world, this.rand, x, y, z);
        }

        for (count = 0; generate && count < 10; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.reedGen.generate(this.world, this.rand, x, y, z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.PUMPKIN);
        if (generate && this.rand.nextInt(32) == 0) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            (new WorldGenPumpkin()).generate(this.world, this.rand, x, y, z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.CACTUS);
        for (count = 0; generate && count < this.settings.cactiPerChunk; ++count) {
            x = this.chunkX + this.rand.nextInt(16) + 8;
            y = this.rand.nextInt(128);
            z = this.chunkZ + this.rand.nextInt(16) + 8;
            this.cactusGen.generate(this.world, this.rand, x, y, z);
        }

        generate = TerrainGen
            .decorate(this.world, this.rand, this.chunkX, this.chunkZ, DecorateBiomeEvent.Decorate.EventType.LAKE);
        if (generate && this.settings.generateLakes) {
            for (count = 0; count < 50; ++count) {
                x = this.chunkX + this.rand.nextInt(16) + 8;
                y = this.rand.nextInt(this.rand.nextInt(120) + 8);
                z = this.chunkZ + this.rand.nextInt(16) + 8;
                (new WorldGenLiquids(Blocks.flowing_water)).generate(this.world, this.rand, x, y, z);
            }

            for (count = 0; count < 20; ++count) {
                x = this.chunkX + this.rand.nextInt(16) + 8;
                y = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(112) + 8) + 8);
                z = this.chunkZ + this.rand.nextInt(16) + 8;
                (new WorldGenLiquids(Blocks.flowing_lava)).generate(this.world, this.rand, x, y, z);
            }
        }

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(this.world, this.rand, this.chunkX, this.chunkZ));
    }

    private void decorateExtra() {
        int count;
        int x;
        int y;
        int z;

        switch (this.settings.kind) {
            case HILLS:
                count = 3 + this.rand.nextInt(6);

                for (int i = 0; i < count; ++i) {
                    x = this.chunkX + this.rand.nextInt(16);
                    y = this.rand.nextInt(28) + 4;
                    z = this.chunkZ + this.rand.nextInt(16);

                    if (this.world.getBlock(x, y, z) == Blocks.stone) {
                        this.world.setBlock(x, y, z, Blocks.emerald_ore, 0, 2);
                    }
                }

                for (int i = 0; i < 7; ++i) {
                    x = this.chunkX + this.rand.nextInt(16);
                    y = this.rand.nextInt(64);
                    z = this.chunkZ + this.rand.nextInt(16);
                    this.silverfishGen.generate(this.world, this.rand, x, y, z);
                }

                break;
            case DESERT:
                if (this.rand.nextInt(1000) == 0) {
                    x = this.chunkX + this.rand.nextInt(16) + 8;
                    z = this.chunkZ + this.rand.nextInt(16) + 8;
                    (new WorldGenDesertWells())
                        .generate(this.world, this.rand, x, this.world.getHeightValue(x, z) + 1, z);
                }

                break;
            case JUNGLE:
                WorldGenVines vines = new WorldGenVines();

                for (int i = 0; i < 50; ++i) {
                    x = this.chunkX + this.rand.nextInt(16) + 8;
                    byte vineY = 64;
                    z = this.chunkZ + this.rand.nextInt(16) + 8;
                    vines.generate(this.world, this.rand, x, vineY, z);
                }

                break;
            default:
                break;
        }
    }

    private WorldGenerator getRandomWorldGenForTrees() {
        switch (this.settings.kind) {
            case FOREST:
                return this.rand.nextInt(5) == 0 ? this.forestGen
                    : (this.rand.nextInt(10) == 0 ? this.bigTree() : this.treesGen);
            case TAIGA:
                return this.rand.nextInt(3) == 0 ? new WorldGenTaiga164() : new WorldGenTaiga2164();
            case SWAMP:
                return this.swampGen;
            case JUNGLE:
                return this.rand.nextInt(10) == 0 ? this.bigTree()
                    : (this.rand.nextInt(2) == 0 ? new WorldGenShrub164(3, 0)
                        : (this.rand.nextInt(3) == 0 ? new WorldGenHugeTrees164(false, 10 + this.rand.nextInt(20), 3, 3)
                            : new WorldGenTrees164(false, 4 + this.rand.nextInt(7), 3, 3, true)));
            default:
                return this.rand.nextInt(10) == 0 ? this.bigTree() : this.treesGen;
        }
    }

    private WorldGenerator bigTree() {
        WorldGenBigTree164 generator = this.bigTreeGens[this.biome.biomeID];

        if (generator == null) {
            generator = new WorldGenBigTree164(false);
            this.bigTreeGens[this.biome.biomeID] = generator;
        }

        return generator;
    }

    private WorldGenerator getRandomWorldGenForGrass() {
        if (this.settings.kind == Decoration164.Kind.JUNGLE) {
            return this.rand.nextInt(4) == 0 ? new WorldGenTallGrass164(Blocks.tallgrass, 2)
                : new WorldGenTallGrass164(Blocks.tallgrass, 1);
        }

        return new WorldGenTallGrass164(Blocks.tallgrass, 1);
    }

    private void genStandardOre1(int count, WorldGenerator generator, int minY, int maxY) {
        for (int i = 0; i < count; ++i) {
            int x = this.chunkX + this.rand.nextInt(16);
            int y = this.rand.nextInt(maxY - minY) + minY;
            int z = this.chunkZ + this.rand.nextInt(16);
            generator.generate(this.world, this.rand, x, y, z);
        }
    }

    private void genStandardOre2(int count, WorldGenerator generator, int centerY, int spread) {
        for (int i = 0; i < count; ++i) {
            int x = this.chunkX + this.rand.nextInt(16);
            int y = this.rand.nextInt(spread) + this.rand.nextInt(spread) + (centerY - spread);
            int z = this.chunkZ + this.rand.nextInt(16);
            generator.generate(this.world, this.rand, x, y, z);
        }
    }

    private void generateOres() {
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(this.world, this.rand, this.chunkX, this.chunkZ));
        if (this.ore(this.dirtGen, OreGenEvent.GenerateMinable.EventType.DIRT)) {
            this.genStandardOre1(20, this.dirtGen, 0, 128);
        }
        if (this.ore(this.gravelGen, OreGenEvent.GenerateMinable.EventType.GRAVEL)) {
            this.genStandardOre1(10, this.gravelGen, 0, 128);
        }
        if (this.ore(this.coalGen, OreGenEvent.GenerateMinable.EventType.COAL)) {
            this.genStandardOre1(20, this.coalGen, 0, 128);
        }
        if (this.ore(this.ironGen, OreGenEvent.GenerateMinable.EventType.IRON)) {
            this.genStandardOre1(20, this.ironGen, 0, 64);
        }
        if (this.ore(this.goldGen, OreGenEvent.GenerateMinable.EventType.GOLD)) {
            this.genStandardOre1(2, this.goldGen, 0, 32);
        }
        if (this.ore(this.redstoneGen, OreGenEvent.GenerateMinable.EventType.REDSTONE)) {
            this.genStandardOre1(8, this.redstoneGen, 0, 16);
        }
        if (this.ore(this.diamondGen, OreGenEvent.GenerateMinable.EventType.DIAMOND)) {
            this.genStandardOre1(1, this.diamondGen, 0, 16);
        }
        if (this.ore(this.lapisGen, OreGenEvent.GenerateMinable.EventType.LAPIS)) {
            this.genStandardOre2(1, this.lapisGen, 16, 16);
        }
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(this.world, this.rand, this.chunkX, this.chunkZ));
    }

    private boolean ore(WorldGenerator generator, OreGenEvent.GenerateMinable.EventType type) {
        return TerrainGen.generateOre(this.world, this.rand, generator, this.chunkX, this.chunkZ, type);
    }
}
