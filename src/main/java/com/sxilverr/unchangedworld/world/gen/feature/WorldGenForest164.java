package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenForest164 extends WorldGenerator {

    public WorldGenForest164(boolean notify) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(3) + 5;
        boolean canGrow = true;

        if (y >= 1 && y + height + 1 <= 256) {
            for (int cy = y; cy <= y + 1 + height; ++cy) {
                byte radius = 1;

                if (cy == y) {
                    radius = 0;
                }

                if (cy >= y + 1 + height - 2) {
                    radius = 2;
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
                this.func_150515_a(world, x, y - 1, z, Blocks.dirt);

                for (int cy = y - 3 + height; cy <= y + height; ++cy) {
                    int layer = cy - (y + height);
                    int radius = 1 - layer / 2;

                    for (int cx = x - radius; cx <= x + radius; ++cx) {
                        int dx = cx - x;

                        for (int cz = z - radius; cz <= z + radius; ++cz) {
                            int dz = cz - z;

                            if (Math.abs(dx) != radius || Math.abs(dz) != radius
                                || rand.nextInt(2) != 0 && layer != 0) {
                                Block block = world.getBlock(cx, cy, cz);

                                if (block == Blocks.air || block == Blocks.leaves) {
                                    this.setBlockAndNotifyAdequately(world, cx, cy, cz, Blocks.leaves, 2);
                                }
                            }
                        }
                    }
                }

                for (int cy = 0; cy < height; ++cy) {
                    Block block = world.getBlock(x, y + cy, z);

                    if (block == Blocks.air || block == Blocks.leaves) {
                        this.setBlockAndNotifyAdequately(world, x, y + cy, z, Blocks.log, 2);
                    }
                }

                return true;
            }

            return false;
        }

        return false;
    }
}
