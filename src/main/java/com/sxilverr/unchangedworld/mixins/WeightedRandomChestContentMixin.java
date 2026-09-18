package com.sxilverr.unchangedworld.mixins;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sxilverr.unchangedworld.world.gen.Generation164;

@Mixin(WeightedRandomChestContent.class)
public abstract class WeightedRandomChestContentMixin {

    @Inject(method = "generateDispenserContents", at = @At("HEAD"), cancellable = true)
    private static void unchangedworld$generateDispenserContentsLike164(Random rand, WeightedRandomChestContent[] items,
        TileEntityDispenser dispenser, int count, CallbackInfo ci) {
        if (!Generation164.isActive()) {
            return;
        }

        for (int i = 0; i < count; ++i) {
            WeightedRandomChestContent content = (WeightedRandomChestContent) WeightedRandom.getRandomItem(rand, items);
            int amount = content.theMinimumChanceToGenerateItem
                + rand.nextInt(content.theMaximumChanceToGenerateItem - content.theMinimumChanceToGenerateItem + 1);

            if (content.theItemId.getMaxStackSize() >= amount) {
                ItemStack stack = content.theItemId.copy();
                stack.stackSize = amount;
                dispenser.setInventorySlotContents(rand.nextInt(dispenser.getSizeInventory()), stack);
            } else {
                for (int j = 0; j < amount; ++j) {
                    ItemStack stack = content.theItemId.copy();
                    stack.stackSize = 1;
                    dispenser.setInventorySlotContents(rand.nextInt(dispenser.getSizeInventory()), stack);
                }
            }
        }

        ci.cancel();
    }
}
