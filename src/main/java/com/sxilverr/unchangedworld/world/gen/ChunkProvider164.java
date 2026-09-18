package com.sxilverr.unchangedworld.world.gen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import com.sxilverr.unchangedworld.world.biome.Biome164;
import com.sxilverr.unchangedworld.world.biome.BiomeDecorator164;
import com.sxilverr.unchangedworld.world.biome.Climate164;
import com.sxilverr.unchangedworld.world.biome.SpawnerAnimals164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenDungeons164;
import com.sxilverr.unchangedworld.world.gen.feature.WorldGenLakes164;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class ChunkProvider164 implements IChunkProvider {

    private static final List<BiomeGenBase> STRONGHOLD_BIOMES = Arrays.asList(
        BiomeGenBase.desert,
        BiomeGenBase.forest,
        BiomeGenBase.extremeHills,
        BiomeGenBase.swampland,
        BiomeGenBase.taiga,
        BiomeGenBase.icePlains,
        BiomeGenBase.iceMountains,
        BiomeGenBase.desertHills,
        BiomeGenBase.forestHills,
        BiomeGenBase.extremeHillsEdge,
        BiomeGenBase.jungle,
        BiomeGenBase.jungleHills);

    private final World worldObj;
    private final boolean mapFeaturesEnabled;
    private final Random rand;
    private final NoiseGeneratorOctaves noiseGen1;
    private final NoiseGeneratorOctaves noiseGen2;
    private final NoiseGeneratorOctaves noiseGen3;
    private final NoiseGeneratorOctaves noiseGen4;
    public final NoiseGeneratorOctaves noiseGen5;
    public final NoiseGeneratorOctaves noiseGen6;
    public final NoiseGeneratorOctaves mobSpawnerNoise;
    private double[] noiseArray;
    private double[] stoneNoise = new double[256];
    private float[] parabolicField;
    private double[] noise1;
    private double[] noise2;
    private double[] noise3;
    private double[] noise5;
    private double[] noise6;
    private BiomeGenBase[] biomesForGeneration;
    private MapGenBase caveGenerator = new MapGenCaves164();
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();
    private MapGenVillage villageGenerator = new MapGenVillage();
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft(Collections.singletonMap("chance", "0.01"));
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
    private MapGenBase ravineGenerator = new MapGenRavine164();
    private final BiomeDecorator164 decorator = new BiomeDecorator164();

    {
        caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
        strongholdGenerator = (MapGenStronghold) TerrainGen
            .getModdedMapGen(strongholdGenerator, InitMapGenEvent.EventType.STRONGHOLD);
        villageGenerator = (MapGenVillage) TerrainGen
            .getModdedMapGen(villageGenerator, InitMapGenEvent.EventType.VILLAGE);
        mineshaftGenerator = (MapGenMineshaft) TerrainGen
            .getModdedMapGen(mineshaftGenerator, InitMapGenEvent.EventType.MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen
            .getModdedMapGen(scatteredFeatureGenerator, InitMapGenEvent.EventType.SCATTERED_FEATURE);
        ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, InitMapGenEvent.EventType.RAVINE);
        strongholdGenerator.field_151546_e.clear();
        strongholdGenerator.field_151546_e.addAll(STRONGHOLD_BIOMES);
    }

    public ChunkProvider164(World world, long seed, boolean mapFeaturesEnabled) {
        this.worldObj = world;
        this.mapFeaturesEnabled = mapFeaturesEnabled;
        this.rand = new Random(seed);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
    }

    public void generateTerrain(int chunkX, int chunkZ, Block[] blocks) {
        byte cellsXZ = 4;
        byte cellsY = 16;
        byte seaLevel = 63;
        int noiseSizeX = cellsXZ + 1;
        byte noiseSizeY = 17;
        int noiseSizeZ = cellsXZ + 1;
        this.biomesForGeneration = this.worldObj.getWorldChunkManager()
            .getBiomesForGeneration(
                this.biomesForGeneration,
                chunkX * 4 - 2,
                chunkZ * 4 - 2,
                noiseSizeX + 5,
                noiseSizeZ + 5);
        this.noiseArray = this.initializeNoiseField(
            this.noiseArray,
            chunkX * cellsXZ,
            0,
            chunkZ * cellsXZ,
            noiseSizeX,
            noiseSizeY,
            noiseSizeZ);

        for (int cx = 0; cx < cellsXZ; ++cx) {
            for (int cz = 0; cz < cellsXZ; ++cz) {
                for (int cy = 0; cy < cellsY; ++cy) {
                    double yStep = 0.125D;
                    double n00 = this.noiseArray[((cx + 0) * noiseSizeZ + cz + 0) * noiseSizeY + cy + 0];
                    double n01 = this.noiseArray[((cx + 0) * noiseSizeZ + cz + 1) * noiseSizeY + cy + 0];
                    double n10 = this.noiseArray[((cx + 1) * noiseSizeZ + cz + 0) * noiseSizeY + cy + 0];
                    double n11 = this.noiseArray[((cx + 1) * noiseSizeZ + cz + 1) * noiseSizeY + cy + 0];
                    double d00 = (this.noiseArray[((cx + 0) * noiseSizeZ + cz + 0) * noiseSizeY + cy + 1] - n00)
                        * yStep;
                    double d01 = (this.noiseArray[((cx + 0) * noiseSizeZ + cz + 1) * noiseSizeY + cy + 1] - n01)
                        * yStep;
                    double d10 = (this.noiseArray[((cx + 1) * noiseSizeZ + cz + 0) * noiseSizeY + cy + 1] - n10)
                        * yStep;
                    double d11 = (this.noiseArray[((cx + 1) * noiseSizeZ + cz + 1) * noiseSizeY + cy + 1] - n11)
                        * yStep;

                    for (int dy = 0; dy < 8; ++dy) {
                        double xStep = 0.25D;
                        double z0 = n00;
                        double z1 = n01;
                        double dx0 = (n10 - n00) * xStep;
                        double dx1 = (n11 - n01) * xStep;

                        for (int dx = 0; dx < 4; ++dx) {
                            int y = cy * 8 + dy;
                            int index = (dx + cx * 4) << 12 | (cz * 4) << 8 | y;
                            double zStep = 0.25D;
                            double dz = (z1 - z0) * zStep;
                            double density = z0 - dz;

                            for (int dzi = 0; dzi < 4; ++dzi) {
                                if ((density += dz) > 0.0D) {
                                    blocks[index] = Blocks.stone;
                                } else if (y < seaLevel) {
                                    blocks[index] = Blocks.water;
                                } else {
                                    blocks[index] = null;
                                }

                                index += 256;
                            }

                            z0 += dx0;
                            z1 += dx1;
                        }

                        n00 += d00;
                        n01 += d01;
                        n10 += d10;
                        n11 += d11;
                    }
                }
            }
        }
    }

    public void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(
            this,
            chunkX,
            chunkZ,
            blocks,
            metadata,
            biomes,
            this.worldObj);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.getResult() == Result.DENY) {
            return;
        }

        byte seaLevel = 63;
        double scale = 0.03125D;
        this.stoneNoise = this.noiseGen4.generateNoiseOctaves(
            this.stoneNoise,
            chunkX * 16,
            chunkZ * 16,
            0,
            16,
            16,
            1,
            scale * 2.0D,
            scale * 2.0D,
            scale * 2.0D);

        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                Biome164 biome = Biome164.get(biomes[x + z * 16]);
                float temperature = biome.temperature;
                int depth = (int) (this.stoneNoise[z + x * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int remaining = -1;
                Block top = biome.topBlock;
                Block filler = biome.fillerBlock;

                for (int y = 127; y >= 0; --y) {
                    int index = (x * 16 + z) * 256 + y;

                    if (y <= 0 + this.rand.nextInt(5)) {
                        blocks[index] = Blocks.bedrock;
                    } else {
                        Block block = blocks[index];

                        if (block == null || block == Blocks.air) {
                            remaining = -1;
                        } else if (block == Blocks.stone) {
                            if (remaining == -1) {
                                if (depth <= 0) {
                                    top = null;
                                    filler = Blocks.stone;
                                } else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                                    top = biome.topBlock;
                                    filler = biome.fillerBlock;
                                }

                                if (y < seaLevel && top == null) {
                                    if (temperature < 0.15F) {
                                        top = Blocks.ice;
                                    } else {
                                        top = Blocks.water;
                                    }
                                }

                                remaining = depth;

                                if (y >= seaLevel - 1) {
                                    blocks[index] = top;
                                } else {
                                    blocks[index] = filler;
                                }
                            } else if (remaining > 0) {
                                --remaining;
                                blocks[index] = filler;

                                if (remaining == 0 && filler == Blocks.sand) {
                                    remaining = this.rand.nextInt(4);
                                    filler = Blocks.sandstone;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return this.provideChunk(chunkX, chunkZ);
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        this.rand.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);
        Block[] blocks = new Block[65536];
        byte[] metadata = new byte[65536];
        this.generateTerrain(chunkX, chunkZ, blocks);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager()
            .loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
        this.replaceBlocksForBiome(chunkX, chunkZ, blocks, metadata, this.biomesForGeneration);
        this.caveGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
        this.ravineGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);

        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
            this.villageGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
            this.strongholdGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
            this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
        }

        Chunk chunk = new Chunk(this.worldObj, blocks, metadata, chunkX, chunkZ);
        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte) this.biomesForGeneration[i].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private double[] initializeNoiseField(double[] noise, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
        if (noise == null) {
            noise = new double[sizeX * sizeY * sizeZ];
        }

        if (this.parabolicField == null) {
            this.parabolicField = new float[25];

            for (int ox = -2; ox <= 2; ++ox) {
                for (int oz = -2; oz <= 2; ++oz) {
                    float weight = 10.0F / MathHelper.sqrt_float((float) (ox * ox + oz * oz) + 0.2F);
                    this.parabolicField[ox + 2 + (oz + 2) * 5] = weight;
                }
            }
        }

        double horizontalScale = 684.412D;
        double verticalScale = 684.412D;
        this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, x, z, sizeX, sizeZ, 1.121D, 1.121D, 0.5D);
        this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, x, z, sizeX, sizeZ, 200.0D, 200.0D, 0.5D);
        this.noise3 = this.noiseGen3.generateNoiseOctaves(
            this.noise3,
            x,
            y,
            z,
            sizeX,
            sizeY,
            sizeZ,
            horizontalScale / 80.0D,
            verticalScale / 160.0D,
            horizontalScale / 80.0D);
        this.noise1 = this.noiseGen1.generateNoiseOctaves(
            this.noise1,
            x,
            y,
            z,
            sizeX,
            sizeY,
            sizeZ,
            horizontalScale,
            verticalScale,
            horizontalScale);
        this.noise2 = this.noiseGen2.generateNoiseOctaves(
            this.noise2,
            x,
            y,
            z,
            sizeX,
            sizeY,
            sizeZ,
            horizontalScale,
            verticalScale,
            horizontalScale);
        int index = 0;
        int index2D = 0;

        for (int ix = 0; ix < sizeX; ++ix) {
            for (int iz = 0; iz < sizeZ; ++iz) {
                float maxSum = 0.0F;
                float minSum = 0.0F;
                float weightSum = 0.0F;
                byte radius = 2;
                Biome164 center = Biome164.get(this.biomesForGeneration[ix + 2 + (iz + 2) * (sizeX + 5)]);

                for (int ox = -radius; ox <= radius; ++ox) {
                    for (int oz = -radius; oz <= radius; ++oz) {
                        Biome164 neighbour = Biome164
                            .get(this.biomesForGeneration[ix + ox + 2 + (iz + oz + 2) * (sizeX + 5)]);
                        float weight = this.parabolicField[ox + 2 + (oz + 2) * 5] / (neighbour.minHeight + 2.0F);

                        if (neighbour.minHeight > center.minHeight) {
                            weight /= 2.0F;
                        }

                        maxSum += neighbour.maxHeight * weight;
                        minSum += neighbour.minHeight * weight;
                        weightSum += weight;
                    }
                }

                maxSum /= weightSum;
                minSum /= weightSum;
                maxSum = maxSum * 0.9F + 0.1F;
                minSum = (minSum * 4.0F - 1.0F) / 8.0F;
                double depthNoise = this.noise6[index2D] / 8000.0D;

                if (depthNoise < 0.0D) {
                    depthNoise = -depthNoise * 0.3D;
                }

                depthNoise = depthNoise * 3.0D - 2.0D;

                if (depthNoise < 0.0D) {
                    depthNoise /= 2.0D;

                    if (depthNoise < -1.0D) {
                        depthNoise = -1.0D;
                    }

                    depthNoise /= 1.4D;
                    depthNoise /= 2.0D;
                } else {
                    if (depthNoise > 1.0D) {
                        depthNoise = 1.0D;
                    }

                    depthNoise /= 8.0D;
                }

                ++index2D;

                for (int iy = 0; iy < sizeY; ++iy) {
                    double offset = (double) minSum;
                    double scale = (double) maxSum;
                    offset += depthNoise * 0.2D;
                    offset = offset * (double) sizeY / 16.0D;
                    double midpoint = (double) sizeY / 2.0D + offset * 4.0D;
                    double density = 0.0D;
                    double falloff = ((double) iy - midpoint) * 12.0D * 128.0D / 128.0D / scale;

                    if (falloff < 0.0D) {
                        falloff *= 4.0D;
                    }

                    double low = this.noise1[index] / 512.0D;
                    double high = this.noise2[index] / 512.0D;
                    double selector = (this.noise3[index] / 10.0D + 1.0D) / 2.0D;

                    if (selector < 0.0D) {
                        density = low;
                    } else if (selector > 1.0D) {
                        density = high;
                    } else {
                        density = low + (high - low) * selector;
                    }

                    density -= falloff;

                    if (iy > sizeY - 4) {
                        double taper = (double) ((float) (iy - (sizeY - 4)) / 3.0F);
                        density = density * (1.0D - taper) + -10.0D * taper;
                    }

                    noise[index] = density;
                    ++index;
                }
            }
        }

        return noise;
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return true;
    }

    @Override
    public void populate(IChunkProvider provider, int chunkX, int chunkZ) {
        Generation164.begin();

        try {
            this.populate164(provider, chunkX, chunkZ);
        } finally {
            Generation164.end();
        }
    }

    private void populate164(IChunkProvider provider, int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        int x = chunkX * 16;
        int z = chunkZ * 16;
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x + 16, z + 16);
        this.rand.setSeed(this.worldObj.getSeed());
        long xSeed = this.rand.nextLong() / 2L * 2L + 1L;
        long zSeed = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long) chunkX * xSeed + (long) chunkZ * zSeed ^ this.worldObj.getSeed());
        boolean villageGenerated = false;

        MinecraftForge.EVENT_BUS
            .post(new PopulateChunkEvent.Pre(provider, this.worldObj, this.rand, chunkX, chunkZ, villageGenerated));

        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, chunkX, chunkZ);
            villageGenerated = this.villageGenerator
                .generateStructuresInChunk(this.worldObj, this.rand, chunkX, chunkZ);
            this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.rand, chunkX, chunkZ);
            this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, chunkX, chunkZ);
        }

        int px;
        int py;
        int pz;

        if (biome != BiomeGenBase.desert && biome != BiomeGenBase.desertHills
            && !villageGenerated
            && this.rand.nextInt(4) == 0
            && TerrainGen.populate(
                provider,
                this.worldObj,
                this.rand,
                chunkX,
                chunkZ,
                villageGenerated,
                PopulateChunkEvent.Populate.EventType.LAKE)) {
            px = x + this.rand.nextInt(16) + 8;
            py = this.rand.nextInt(128);
            pz = z + this.rand.nextInt(16) + 8;
            (new WorldGenLakes164(Blocks.water)).generate(this.worldObj, this.rand, px, py, pz);
        }

        if (TerrainGen.populate(
            provider,
            this.worldObj,
            this.rand,
            chunkX,
            chunkZ,
            villageGenerated,
            PopulateChunkEvent.Populate.EventType.LAVA) && !villageGenerated && this.rand.nextInt(8) == 0) {
            px = x + this.rand.nextInt(16) + 8;
            py = this.rand.nextInt(this.rand.nextInt(120) + 8);
            pz = z + this.rand.nextInt(16) + 8;

            if (py < 63 || this.rand.nextInt(10) == 0) {
                (new WorldGenLakes164(Blocks.lava)).generate(this.worldObj, this.rand, px, py, pz);
            }
        }

        boolean generate = TerrainGen.populate(
            provider,
            this.worldObj,
            this.rand,
            chunkX,
            chunkZ,
            villageGenerated,
            PopulateChunkEvent.Populate.EventType.DUNGEON);

        for (int i = 0; generate && i < 8; ++i) {
            px = x + this.rand.nextInt(16) + 8;
            py = this.rand.nextInt(128);
            pz = z + this.rand.nextInt(16) + 8;
            (new WorldGenDungeons164()).generate(this.worldObj, this.rand, px, py, pz);
        }

        this.decorator.decorate(this.worldObj, this.rand, biome, x, z);

        if (TerrainGen.populate(
            provider,
            this.worldObj,
            this.rand,
            chunkX,
            chunkZ,
            villageGenerated,
            PopulateChunkEvent.Populate.EventType.ANIMALS)) {
            SpawnerAnimals164.performWorldGenSpawning(this.worldObj, biome, x + 8, z + 8, 16, 16, this.rand);
        }

        x += 8;
        z += 8;
        generate = TerrainGen.populate(
            provider,
            this.worldObj,
            this.rand,
            chunkX,
            chunkZ,
            villageGenerated,
            PopulateChunkEvent.Populate.EventType.ICE);

        for (int dx = 0; generate && dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                int height = this.worldObj.getPrecipitationHeight(x + dx, z + dz);

                if (Climate164.canBlockFreeze(this.worldObj, dx + x, height - 1, dz + z, false)) {
                    this.worldObj.setBlock(dx + x, height - 1, dz + z, Blocks.ice, 0, 2);
                }

                if (Climate164.canSnowAt(this.worldObj, dx + x, height, dz + z)) {
                    this.worldObj.setBlock(dx + x, height, dz + z, Blocks.snow_layer, 0, 2);
                }
            }
        }

        MinecraftForge.EVENT_BUS
            .post(new PopulateChunkEvent.Post(provider, this.worldObj, this.rand, chunkX, chunkZ, villageGenerated));
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean saveChunks(boolean saveAll, IProgressUpdate progress) {
        return true;
    }

    @Override
    public void saveExtraData() {}

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "RandomLevelSource";
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x, z);
        return creatureType == EnumCreatureType.monster && this.scatteredFeatureGenerator.func_143030_a(x, y, z)
            ? this.scatteredFeatureGenerator.getScatteredFeatureSpawnList()
            : biome.getSpawnableList(creatureType);
    }

    @Override
    public ChunkPosition func_147416_a(World world, String structureName, int x, int y, int z) {
        return "Stronghold".equals(structureName) && this.strongholdGenerator != null
            ? this.strongholdGenerator.func_151545_a(world, x, y, z)
            : null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int chunkX, int chunkZ) {
        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, (Block[]) null);
            this.villageGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, (Block[]) null);
            this.strongholdGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, (Block[]) null);
            this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, (Block[]) null);
        }
    }
}
