package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenTallGrass164 extends WorldGenerator {

    private final Block block;
    private final int metadata;

    public WorldGenTallGrass164(Block block, int metadata) {
        this.block = block;
        this.metadata = metadata;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        Block current;

        while (((current = world.getBlock(x, y, z)) == Blocks.air || current == Blocks.leaves) && y > 0) {
            --y;
        }

        for (int i = 0; i < 128; ++i) {
            int px = x + rand.nextInt(8) - rand.nextInt(8);
            int py = y + rand.nextInt(4) - rand.nextInt(4);
            int pz = z + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(px, py, pz) && Plant164.canFlowerStay(world, px, py, pz)) {
                world.setBlock(px, py, pz, this.block, this.metadata, 2);
            }
        }

        return true;
    }
}
