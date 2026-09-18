package com.sxilverr.unchangedworld.mixins;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sxilverr.unchangedworld.world.gen.Drops164;
import com.sxilverr.unchangedworld.world.gen.Generation164;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Shadow
    protected abstract void dropBlockAsItem(World world, int x, int y, int z, ItemStack stack);

    @Inject(method = "dropBlockAsItemWithChance", at = @At("HEAD"), cancellable = true)
    private void unchangedworld$dropLike164(World world, int x, int y, int z, int meta, float chance, int fortune,
        CallbackInfo ci) {
        if (!Generation164.isActive()) {
            return;
        }

        ci.cancel();

        if (world.isRemote) {
            return;
        }

        Block block = (Block) (Object) this;
        int count = block.quantityDroppedWithBonus(fortune, world.rand);

        for (int i = 0; i < count; ++i) {
            if (world.rand.nextFloat() <= chance) {
                Item item = Drops164.getItemDropped(block, meta, world.rand, fortune);

                if (item != null) {
                    this.dropBlockAsItem(world, x, y, z, new ItemStack(item, 1, block.damageDropped(meta)));
                }
            }
        }
    }
}
