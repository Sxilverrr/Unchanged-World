package com.sxilverr.unchangedworld.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public final class Drops164 {

    private Drops164() {}

    public static Item getItemDropped(Block block, int meta, Random rand, int fortune) {
        if (block == Blocks.tallgrass) {
            return rand.nextInt(8) == 0 ? Items.wheat_seeds : null;
        }

        if (block == Blocks.deadbush) {
            return null;
        }

        return block.getItemDropped(meta, rand, fortune);
    }
}
