package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSwamp164 extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height;

        for (height = rand.nextInt(4) + 5; world.getBlock(x, y - 1, z)
            .getMaterial() == Material.water; --y) {
            ;
        }

        boolean canGrow = true;

        if (y >= 1 && y + height + 1 <= 128) {
            for (int cy = y; cy <= y + 1 + height; ++cy) {
                byte radius = 1;

                if (cy == y) {
                    radius = 0;
                }

                if (cy >= y + 1 + height - 2) {
                    radius = 3;
                }

                for (int cx = x - radius; cx <= x + radius && canGrow; ++cx) {
                    for (int cz = z - radius; cz <= z + radius && canGrow; ++cz) {
                        if (cy >= 0 && cy < 128) {
                            Block block = world.getBlock(cx, cy, cz);

                            if (block != Blocks.air && block != Blocks.leaves) {
                                if (block != Blocks.water && block != Blocks.flowing_water) {
                                    canGrow = false;
                                } else if (cy > y) {
                                    canGrow = false;
                                }
                            }
                        } else {
                            canGrow = false;
                        }
                    }
                }
            }

            if (!canGrow) {
                return false;
            }

            Block soil = world.getBlock(x, y - 1, z);

            if ((soil == Blocks.grass || soil == Blocks.dirt) && y < 128 - height - 1) {
                this.func_150515_a(world, x, y - 1, z, Blocks.dirt);

                for (int cy = y - 3 + height; cy <= y + height; ++cy) {
                    int layer = cy - (y + height);
                    int radius = 2 - layer / 2;

                    for (int cx = x - radius; cx <= x + radius; ++cx) {
                        int dx = cx - x;

                        for (int cz = z - radius; cz <= z + radius; ++cz) {
                            int dz = cz - z;

                            if ((Math.abs(dx) != radius || Math.abs(dz) != radius || rand.nextInt(2) != 0 && layer != 0)
                                && !world.getBlock(cx, cy, cz)
                                    .func_149730_j()) {
                                this.func_150515_a(world, cx, cy, cz, Blocks.leaves);
                            }
                        }
                    }
                }

                for (int cy = 0; cy < height; ++cy) {
                    Block block = world.getBlock(x, y + cy, z);

                    if (block == Blocks.air || block == Blocks.leaves
                        || block == Blocks.flowing_water
                        || block == Blocks.water) {
                        this.func_150515_a(world, x, y + cy, z, Blocks.log);
                    }
                }

                for (int cy = y - 3 + height; cy <= y + height; ++cy) {
                    int layer = cy - (y + height);
                    int radius = 2 - layer / 2;

                    for (int cx = x - radius; cx <= x + radius; ++cx) {
                        for (int cz = z - radius; cz <= z + radius; ++cz) {
                            if (world.getBlock(cx, cy, cz) == Blocks.leaves) {
                                if (rand.nextInt(4) == 0 && world.getBlock(cx - 1, cy, cz) == Blocks.air) {
                                    this.generateVines(world, cx - 1, cy, cz, 8);
                                }

                                if (rand.nextInt(4) == 0 && world.getBlock(cx + 1, cy, cz) == Blocks.air) {
                                    this.generateVines(world, cx + 1, cy, cz, 2);
                                }

                                if (rand.nextInt(4) == 0 && world.getBlock(cx, cy, cz - 1) == Blocks.air) {
                                    this.generateVines(world, cx, cy, cz - 1, 1);
                                }

                                if (rand.nextInt(4) == 0 && world.getBlock(cx, cy, cz + 1) == Blocks.air) {
                                    this.generateVines(world, cx, cy, cz + 1, 4);
                                }
                            }
                        }
                    }
                }

                return true;
            }

            return false;
        }

        return false;
    }

    private void generateVines(World world, int x, int y, int z, int meta) {
        this.setBlockAndNotifyAdequately(world, x, y, z, Blocks.vine, meta);
        int remaining = 4;

        while (true) {
            --y;

            if (world.getBlock(x, y, z) != Blocks.air || remaining <= 0) {
                return;
            }

            this.setBlockAndNotifyAdequately(world, x, y, z, Blocks.vine, meta);
            --remaining;
        }
    }
}
