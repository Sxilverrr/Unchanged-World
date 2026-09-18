package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenHugeTrees164 extends WorldGenerator {

    private final int baseHeight;
    private final int woodMetadata;
    private final int leavesMetadata;

    public WorldGenHugeTrees164(boolean notify, int baseHeight, int woodMetadata, int leavesMetadata) {
        super(notify);
        this.baseHeight = baseHeight;
        this.woodMetadata = woodMetadata;
        this.leavesMetadata = leavesMetadata;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(3) + this.baseHeight;
        boolean canGrow = true;

        if (y >= 1 && y + height + 1 <= 256) {
            for (int cy = y; cy <= y + 1 + height; ++cy) {
                byte radius = 2;

                if (cy == y) {
                    radius = 1;
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
                                && block != Blocks.log
                                && block != Blocks.sapling) {
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
                world.setBlock(x, y - 1, z, Blocks.dirt, 0, 2);
                world.setBlock(x + 1, y - 1, z, Blocks.dirt, 0, 2);
                world.setBlock(x, y - 1, z + 1, Blocks.dirt, 0, 2);
                world.setBlock(x + 1, y - 1, z + 1, Blocks.dirt, 0, 2);
                this.growLeaves(world, x, z, y + height, 2, rand);

                for (int branchY = y + height - 2 - rand.nextInt(4); branchY
                    > y + height / 2; branchY -= 2 + rand.nextInt(4)) {
                    float angle = rand.nextFloat() * (float) Math.PI * 2.0F;
                    int branchX = x + (int) (0.5F + MathHelper.cos(angle) * 4.0F);
                    int branchZ = z + (int) (0.5F + MathHelper.sin(angle) * 4.0F);
                    this.growLeaves(world, branchX, branchZ, branchY, 0, rand);

                    for (int i = 0; i < 5; ++i) {
                        branchX = x + (int) (1.5F + MathHelper.cos(angle) * (float) i);
                        branchZ = z + (int) (1.5F + MathHelper.sin(angle) * (float) i);
                        this.setBlockAndNotifyAdequately(
                            world,
                            branchX,
                            branchY - 3 + i / 2,
                            branchZ,
                            Blocks.log,
                            this.woodMetadata);
                    }
                }

                for (int cy = 0; cy < height; ++cy) {
                    Block block = world.getBlock(x, y + cy, z);

                    if (block == Blocks.air || block == Blocks.leaves) {
                        this.setBlockAndNotifyAdequately(world, x, y + cy, z, Blocks.log, this.woodMetadata);

                        if (cy > 0) {
                            if (rand.nextInt(3) > 0 && world.isAirBlock(x - 1, y + cy, z)) {
                                this.setBlockAndNotifyAdequately(world, x - 1, y + cy, z, Blocks.vine, 8);
                            }

                            if (rand.nextInt(3) > 0 && world.isAirBlock(x, y + cy, z - 1)) {
                                this.setBlockAndNotifyAdequately(world, x, y + cy, z - 1, Blocks.vine, 1);
                            }
                        }
                    }

                    if (cy < height - 1) {
                        block = world.getBlock(x + 1, y + cy, z);

                        if (block == Blocks.air || block == Blocks.leaves) {
                            this.setBlockAndNotifyAdequately(world, x + 1, y + cy, z, Blocks.log, this.woodMetadata);

                            if (cy > 0) {
                                if (rand.nextInt(3) > 0 && world.isAirBlock(x + 2, y + cy, z)) {
                                    this.setBlockAndNotifyAdequately(world, x + 2, y + cy, z, Blocks.vine, 2);
                                }

                                if (rand.nextInt(3) > 0 && world.isAirBlock(x + 1, y + cy, z - 1)) {
                                    this.setBlockAndNotifyAdequately(world, x + 1, y + cy, z - 1, Blocks.vine, 1);
                                }
                            }
                        }

                        block = world.getBlock(x + 1, y + cy, z + 1);

                        if (block == Blocks.air || block == Blocks.leaves) {
                            this.setBlockAndNotifyAdequately(
                                world,
                                x + 1,
                                y + cy,
                                z + 1,
                                Blocks.log,
                                this.woodMetadata);

                            if (cy > 0) {
                                if (rand.nextInt(3) > 0 && world.isAirBlock(x + 2, y + cy, z + 1)) {
                                    this.setBlockAndNotifyAdequately(world, x + 2, y + cy, z + 1, Blocks.vine, 2);
                                }

                                if (rand.nextInt(3) > 0 && world.isAirBlock(x + 1, y + cy, z + 2)) {
                                    this.setBlockAndNotifyAdequately(world, x + 1, y + cy, z + 2, Blocks.vine, 4);
                                }
                            }
                        }

                        block = world.getBlock(x, y + cy, z + 1);

                        if (block == Blocks.air || block == Blocks.leaves) {
                            this.setBlockAndNotifyAdequately(world, x, y + cy, z + 1, Blocks.log, this.woodMetadata);

                            if (cy > 0) {
                                if (rand.nextInt(3) > 0 && world.isAirBlock(x - 1, y + cy, z + 1)) {
                                    this.setBlockAndNotifyAdequately(world, x - 1, y + cy, z + 1, Blocks.vine, 8);
                                }

                                if (rand.nextInt(3) > 0 && world.isAirBlock(x, y + cy, z + 2)) {
                                    this.setBlockAndNotifyAdequately(world, x, y + cy, z + 2, Blocks.vine, 4);
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

    private void growLeaves(World world, int x, int z, int y, int extraRadius, Random rand) {
        byte layers = 2;

        for (int cy = y - layers; cy <= y; ++cy) {
            int layer = cy - y;
            int radius = extraRadius + 1 - layer;

            for (int cx = x - radius; cx <= x + radius + 1; ++cx) {
                int dx = cx - x;

                for (int cz = z - radius; cz <= z + radius + 1; ++cz) {
                    int dz = cz - z;

                    if ((dx >= 0 || dz >= 0 || dx * dx + dz * dz <= radius * radius)
                        && (dx <= 0 && dz <= 0 || dx * dx + dz * dz <= (radius + 1) * (radius + 1))
                        && (rand.nextInt(4) != 0 || dx * dx + dz * dz <= (radius - 1) * (radius - 1))) {
                        Block block = world.getBlock(cx, cy, cz);

                        if (block == Blocks.air || block == Blocks.leaves) {
                            this.setBlockAndNotifyAdequately(world, cx, cy, cz, Blocks.leaves, this.leavesMetadata);
                        }
                    }
                }
            }
        }
    }
}
