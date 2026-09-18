package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenWaterlily164 extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        for (int i = 0; i < 10; ++i) {
            int px = x + rand.nextInt(8) - rand.nextInt(8);
            int py = y + rand.nextInt(4) - rand.nextInt(4);
            int pz = z + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(px, py, pz) && world.getBlock(px, py - 1, pz) == Blocks.water) {
                world.setBlock(px, py, pz, Blocks.waterlily, 0, 2);
            }
        }

        return true;
    }
}
