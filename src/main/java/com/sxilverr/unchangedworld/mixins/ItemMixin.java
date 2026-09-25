package com.sxilverr.unchangedworld.mixins;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.sxilverr.unchangedworld.world.gen.Generation164;
import com.sxilverr.unchangedworld.world.gen.feature.Loot164;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getChestGenBase", at = @At("HEAD"), cancellable = true, remap = false)
    private void unchangedworld$rollEnchantedBookLike164(ChestGenHooks chest, Random rand,
        WeightedRandomChestContent original, CallbackInfoReturnable<WeightedRandomChestContent> cir) {
        if (Generation164.isBooks164() && (Object) this instanceof ItemEnchantedBook) {
            cir.setReturnValue(
                Loot164.enchantedBook(
                    rand,
                    original.theMinimumChanceToGenerateItem,
                    original.theMaximumChanceToGenerateItem,
                    original.itemWeight));
        }
    }
}
