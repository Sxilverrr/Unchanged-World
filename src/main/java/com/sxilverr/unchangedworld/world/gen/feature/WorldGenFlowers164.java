package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenFlowers164 extends WorldGenerator {

    private final Block block;

    public WorldGenFlowers164(Block block) {
        this.block = block;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        for (int i = 0; i < 64; ++i) {
            int px = x + rand.nextInt(8) - rand.nextInt(8);
            int py = y + rand.nextInt(4) - rand.nextInt(4);
            int pz = z + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(px, py, pz) && (!world.provider.hasNoSky || py < 127)
                && this.canStay(world, px, py, pz)) {
                world.setBlock(px, py, pz, this.block, 0, 2);
            }
        }

        return true;
    }

    private boolean canStay(World world, int x, int y, int z) {
        if (this.block == Blocks.brown_mushroom || this.block == Blocks.red_mushroom) {
            return Plant164.canMushroomStay(world, x, y, z);
        }

        return Plant164.canFlowerStay(world, x, y, z);
    }
}
