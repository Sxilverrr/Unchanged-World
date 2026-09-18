package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenTaiga2164 extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(4) + 6;
        int trunkHeight = 1 + rand.nextInt(2);
        int leavesHeight = height - trunkHeight;
        int maxRadius = 2 + rand.nextInt(2);
        boolean canGrow = true;

        if (y >= 1 && y + height + 1 <= 256) {
            for (int cy = y; cy <= y + 1 + height && canGrow; ++cy) {
                int radius;

                if (cy - y < trunkHeight) {
                    radius = 0;
                } else {
                    radius = maxRadius;
                }

                for (int cx = x - radius; cx <= x + radius && canGrow; ++cx) {
                    for (int cz = z - radius; cz <= z + radius && canGrow; ++cz) {
                        if (cy >= 0 && cy < 256) {
                            Block block = world.getBlock(cx, cy, cz);

                            if (block != Blocks.air && block != Blocks.leaves) {
                                canGrow = false;
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

            if ((soil == Blocks.grass || soil == Blocks.dirt) && y < 256 - height - 1) {
                this.setBlockAndNotifyAdequately(world, x, y - 1, z, Blocks.dirt, 0);
                int radius = rand.nextInt(2);
                int radiusLimit = 1;
                int radiusReset = 0;

                for (int layer = 0; layer <= leavesHeight; ++layer) {
                    int cy = y + height - layer;

                    for (int cx = x - radius; cx <= x + radius; ++cx) {
                        int dx = cx - x;

                        for (int cz = z - radius; cz <= z + radius; ++cz) {
                            int dz = cz - z;

                            if ((Math.abs(dx) != radius || Math.abs(dz) != radius || radius <= 0)
                                && !world.getBlock(cx, cy, cz)
                                    .func_149730_j()) {
                                this.setBlockAndNotifyAdequately(world, cx, cy, cz, Blocks.leaves, 1);
                            }
                        }
                    }

                    if (radius >= radiusLimit) {
                        radius = radiusReset;
                        radiusReset = 1;
                        ++radiusLimit;

                        if (radiusLimit > maxRadius) {
                            radiusLimit = maxRadius;
                        }
                    } else {
                        ++radius;
                    }
                }

                int trunkCut = rand.nextInt(3);

                for (int cy = 0; cy < height - trunkCut; ++cy) {
                    Block block = world.getBlock(x, y + cy, z);

                    if (block == Blocks.air || block == Blocks.leaves) {
                        this.setBlockAndNotifyAdequately(world, x, y + cy, z, Blocks.log, 1);
                    }
                }

                return true;
            }

            return false;
        }

        return false;
    }
}
