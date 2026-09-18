package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.sxilverr.unchangedworld.world.biome.Biome164;
import com.sxilverr.unchangedworld.world.biome.Climate164;

public class WorldGenLakes164 extends WorldGenerator {

    private final Block block;

    public WorldGenLakes164(Block block) {
        this.block = block;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        x -= 8;

        for (z -= 8; y > 5 && world.isAirBlock(x, y, z); --y) {
            ;
        }

        if (y <= 4) {
            return false;
        }

        y -= 4;
        boolean[] shape = new boolean[2048];
        int blobs = rand.nextInt(4) + 4;

        for (int i = 0; i < blobs; ++i) {
            double sizeX = rand.nextDouble() * 6.0D + 3.0D;
            double sizeY = rand.nextDouble() * 4.0D + 2.0D;
            double sizeZ = rand.nextDouble() * 6.0D + 3.0D;
            double centerX = rand.nextDouble() * (16.0D - sizeX - 2.0D) + 1.0D + sizeX / 2.0D;
            double centerY = rand.nextDouble() * (8.0D - sizeY - 4.0D) + 2.0D + sizeY / 2.0D;
            double centerZ = rand.nextDouble() * (16.0D - sizeZ - 2.0D) + 1.0D + sizeZ / 2.0D;

            for (int dx = 1; dx < 15; ++dx) {
                for (int dz = 1; dz < 15; ++dz) {
                    for (int dy = 1; dy < 7; ++dy) {
                        double nx = ((double) dx - centerX) / (sizeX / 2.0D);
                        double ny = ((double) dy - centerY) / (sizeY / 2.0D);
                        double nz = ((double) dz - centerZ) / (sizeZ / 2.0D);

                        if (nx * nx + ny * ny + nz * nz < 1.0D) {
                            shape[(dx * 16 + dz) * 8 + dy] = true;
                        }
                    }
                }
            }
        }

        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                for (int dy = 0; dy < 8; ++dy) {
                    if (isEdge(shape, dx, dy, dz)) {
                        Material material = world.getBlock(x + dx, y + dy, z + dz)
                            .getMaterial();

                        if (dy >= 4 && material.isLiquid()) {
                            return false;
                        }

                        if (dy < 4 && !material.isSolid() && world.getBlock(x + dx, y + dy, z + dz) != this.block) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                for (int dy = 0; dy < 8; ++dy) {
                    if (shape[(dx * 16 + dz) * 8 + dy]) {
                        world.setBlock(x + dx, y + dy, z + dz, dy >= 4 ? Blocks.air : this.block, 0, 2);
                    }
                }
            }
        }

        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                for (int dy = 4; dy < 8; ++dy) {
                    if (shape[(dx * 16 + dz) * 8 + dy] && world.getBlock(x + dx, y + dy - 1, z + dz) == Blocks.dirt
                        && world.getSavedLightValue(EnumSkyBlock.Sky, x + dx, y + dy, z + dz) > 0) {
                        if (Biome164.get(world.getBiomeGenForCoords(x + dx, z + dz)).topBlock == Blocks.mycelium) {
                            world.setBlock(x + dx, y + dy - 1, z + dz, Blocks.mycelium, 0, 2);
                        } else {
                            world.setBlock(x + dx, y + dy - 1, z + dz, Blocks.grass, 0, 2);
                        }
                    }
                }
            }
        }

        if (this.block.getMaterial() == Material.lava) {
            for (int dx = 0; dx < 16; ++dx) {
                for (int dz = 0; dz < 16; ++dz) {
                    for (int dy = 0; dy < 8; ++dy) {
                        if (isEdge(shape, dx, dy, dz) && (dy < 4 || rand.nextInt(2) != 0)
                            && world.getBlock(x + dx, y + dy, z + dz)
                                .getMaterial()
                                .isSolid()) {
                            world.setBlock(x + dx, y + dy, z + dz, Blocks.stone, 0, 2);
                        }
                    }
                }
            }
        }

        if (this.block.getMaterial() == Material.water) {
            for (int dx = 0; dx < 16; ++dx) {
                for (int dz = 0; dz < 16; ++dz) {
                    if (Climate164.canBlockFreeze(world, x + dx, y + 4, z + dz, false)) {
                        world.setBlock(x + dx, y + 4, z + dz, Blocks.ice, 0, 2);
                    }
                }
            }
        }

        return true;
    }

    private static boolean isEdge(boolean[] shape, int dx, int dy, int dz) {
        return !shape[(dx * 16 + dz) * 8 + dy]
            && (dx < 15 && shape[((dx + 1) * 16 + dz) * 8 + dy] || dx > 0 && shape[((dx - 1) * 16 + dz) * 8 + dy]
                || dz < 15 && shape[(dx * 16 + dz + 1) * 8 + dy]
                || dz > 0 && shape[(dx * 16 + (dz - 1)) * 8 + dy]
                || dy < 7 && shape[(dx * 16 + dz) * 8 + dy + 1]
                || dy > 0 && shape[(dx * 16 + dz) * 8 + (dy - 1)]);
    }
}
