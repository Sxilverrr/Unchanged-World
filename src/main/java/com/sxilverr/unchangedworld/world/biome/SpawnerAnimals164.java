package com.sxilverr.unchangedworld.world.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public final class SpawnerAnimals164 {

    private static final float SPAWNING_CHANCE = 0.1F;
    private static final List<SpawnListEntry> DEFAULT = Collections.unmodifiableList(
        Arrays.asList(
            new SpawnListEntry(EntitySheep.class, 12, 4, 4),
            new SpawnListEntry(EntityPig.class, 10, 4, 4),
            new SpawnListEntry(EntityChicken.class, 10, 4, 4),
            new SpawnListEntry(EntityCow.class, 8, 4, 4)));
    @SuppressWarnings("unchecked")
    private static final List<SpawnListEntry>[] TABLE = new List[BiomeIds164.count()];

    static {
        List<SpawnListEntry> none = Collections.emptyList();
        put(BiomeGenBase.ocean, none);
        put(BiomeGenBase.frozenOcean, none);
        put(BiomeGenBase.river, none);
        put(BiomeGenBase.frozenRiver, none);
        put(BiomeGenBase.desert, none);
        put(BiomeGenBase.desertHills, none);
        put(BiomeGenBase.beach, none);
        put(BiomeGenBase.plains, with(new SpawnListEntry(EntityHorse.class, 5, 2, 6)));
        put(BiomeGenBase.forest, with(new SpawnListEntry(EntityWolf.class, 5, 4, 4)));
        put(BiomeGenBase.forestHills, with(new SpawnListEntry(EntityWolf.class, 5, 4, 4)));
        put(BiomeGenBase.taiga, with(new SpawnListEntry(EntityWolf.class, 8, 4, 4)));
        put(BiomeGenBase.taigaHills, with(new SpawnListEntry(EntityWolf.class, 8, 4, 4)));
        put(BiomeGenBase.jungle, with(new SpawnListEntry(EntityChicken.class, 10, 4, 4)));
        put(BiomeGenBase.jungleHills, with(new SpawnListEntry(EntityChicken.class, 10, 4, 4)));
        List<SpawnListEntry> mushroom = Collections.singletonList(new SpawnListEntry(EntityMooshroom.class, 8, 4, 8));
        put(BiomeGenBase.mushroomIsland, mushroom);
        put(BiomeGenBase.mushroomIslandShore, mushroom);
    }

    private SpawnerAnimals164() {}

    private static List<SpawnListEntry> with(SpawnListEntry extra) {
        List<SpawnListEntry> list = new ArrayList<SpawnListEntry>(DEFAULT);
        list.add(extra);
        return Collections.unmodifiableList(list);
    }

    private static void put(BiomeGenBase biome, List<SpawnListEntry> list) {
        TABLE[biome.biomeID] = list;
    }

    public static List<SpawnListEntry> creatures(BiomeGenBase biome) {
        if (biome != null && Biome164.find(biome) == null) {
            return biome.getSpawnableList(EnumCreatureType.creature);
        }

        List<SpawnListEntry> list = biome == null ? null : TABLE[biome.biomeID];
        return list == null ? DEFAULT : list;
    }

    public static void performWorldGenSpawning(World world, BiomeGenBase biome, int x, int z, int width, int length,
        Random rand) {
        List<SpawnListEntry> list = creatures(biome);

        if (list.isEmpty()) {
            return;
        }

        while (rand.nextFloat() < SPAWNING_CHANCE) {
            SpawnListEntry entry = (SpawnListEntry) WeightedRandom.getRandomItem(world.rand, list);
            IEntityLivingData data = null;
            int count = entry.minGroupCount + rand.nextInt(1 + entry.maxGroupCount - entry.minGroupCount);
            int spawnX = x + rand.nextInt(width);
            int spawnZ = z + rand.nextInt(length);
            int centerX = spawnX;
            int centerZ = spawnZ;

            for (int i = 0; i < count; ++i) {
                boolean spawned = false;

                for (int attempt = 0; !spawned && attempt < 4; ++attempt) {
                    int spawnY = world.getTopSolidOrLiquidBlock(spawnX, spawnZ);

                    if (SpawnerAnimals
                        .canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, world, spawnX, spawnY, spawnZ)) {
                        float posX = (float) spawnX + 0.5F;
                        float posY = (float) spawnY;
                        float posZ = (float) spawnZ + 0.5F;
                        EntityLiving entity;

                        try {
                            entity = (EntityLiving) entry.entityClass.getConstructor(new Class[] { World.class })
                                .newInstance(new Object[] { world });
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }

                        entity.setLocationAndAngles(
                            (double) posX,
                            (double) posY,
                            (double) posZ,
                            rand.nextFloat() * 360.0F,
                            0.0F);
                        world.spawnEntityInWorld(entity);
                        data = entity.onSpawnWithEgg(data);
                        spawned = true;
                    }

                    spawnX += rand.nextInt(5) - rand.nextInt(5);

                    for (spawnZ += rand.nextInt(5) - rand.nextInt(5); spawnX < x || spawnX >= x + width
                        || spawnZ < z
                        || spawnZ >= z + width; spawnZ = centerZ + rand.nextInt(5) - rand.nextInt(5)) {
                        spawnX = centerX + rand.nextInt(5) - rand.nextInt(5);
                    }
                }
            }
        }
    }
}
