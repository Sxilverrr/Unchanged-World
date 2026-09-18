package com.sxilverr.unchangedworld.mixins;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sxilverr.unchangedworld.world.WorldTypeDefault164;

@Mixin(Chunk.class)
public abstract class ChunkMixin {

    @Shadow
    public World worldObj;

    @Shadow
    public boolean isTerrainPopulated;

    @Shadow
    public boolean[] updateSkylightColumns;

    @Shadow
    private boolean isGapLightingUpdated;

    @Shadow
    private ExtendedBlockStorage[] storageArrays;

    @Shadow
    public abstract int getTopFilledSegment();

    @Shadow
    public abstract int func_150808_b(int x, int y, int z);

    private boolean unchangedworld$is164World() {
        return this.worldObj.getWorldInfo()
            .getTerrainType() instanceof WorldTypeDefault164;
    }

    @Inject(method = "func_150809_p", at = @At("HEAD"), cancellable = true)
    private void unchangedworld$deferLightPopulation(CallbackInfo ci) {
        if (!this.isTerrainPopulated && this.unchangedworld$is164World()) {
            this.isTerrainPopulated = true;
            ci.cancel();
        }
    }

    @Inject(method = "generateSkylightMap", at = @At("RETURN"))
    private void unchangedworld$generateSkylightMapLike164(CallbackInfo ci) {
        if (!this.unchangedworld$is164World()) {
            return;
        }

        int top = this.getTopFilledSegment();

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                if (!this.worldObj.provider.hasNoSky) {
                    int light = 15;
                    int y = top + 16 - 1;

                    do {
                        light -= this.func_150808_b(x, y, z);

                        if (light > 0) {
                            ExtendedBlockStorage section = this.storageArrays[y >> 4];

                            if (section != null) {
                                section.setExtSkylightValue(x, y & 15, z, light);
                            }
                        }

                        --y;
                    } while (y > 0 && light > 0);
                }

                this.updateSkylightColumns[x + z * 16] = true;
            }
        }

        this.isGapLightingUpdated = true;
    }
}
