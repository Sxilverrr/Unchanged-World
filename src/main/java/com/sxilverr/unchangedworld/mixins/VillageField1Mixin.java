package com.sxilverr.unchangedworld.mixins;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.sxilverr.unchangedworld.world.gen.Generation164;

@Mixin(StructureVillagePieces.Field1.class)
public abstract class VillageField1Mixin {

    @ModifyArgs(
        method = "addComponentParts",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Field1;fillWithBlocks(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;IIIIIILnet/minecraft/block/Block;Lnet/minecraft/block/Block;Z)V"))
    private void unchangedworld$flowingFarmWaterLike164(Args args) {
        if (Generation164.isActive()) {
            for (int i = 8; i <= 9; ++i) {
                if ((Block) args.get(i) == Blocks.water) {
                    args.set(i, Blocks.flowing_water);
                }
            }
        }
    }
}
