package com.sxilverr.unchangedworld.mixins;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sxilverr.unchangedworld.world.gen.Generation164;

@Mixin(BlockDoublePlant.class)
public abstract class BlockDoublePlantMixin {

    @Inject(method = "func_149889_c", at = @At("HEAD"), cancellable = true)
    private void unchangedworld$noDoubleTallGrass(World world, int x, int y, int z, int type, int flags,
        CallbackInfo ci) {
        if (type == 2 && Generation164.noDoubleTallGrass()) {
            ci.cancel();
        }
    }
}
