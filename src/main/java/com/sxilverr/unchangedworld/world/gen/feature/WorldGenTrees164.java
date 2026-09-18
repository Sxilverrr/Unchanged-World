package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenTrees164 extends WorldGenerator {

    private final int minTreeHeight;
    private final boolean vinesGrow;
    private final int metaWood;
    private final int metaLeaves;

    public WorldGenTrees164(boolean notify) {
        this(notify, 4, 0, 0, false);
    }

    public WorldGenTrees164(boolean notify, int minTreeHeight, int metaWood, int metaLeaves, boolean vinesGrow) {
        super(notify);
        this.minTreeHeight = minTreeHeight;
        this.metaWood = metaWood;
        this.metaLeaves = metaLeaves;
        this.vinesGrow = vinesGrow;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(3) + this.minTreeHeight;
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

                            if (block != Blocks.air && block != Blocks.leaves
                                && block != Blocks.grass
                                && block != Blocks.dirt
                                && block != Blocks.log) {
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
                byte canopyHeight = 3;
                byte canopyExtra = 0;

                for (int cy = y - canopyHeight + height; cy <= y + height; ++cy) {
                    int layer = cy - (y + height);
                    int radius = canopyExtra + 1 - layer / 2;

                    for (int cx = x - radius; cx <= x + radius; ++cx) {
                        int dx = cx - x;

                        for (int cz = z - radius; cz <= z + radius; ++cz) {
                            int dz = cz - z;

                            if (Math.abs(dx) != radius || Math.abs(dz) != radius
                                || rand.nextInt(2) != 0 && layer != 0) {
                                Block block = world.getBlock(cx, cy, cz);

                                if (block == Blocks.air || block == Blocks.leaves) {
                                    this.setBlockAndNotifyAdequately(world, cx, cy, cz, Blocks.leaves, this.metaLeaves);
                                }
                            }
                        }
                    }
                }

                for (int cy = 0; cy < height; ++cy) {
                    Block block = world.getBlock(x, y + cy, z);

                    if (block == Blocks.air || block == Blocks.leaves) {
                        this.setBlockAndNotifyAdequately(world, x, y + cy, z, Blocks.log, this.metaWood);

                        if (this.vinesGrow && cy > 0) {
                            if (rand.nextInt(3) > 0 && world.isAirBlock(x - 1, y + cy, z)) {
                                this.setBlockAndNotifyAdequately(world, x - 1, y + cy, z, Blocks.vine, 8);
                            }

                            if (rand.nextInt(3) > 0 && world.isAirBlock(x + 1, y + cy, z)) {
                                this.setBlockAndNotifyAdequately(world, x + 1, y + cy, z, Blocks.vine, 2);
                            }

                            if (rand.nextInt(3) > 0 && world.isAirBlock(x, y + cy, z - 1)) {
                                this.setBlockAndNotifyAdequately(world, x, y + cy, z - 1, Blocks.vine, 1);
                            }

                            if (rand.nextInt(3) > 0 && world.isAirBlock(x, y + cy, z + 1)) {
                                this.setBlockAndNotifyAdequately(world, x, y + cy, z + 1, Blocks.vine, 4);
                            }
                        }
                    }
                }

                if (this.vinesGrow) {
                    for (int cy = y - 3 + height; cy <= y + height; ++cy) {
                        int layer = cy - (y + height);
                        int radius = 2 - layer / 2;

                        for (int cx = x - radius; cx <= x + radius; ++cx) {
                            for (int cz = z - radius; cz <= z + radius; ++cz) {
                                if (world.getBlock(cx, cy, cz) == Blocks.leaves) {
                                    if (rand.nextInt(4) == 0 && world.getBlock(cx - 1, cy, cz) == Blocks.air) {
                                        this.growVines(world, cx - 1, cy, cz, 8);
                                    }

                                    if (rand.nextInt(4) == 0 && world.getBlock(cx + 1, cy, cz) == Blocks.air) {
                                        this.growVines(world, cx + 1, cy, cz, 2);
                                    }

                                    if (rand.nextInt(4) == 0 && world.getBlock(cx, cy, cz - 1) == Blocks.air) {
                                        this.growVines(world, cx, cy, cz - 1, 1);
                                    }

                                    if (rand.nextInt(4) == 0 && world.getBlock(cx, cy, cz + 1) == Blocks.air) {
                                        this.growVines(world, cx, cy, cz + 1, 4);
                                    }
                                }
                            }
                        }
                    }

                    if (rand.nextInt(5) == 0 && height > 5) {
                        for (int i = 0; i < 2; ++i) {
                            for (int side = 0; side < 4; ++side) {
                                if (rand.nextInt(4 - i) == 0) {
                                    int age = rand.nextInt(3);
                                    this.setBlockAndNotifyAdequately(
                                        world,
                                        x + Direction.offsetX[Direction.rotateOpposite[side]],
                                        y + height - 5 + i,
                                        z + Direction.offsetZ[Direction.rotateOpposite[side]],
                                        Blocks.cocoa,
                                        age << 2 | side);
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

    private void growVines(World world, int x, int y, int z, int meta) {
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
