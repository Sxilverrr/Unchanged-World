package com.sxilverr.unchangedworld.world.gen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;

public final class Loot164 {

    private static final int[] BOOK_ENCHANTMENT_IDS = { 0, 1, 2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 20, 21, 32, 33, 34, 35,
        48, 49, 50, 51 };
    private static Enchantment[] bookEnchantments;

    private Loot164() {}

    private static Enchantment[] bookEnchantments() {
        if (bookEnchantments == null) {
            List<Enchantment> list = new ArrayList<Enchantment>();

            for (int id : BOOK_ENCHANTMENT_IDS) {
                if (Enchantment.enchantmentsList[id] != null) {
                    list.add(Enchantment.enchantmentsList[id]);
                }
            }

            bookEnchantments = list.toArray(new Enchantment[0]);
        }

        return bookEnchantments;
    }

    public static WeightedRandomChestContent enchantedBook(Random rand) {
        return enchantedBook(rand, 1, 1, 1);
    }

    public static WeightedRandomChestContent enchantedBook(Random rand, int min, int max, int weight) {
        Enchantment[] enchantments = bookEnchantments();
        Enchantment enchantment = enchantments[rand.nextInt(enchantments.length)];
        ItemStack stack = new ItemStack(Items.enchanted_book, 1, 0);
        int level = MathHelper.getRandomIntegerInRange(rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
        Items.enchanted_book.addEnchantment(stack, new EnchantmentData(enchantment, level));
        return new WeightedRandomChestContent(stack, min, max, weight);
    }
}
