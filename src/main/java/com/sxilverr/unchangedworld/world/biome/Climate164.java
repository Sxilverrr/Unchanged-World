package com.sxilverr.unchangedworld.world.biome;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public final class Climate164 {

    private Climate164() {}

    public static float getTemperature(World world, int x, int z) {
        return Biome164.get(world.getBiomeGenForCoords(x, z)).temperature;
    }

    public static boolean canBlockFreeze(World world, int x, int y, int z, boolean byWater) {
        if (getTemperature(world, x, z) > 0.15F) {
            return false;
        }

        if (y >= 0 && y < 256 && world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
            Block block = world.getBlock(x, y, z);

            if ((block == Blocks.water || block == Blocks.flowing_water) && world.getBlockMetadata(x, y, z) == 0) {
                if (!byWater) {
                    return true;
                }

                boolean surrounded = world.getBlock(x - 1, y, z)
                    .getMaterial() == Material.water
                    && world.getBlock(x + 1, y, z)
                        .getMaterial() == Material.water
                    && world.getBlock(x, y, z - 1)
                        .getMaterial() == Material.water
                    && world.getBlock(x, y, z + 1)
                        .getMaterial() == Material.water;

                if (!surrounded) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean canSnowAt(World world, int x, int y, int z) {
        if (getTemperature(world, x, z) > 0.15F) {
            return false;
        }

        if (y >= 0 && y < 256 && world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
            Block below = world.getBlock(x, y - 1, z);
            Block block = world.getBlock(x, y, z);

            if (block == Blocks.air && Blocks.snow_layer.canPlaceBlockAt(world, x, y, z)
                && below != Blocks.air
                && below != Blocks.ice
                && below.getMaterial()
                    .blocksMovement()) {
                return true;
            }
        }

        return false;
    }
}
