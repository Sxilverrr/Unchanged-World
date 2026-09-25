package com.sxilverr.unchangedworld.world;

import java.io.File;

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
            config,
            CATEGORY,
            "biomeLayout",
            "Biome layout. 1.6.4 has large continents and the 1.6.4 biomes, 1.7.10 has climate zones and all 1.7.10 biomes.");
        loaded.terrain1710 = is1710(
            config,
            CATEGORY,
            "terrain",
            "Terrain generator. With 1.7.10 only the biome layout, lighting and biome options still apply.");
        loaded.caves1710 = is1710(config, CATEGORY, "caves", "Caves.");
        loaded.ravines1710 = is1710(config, CATEGORY, "ravines", "Ravines.");
        loaded.lighting1710 = is1710(config, CATEGORY, "lighting", "Chunk lighting rules.");
        loaded.climate1710 = is1710(
            config,
            CATEGORY,
            "climate",
            "Temperatures, snow and ice. 1.7.10 gets colder with height and has no snowy taiga.");
        loaded.surfaceRules1710 = is1710(
            config,
            CATEGORY,
            "surfaceRules",
            "Surface blocks. 1.7.10 has gravel sea floors, deeper sandstone and stone patches on extreme hills.");
        loaded.animals1710 = is1710(config, CATEGORY, "animalSpawns", "Animal spawn lists.");
        loaded.dungeons1710 = is1710(config, CATEGORY, "dungeons", "Dungeon mobs and loot.");
        loaded.mineshafts1710 = is1710(config, CATEGORY, "mineshafts", "Mineshaft frequency. 1.6.4 is more common.");
        loaded.strongholdBiomes1710 = is1710(config, CATEGORY, "strongholdBiomes", "Biomes that can hold strongholds.");
        loaded.chestBooks1710 = is1710(config, CATEGORY, "chestBooks", "Enchanted books in generated chests.");

        loaded.largeBiomes = flag(
            config,
            BIOMES,
            "largeBiomes",
            false,
            "Larger biomes, like the Large Biomes world type.");
        loaded.moddedBiomes = flag(config, BIOMES, "moddedBiomes", true, "Biomes from other mods.");
        loaded.vanillaBiomeWeight = config.getInt(
            "vanillaBiomeWeight",
            BIOMES,
            10,
            1,
            1000,
            "Weight of each 1.6.4 biome against modded biome weights.");

        loaded.gravelOceanFloor = flag(config, SURFACE, "gravelOceanFloor", false, "Gravel under deep water.");
        loaded.deepSandstone = flag(config, SURFACE, "deepSandstone", false, "Deeper sandstone under sand.");
        loaded.hillsStonePatches = flag(
            config,
            SURFACE,
            "hillsStonePatches",
            false,
            "Bare stone patches on extreme hills.");

        loaded.flowers1710 = flag(
            config,
            DECORATION,
            "flowers",
            true,
            "1.7.10 flowers: tulips, orchids, daisies and poppies.");
        loaded.plainsDoubleGrass = flag(config, DECORATION, "plainsDoubleGrass", true, "Double tall grass in plains.");
        loaded.forestDoublePlants = flag(
            config,
            DECORATION,
            "forestDoublePlants",
            true,
            "Lilacs, rose bushes and peonies in forests.");
        loaded.taigaLargeFerns = flag(config, DECORATION, "taigaLargeFerns", true, "Large ferns in taigas.");
        loaded.taigaFerns = flag(config, DECORATION, "taigaFerns", true, "Ferns instead of most grass in taigas.");
        loaded.taigaMushrooms = flag(config, DECORATION, "taigaMushrooms", true, "Mushrooms in taigas.");
        loaded.swampGrass = flag(config, DECORATION, "swampGrass", true, "More grass in swamps.");
        loaded.swampNoPatches = flag(
            config,
            DECORATION,
            "swampNoPatches",
            false,
            "No sand or gravel patches in swamps.");
        loaded.jungleMelons = flag(config, DECORATION, "jungleMelons", true, "Melons in jungles.");
        loaded.gravelPatches = flag(config, DECORATION, "gravelPatches", true, "Gravel patches on shores.");
        loaded.jungleVines = flag(config, DECORATION, "jungleVines", true, "Extra vines in jungles.");

        loaded.forestTrees1710 = flag(config, TREES, "forest", false, "1.7.10 forest trees: no big oaks.");
        loaded.hillsTrees1710 = flag(
            config,
            TREES,
            "hills",
            false,
            "1.7.10 extreme hills trees: mostly spruce, and trees on the hill edges.");
        loaded.snowTrees1710 = flag(
            config,
            TREES,
            "snow",
            false,
            "Spruce instead of oak in ice plains and ice mountains.");
        loaded.jungleTrees1710 = flag(config, TREES, "jungle", false, "1.7.10 jungle trees.");

        if (config.hasChanged()) {
            config.save();
        }

        settings = loaded;
    }

    private static boolean is1710(Configuration config, String category, String name, String comment) {
        return V1710.equals(config.getString(name, category, V164, comment, VERSIONS));
    }

    private static boolean flag(Configuration config, String category, String name, boolean fallback, String comment) {
        return config.getBoolean(name, category, fallback, comment);
    }
}
