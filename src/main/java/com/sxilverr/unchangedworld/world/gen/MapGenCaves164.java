package com.sxilverr.unchangedworld.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

import com.sxilverr.unchangedworld.world.biome.Biome164;

public class MapGenCaves164 extends MapGenBase {

    protected void generateLargeCaveNode(long seed, int chunkX, int chunkZ, Block[] blocks, double x, double y,
        double z) {
        this.generateCaveNode(
            seed,
            chunkX,
            chunkZ,
            blocks,
            x,
            y,
            z,
            1.0F + this.rand.nextFloat() * 6.0F,
            0.0F,
            0.0F,
            -1,
            -1,
            0.5D);
    }

    protected void generateCaveNode(long seed, int chunkX, int chunkZ, Block[] blocks, double x, double y, double z,
        float radius, float yaw, float pitch, int step, int steps, double heightScale) {
        double centerX = (double) (chunkX * 16 + 8);
        double centerZ = (double) (chunkZ * 16 + 8);
        float yawDelta = 0.0F;
        float pitchDelta = 0.0F;
        Random random = new Random(seed);

        if (steps <= 0) {
            int maxSteps = this.range * 16 - 16;
            steps = maxSteps - random.nextInt(maxSteps / 4);
        }

        boolean single = false;

        if (step == -1) {
            step = steps / 2;
            single = true;
        }

        int branchStep = random.nextInt(steps / 2) + steps / 4;

        for (boolean steep = random.nextInt(6) == 0; step < steps; ++step) {
            double horizontal = 1.5D
                + (double) (MathHelper.sin((float) step * (float) Math.PI / (float) steps) * radius * 1.0F);
            double vertical = horizontal * heightScale;
            float cosPitch = MathHelper.cos(pitch);
            float sinPitch = MathHelper.sin(pitch);
            x += (double) (MathHelper.cos(yaw) * cosPitch);
            y += (double) sinPitch;
            z += (double) (MathHelper.sin(yaw) * cosPitch);

            if (steep) {
                pitch *= 0.92F;
            } else {
                pitch *= 0.7F;
            }

            pitch += pitchDelta * 0.1F;
            yaw += yawDelta * 0.1F;
            pitchDelta *= 0.9F;
            yawDelta *= 0.75F;
            pitchDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!single && step == branchStep && radius > 1.0F && steps > 0) {
                this.generateCaveNode(
                    random.nextLong(),
                    chunkX,
                    chunkZ,
                    blocks,
                    x,
                    y,
                    z,
                    random.nextFloat() * 0.5F + 0.5F,
                    yaw - ((float) Math.PI / 2F),
                    pitch / 3.0F,
                    step,
                    steps,
                    1.0D);
                this.generateCaveNode(
                    random.nextLong(),
                    chunkX,
                    chunkZ,
                    blocks,
                    x,
                    y,
                    z,
                    random.nextFloat() * 0.5F + 0.5F,
                    yaw + ((float) Math.PI / 2F),
                    pitch / 3.0F,
                    step,
                    steps,
                    1.0D);
                return;
            }

            if (single || random.nextInt(4) != 0) {
                double offsetX = x - centerX;
                double offsetZ = z - centerZ;
                double remaining = (double) (steps - step);
                double reach = (double) (radius + 2.0F + 16.0F);

                if (offsetX * offsetX + offsetZ * offsetZ - remaining * remaining > reach * reach) {
                    return;
                }

                if (x >= centerX - 16.0D - horizontal * 2.0D && z >= centerZ - 16.0D - horizontal * 2.0D
                    && x <= centerX + 16.0D + horizontal * 2.0D
                    && z <= centerZ + 16.0D + horizontal * 2.0D) {
                    int minX = MathHelper.floor_double(x - horizontal) - chunkX * 16 - 1;
                    int maxX = MathHelper.floor_double(x + horizontal) - chunkX * 16 + 1;
                    int minY = MathHelper.floor_double(y - vertical) - 1;
                    int maxY = MathHelper.floor_double(y + vertical) + 1;
                    int minZ = MathHelper.floor_double(z - horizontal) - chunkZ * 16 - 1;
                    int maxZ = MathHelper.floor_double(z + horizontal) - chunkZ * 16 + 1;

                    if (minX < 0) {
                        minX = 0;
                    }

                    if (maxX > 16) {
                        maxX = 16;
                    }

                    if (minY < 1) {
                        minY = 1;
                    }

                    if (maxY > 120) {
                        maxY = 120;
                    }

                    if (minZ < 0) {
                        minZ = 0;
                    }

                    if (maxZ > 16) {
                        maxZ = 16;
                    }

                    boolean water = false;

                    for (int bx = minX; !water && bx < maxX; ++bx) {
                        for (int bz = minZ; !water && bz < maxZ; ++bz) {
                            for (int by = maxY + 1; !water && by >= minY - 1; --by) {
                                int index = (bx * 16 + bz) * 256 + by;

                                if (by >= 0 && by < 128) {
                                    Block block = blocks[index];

                                    if (block == Blocks.flowing_water || block == Blocks.water) {
                                        water = true;
                                    }

                                    if (by != minY - 1 && bx != minX
                                        && bx != maxX - 1
                                        && bz != minZ
                                        && bz != maxZ - 1) {
                                        by = minY;
                                    }
                                }
                            }
                        }
                    }

                    if (!water) {
                        for (int bx = minX; bx < maxX; ++bx) {
                            double nx = ((double) (bx + chunkX * 16) + 0.5D - x) / horizontal;

                            for (int bz = minZ; bz < maxZ; ++bz) {
                                double nz = ((double) (bz + chunkZ * 16) + 0.5D - z) / horizontal;
                                int index = (bx * 16 + bz) * 256 + maxY;
                                boolean foundGrass = false;

                                if (nx * nx + nz * nz < 1.0D) {
                                    for (int by = maxY - 1; by >= minY; --by) {
                                        double ny = ((double) by + 0.5D - y) / vertical;

                                        if (ny > -0.7D && nx * nx + ny * ny + nz * nz < 1.0D) {
                                            Block block = blocks[index];

                                            if (block == Blocks.grass) {
                                                foundGrass = true;
                                            }

                                            if (block == Blocks.stone || block == Blocks.dirt
                                                || block == Blocks.grass) {
                                                if (by < 10) {
                                                    blocks[index] = Blocks.flowing_lava;
                                                } else {
                                                    blocks[index] = null;

                                                    if (foundGrass && blocks[index - 1] == Blocks.dirt) {
                                                        blocks[index - 1] = Biome164.get(
                                                            this.worldObj.getBiomeGenForCoords(
                                                                bx + chunkX * 16,
                                                                bz + chunkZ * 16)).topBlock;
                                                    }
                                                }
                                            }
                                        }

                                        --index;
                                    }
                                }
                            }
                        }

                        if (single) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void func_151538_a(World world, int sourceX, int sourceZ, int chunkX, int chunkZ, Block[] blocks) {
        int count = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);

        if (this.rand.nextInt(15) != 0) {
            count = 0;
        }

        for (int i = 0; i < count; ++i) {
            double x = (double) (sourceX * 16 + this.rand.nextInt(16));
            double y = (double) this.rand.nextInt(this.rand.nextInt(120) + 8);
            double z = (double) (sourceZ * 16 + this.rand.nextInt(16));
            int nodes = 1;

            if (this.rand.nextInt(4) == 0) {
                this.generateLargeCaveNode(this.rand.nextLong(), chunkX, chunkZ, blocks, x, y, z);
                nodes += this.rand.nextInt(4);
            }

            for (int j = 0; j < nodes; ++j) {
                float yaw = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float radius = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0) {
                    radius *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.generateCaveNode(
                    this.rand.nextLong(),
                    chunkX,
                    chunkZ,
                    blocks,
                    x,
                    y,
                    z,
                    radius,
                    yaw,
                    pitch,
                    0,
                    0,
                    1.0D);
            }
        }
    }
}
