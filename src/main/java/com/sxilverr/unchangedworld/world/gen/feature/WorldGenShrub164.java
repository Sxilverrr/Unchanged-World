package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenShrub164 extends WorldGenerator {

    private final int woodMetadata;
    private final int leavesMetadata;

    public WorldGenShrub164(int woodMetadata, int leavesMetadata) {
        this.woodMetadata = woodMetadata;
        this.leavesMetadata = leavesMetadata;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        Block current;

        while (((current = world.getBlock(x, y, z)) == Blocks.air || current == Blocks.leaves) && y > 0) {
            --y;
        }

        Block soil = world.getBlock(x, y, z);

        if (soil == Blocks.dirt || soil == Blocks.grass) {
            ++y;
            this.setBlockAndNotifyAdequately(world, x, y, z, Blocks.log, this.woodMetadata);

            for (int cy = y; cy <= y + 2; ++cy) {
                int radius = 2 - (cy - y);

                for (int cx = x - radius; cx <= x + radius; ++cx) {
                    int dx = cx - x;

                    for (int cz = z - radius; cz <= z + radius; ++cz) {
                        int dz = cz - z;

                        if ((Math.abs(dx) != radius || Math.abs(dz) != radius || rand.nextInt(2) != 0)
                            && !world.getBlock(cx, cy, cz)
                                .func_149730_j()) {
                            this.setBlockAndNotifyAdequately(world, cx, cy, cz, Blocks.leaves, this.leavesMetadata);
                        }
                    }
                }
            }
        }

        return true;
    }
}
