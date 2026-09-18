package com.sxilverr.unchangedworld.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public final class Plant164 {

    private Plant164() {}

    public static boolean canFlowerStay(World world, int x, int y, int z) {
        return isLit(world, x, y, z) && isFlowerSoil(world.getBlock(x, y - 1, z));
    }

    public static boolean canDeadBushStay(World world, int x, int y, int z) {
        return isLit(world, x, y, z) && world.getBlock(x, y - 1, z) == Blocks.sand;
    }

    public static boolean canMushroomStay(World world, int x, int y, int z) {
        if (y < 0 || y >= 256) {
            return false;
        }

        Block soil = world.getBlock(x, y - 1, z);
        return soil == Blocks.mycelium || world.getFullBlockLightValue(x, y, z) < 13 && soil.func_149730_j();
    }

    private static boolean isLit(World world, int x, int y, int z) {
        return world.getFullBlockLightValue(x, y, z) >= 8 || world.canBlockSeeTheSky(x, y, z);
    }

    private static boolean isFlowerSoil(Block block) {
        return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland;
    }
}
