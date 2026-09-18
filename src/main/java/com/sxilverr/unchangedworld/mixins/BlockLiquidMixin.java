package com.sxilverr.unchangedworld.mixins;

import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sxilverr.unchangedworld.world.gen.Generation164;

@Mixin(BlockLiquid.class)
public abstract class BlockLiquidMixin {

    @Inject(method = "func_149799_m", at = @At("HEAD"), cancellable = true)
    private void unchangedworld$noLavaMixEffectsInAirLike164(World world, int x, int y, int z, CallbackInfo ci) {
        if (Generation164.isActive() && world.getBlock(x, y, z) == Blocks.air) {
            ci.cancel();
        }
    }
}
