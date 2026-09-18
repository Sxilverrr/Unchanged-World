package com.sxilverr.unchangedworld.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

import com.sxilverr.unchangedworld.world.biome.Biome164;

public class MapGenRavine164 extends MapGenBase {

    private final float[] widthScale = new float[1024];

    protected void generateRavine(long seed, int chunkX, int chunkZ, Block[] blocks, double x, double y, double z,
        float radius, float yaw, float pitch, int step, int steps, double heightScale) {
        Random random = new Random(seed);
        double centerX = (double) (chunkX * 16 + 8);
        double centerZ = (double) (chunkZ * 16 + 8);
        float yawDelta = 0.0F;
        float pitchDelta = 0.0F;

        if (steps <= 0) {
            int maxSteps = this.range * 16 - 16;
            steps = maxSteps - random.nextInt(maxSteps / 4);
        }

        boolean single = false;

        if (step == -1) {
            step = steps / 2;
            single = true;
        }

        float scale = 1.0F;

        for (int i = 0; i < 128; ++i) {
            if (i == 0 || random.nextInt(3) == 0) {
                scale = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
            }

            this.widthScale[i] = scale * scale;
        }

        for (; step < steps; ++step) {
            double horizontal = 1.5D
                + (double) (MathHelper.sin((float) step * (float) Math.PI / (float) steps) * radius * 1.0F);
            double vertical = horizontal * heightScale;
            horizontal *= (double) random.nextFloat() * 0.25D + 0.75D;
            vertical *= (double) random.nextFloat() * 0.25D + 0.75D;
            float cosPitch = MathHelper.cos(pitch);
            float sinPitch = MathHelper.sin(pitch);
            x += (double) (MathHelper.cos(yaw) * cosPitch);
            y += (double) sinPitch;
            z += (double) (MathHelper.sin(yaw) * cosPitch);
            pitch *= 0.7F;
            pitch += pitchDelta * 0.05F;
            yaw += yawDelta * 0.05F;
            pitchDelta *= 0.8F;
            yawDelta *= 0.5F;
            pitchDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

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

                                        if ((nx * nx + nz * nz) * (double) this.widthScale[by] + ny * ny / 6.0D
                                            < 1.0D) {
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
        if (this.rand.nextInt(50) == 0) {
            double x = (double) (sourceX * 16 + this.rand.nextInt(16));
            double y = (double) (this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
            double z = (double) (sourceZ * 16 + this.rand.nextInt(16));
            byte count = 1;

            for (int i = 0; i < count; ++i) {
                float yaw = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float radius = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                this.generateRavine(
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
                    3.0D);
            }
        }
    }
}
