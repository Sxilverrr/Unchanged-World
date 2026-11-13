package com.sxilverr.unchangedworld.world;

import java.io.File;
import java.util.Arrays;

import net.minecraftforge.common.config.Configuration;

public final class IntegratedConfig164 {

    public static final String CATEGORY = "integrated";
    private static final String BIOMES = "integrated.biomes";
    private static final String SURFACE = "integrated.surface";
    private static final String DECORATION = "integrated.decoration";
    private static final String TREES = "integrated.trees";
    private static final String V164 = "1.6.4";
    private static final String V1710 = "1.7.10";
    private static final String[] VERSIONS = { V164, V1710 };
    private static final String[] BIOME_SIZES = { "0.5x", "1x (Vanilla)", "2x", "4x (Large Biomes)", "8x" };

    private static Configuration config;
    private static Settings164 settings = new Settings164();

    private IntegratedConfig164() {}

    public static Settings164 settings() {
        return settings;
    }

    public static Configuration config() {
        return config;
    }

    public static void load(File file) {
        config = new Configuration(file);
        reload();
    }

    public static void reload() {
        Settings164 loaded = new Settings164();
        config.setCategoryComment(CATEGORY, "Integrated 1.6.4 world type. Changes only affect newly generated chunks.");
        config.setCategoryComment(BIOMES, "Options for the 1.6.4 biome layout.");
        config.setCategoryComment(SURFACE, "1.7.10 surface details, used with 1.6.4 surface rules.");
        config.setCategoryComment(DECORATION, "1.7.10 plants added to the 1.6.4 biomes.");
        config.setCategoryComment(TREES, "1.7.10 tree selection for the 1.6.4 biomes.");

        loaded.layout1710 = is1710(
            CATEGORY,
            "biomeLayout",
            "Biome layout. 1.6.4 has large continents and the 1.6.4 biomes, 1.7.10 has climate zones and all 1.7.10 biomes.");
        loaded.terrain1710 = is1710(
            CATEGORY,
            "terrain",
            "Terrain generator. With 1.7.10 only the biome layout, lighting and biome options still apply.");
        loaded.caves1710 = is1710(CATEGORY, "caves", "Caves.");
        loaded.ravines1710 = is1710(CATEGORY, "ravines", "Ravines.");
        loaded.lighting1710 = is1710(CATEGORY, "lighting", "Chunk lighting rules.");
        loaded.climate1710 = is1710(
            CATEGORY,
            "climate",
            "Temperatures, snow and ice. 1.7.10 gets colder with height and has no snowy taiga.");
        loaded.surfaceRules1710 = is1710(
            CATEGORY,
            "surfaceRules",
            "Surface blocks. 1.7.10 has gravel sea floors, deeper sandstone and stone patches on extreme hills.");
        loaded.animals1710 = is1710(CATEGORY, "animalSpawns", "Animal spawn lists.");
        loaded.dungeons1710 = is1710(CATEGORY, "dungeons", "Dungeon mobs and loot.");
        loaded.mineshafts1710 = is1710(CATEGORY, "mineshafts", "Mineshaft frequency. 1.6.4 is more common.");
        loaded.strongholdBiomes1710 = is1710(CATEGORY, "strongholdBiomes", "Biomes that can hold strongholds.");
        loaded.chestBooks1710 = is1710(CATEGORY, "chestBooks", "Enchanted books in generated chests.");

        boolean largeBiomes = config.hasKey(BIOMES, "largeBiomes") && config.getCategory(BIOMES)
            .remove("largeBiomes")
            .getBoolean(false);
        int biomeSize = Arrays.asList(BIOME_SIZES)
            .indexOf(
                config.getString(
                    "biomeSize",
                    BIOMES,
                    BIOME_SIZES[largeBiomes ? 3 : 1],
                    "Biome width compared to vanilla.",
                    BIOME_SIZES));
        loaded.biomeSize = (biomeSize < 0 ? 1 : biomeSize) + 3;
        loaded.moddedBiomes = config.getBoolean("moddedBiomes", BIOMES, true, "Biomes from other mods.");
        loaded.vanillaBiomeWeight = config.getInt(
            "vanillaBiomeWeight",
            BIOMES,
            10,
            1,
            1000,
            "Weight of each vanilla biome against modded biome weights.");
        loaded.climateZones = config
            .getBoolean("climateZones", BIOMES, false, "Warm, temperate, cool, and cold climate zones.");
        loaded.newBiomes = config.getBoolean(
            "newBiomes",
            BIOMES,
            false,
            "1.7.10 biomes such as savanna, mesa, birch forest, deep oceans, etc.");

        loaded.gravelOceanFloor = config.getBoolean("gravelOceanFloor", SURFACE, false, "Gravel under deep water.");
        loaded.deepSandstone = config.getBoolean("deepSandstone", SURFACE, false, "Deeper sandstone under sand.");
        loaded.hillsStonePatches = config
            .getBoolean("hillsStonePatches", SURFACE, false, "Bare stone patches on extreme hills.");

        loaded.flowers1710 = config
            .getBoolean("flowers", DECORATION, true, "1.7.10 flowers: tulips, orchids, daisies and poppies.");
        boolean plainsDoubleGrass = !config.hasKey(DECORATION, "plainsDoubleGrass") || config.getCategory(DECORATION)
            .remove("plainsDoubleGrass")
            .getBoolean(true);
        loaded.doubleTallGrass = config
            .getBoolean("doubleTallGrass", DECORATION, plainsDoubleGrass, "Allow double tall grass to generate.");
        loaded.forestDoublePlants = config
            .getBoolean("forestDoublePlants", DECORATION, true, "Lilacs, rose bushes and peonies in forests.");
        loaded.taigaLargeFerns = config.getBoolean("taigaLargeFerns", DECORATION, true, "Large ferns in taigas.");
        loaded.taigaFerns = config.getBoolean("taigaFerns", DECORATION, true, "Ferns instead of most grass in taigas.");
        loaded.taigaMushrooms = config.getBoolean("taigaMushrooms", DECORATION, true, "Mushrooms in taigas.");
        loaded.swampGrass = config.getBoolean("swampGrass", DECORATION, true, "More grass in swamps.");
        loaded.swampNoPatches = config
            .getBoolean("swampNoPatches", DECORATION, false, "No sand or gravel patches in swamps.");
        loaded.jungleMelons = config.getBoolean("jungleMelons", DECORATION, true, "Melons in jungles.");
        loaded.gravelPatches = config.getBoolean("gravelPatches", DECORATION, true, "Gravel patches on shores.");
        loaded.jungleVines = config.getBoolean("jungleVines", DECORATION, true, "Extra vines in jungles.");

        loaded.forestTrees1710 = config.getBoolean("forest", TREES, false, "1.7.10 forest trees: no big oaks.");
        loaded.hillsTrees1710 = config.getBoolean(
            "hills",
            TREES,
            false,
            "1.7.10 extreme hills trees: mostly spruce, and trees on the hill edges.");
        loaded.snowTrees1710 = config
            .getBoolean("snow", TREES, false, "Spruce instead of oak in ice plains and ice mountains.");
        loaded.jungleTrees1710 = config.getBoolean("jungle", TREES, false, "1.7.10 jungle trees.");

        if (config.hasChanged()) {
            config.save();
        }

        settings = loaded;
    }

    private static boolean is1710(String category, String name, String comment) {
        return V1710.equals(config.getString(name, category, V164, comment, VERSIONS));
    }
}
