package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenDungeons164 extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        byte height = 3;
        int radiusX = rand.nextInt(2) + 2;
        int radiusZ = rand.nextInt(2) + 2;
        int openings = 0;

        for (int cx = x - radiusX - 1; cx <= x + radiusX + 1; ++cx) {
            for (int cy = y - 1; cy <= y + height + 1; ++cy) {
                for (int cz = z - radiusZ - 1; cz <= z + radiusZ + 1; ++cz) {
                    Material material = world.getBlock(cx, cy, cz)
                        .getMaterial();

                    if (cy == y - 1 && !material.isSolid()) {
                        return false;
                    }

                    if (cy == y + height + 1 && !material.isSolid()) {
                        return false;
                    }

                    if ((cx == x - radiusX - 1 || cx == x + radiusX + 1
                        || cz == z - radiusZ - 1
                        || cz == z + radiusZ + 1) && cy == y
                        && world.isAirBlock(cx, cy, cz)
                        && world.isAirBlock(cx, cy + 1, cz)) {
                        ++openings;
                    }
                }
            }
        }

        if (openings < 1 || openings > 5) {
            return false;
        }

        for (int cx = x - radiusX - 1; cx <= x + radiusX + 1; ++cx) {
            for (int cy = y + height; cy >= y - 1; --cy) {
                for (int cz = z - radiusZ - 1; cz <= z + radiusZ + 1; ++cz) {
                    if (cx != x - radiusX - 1 && cy != y - 1
                        && cz != z - radiusZ - 1
                        && cx != x + radiusX + 1
                        && cy != y + height + 1
                        && cz != z + radiusZ + 1) {
                        world.setBlockToAir(cx, cy, cz);
                    } else if (cy >= 0 && !world.getBlock(cx, cy - 1, cz)
                        .getMaterial()
                        .isSolid()) {
                            world.setBlockToAir(cx, cy, cz);
                        } else if (world.getBlock(cx, cy, cz)
                            .getMaterial()
                            .isSolid()) {
                                if (cy == y - 1 && rand.nextInt(4) != 0) {
                                    world.setBlock(cx, cy, cz, Blocks.mossy_cobblestone, 0, 2);
                                } else {
                                    world.setBlock(cx, cy, cz, Blocks.cobblestone, 0, 2);
                                }
                            }
                }
            }
        }

        for (int chest = 0; chest < 2; ++chest) {
            for (int attempt = 0; attempt < 3; ++attempt) {
                int cx = x + rand.nextInt(radiusX * 2 + 1) - radiusX;
                int cz = z + rand.nextInt(radiusZ * 2 + 1) - radiusZ;

                if (world.isAirBlock(cx, y, cz)) {
                    int walls = 0;

                    if (world.getBlock(cx - 1, y, cz)
                        .getMaterial()
                        .isSolid()) {
                        ++walls;
                    }

                    if (world.getBlock(cx + 1, y, cz)
                        .getMaterial()
                        .isSolid()) {
                        ++walls;
                    }

                    if (world.getBlock(cx, y, cz - 1)
                        .getMaterial()
                        .isSolid()) {
                        ++walls;
                    }

                    if (world.getBlock(cx, y, cz + 1)
                        .getMaterial()
                        .isSolid()) {
                        ++walls;
                    }

                    if (walls == 1) {
                        world.setBlock(cx, y, cz, Blocks.chest, 0, 2);
                        WeightedRandomChestContent[] items = WeightedRandomChestContent.func_92080_a(
                            WorldGenDungeons.field_111189_a,
                            new WeightedRandomChestContent[] { Loot164.enchantedBook(rand) });
                        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(cx, y, cz);

                        if (tileChest != null) {
                            WeightedRandomChestContent.generateChestContents(rand, items, tileChest, 8);
                        }

                        break;
                    }
                }
            }
        }

        world.setBlock(x, y, z, Blocks.mob_spawner, 0, 2);
        TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(x, y, z);

        if (spawner != null) {
            spawner.func_145881_a()
                .setEntityName(this.pickMobSpawner(rand));
        } else {
            System.err.println("Failed to fetch mob spawner entity at (" + x + ", " + y + ", " + z + ")");
        }

        return true;
    }

    private String pickMobSpawner(Random rand) {
        int roll = rand.nextInt(4);
        return roll == 0 ? "Skeleton" : (roll == 1 ? "Zombie" : (roll == 2 ? "Zombie" : (roll == 3 ? "Spider" : "")));
    }
}
