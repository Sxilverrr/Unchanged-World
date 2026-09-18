package com.sxilverr.unchangedworld.world.biome;

import net.minecraft.world.biome.BiomeGenBase;

public final class Decoration164 {

    public enum Kind {
        DEFAULT,
        DESERT,
        HILLS,
        FOREST,
        TAIGA,
        SWAMP,
        JUNGLE
    }

    private static final Decoration164[] TABLE = new Decoration164[256];
    private static final Decoration164 DEFAULT = new Decoration164(Kind.DEFAULT);

    static {
        put(
            BiomeGenBase.plains,
            new Decoration164(Kind.DEFAULT).trees(-999)
                .flowers(4)
                .grass(10));
        Decoration164 desert = new Decoration164(Kind.DESERT).trees(-999)
            .deadBushes(2)
            .reeds(50)
            .cacti(10);
        put(BiomeGenBase.desert, desert);
        put(BiomeGenBase.desertHills, desert);
        Decoration164 hills = new Decoration164(Kind.HILLS);
        put(BiomeGenBase.extremeHills, hills);
        put(BiomeGenBase.extremeHillsEdge, hills);
        Decoration164 forest = new Decoration164(Kind.FOREST).trees(10)
            .grass(2);
        put(BiomeGenBase.forest, forest);
        put(BiomeGenBase.forestHills, forest);
        Decoration164 taiga = new Decoration164(Kind.TAIGA).trees(10)
            .grass(1);
        put(BiomeGenBase.taiga, taiga);
        put(BiomeGenBase.taigaHills, taiga);
        put(
            BiomeGenBase.swampland,
            new Decoration164(Kind.SWAMP).trees(2)
                .flowers(-999)
                .deadBushes(1)
                .mushrooms(8)
                .reeds(10)
                .clay(1)
                .waterlilies(4));
        Decoration164 mushroom = new Decoration164(Kind.DEFAULT).trees(-100)
            .flowers(-100)
            .grass(-100)
            .mushrooms(1)
            .bigMushrooms(1);
        put(BiomeGenBase.mushroomIsland, mushroom);
        put(BiomeGenBase.mushroomIslandShore, mushroom);
        put(BiomeGenBase.beach, new Decoration164(Kind.DEFAULT).trees(-999));
        Decoration164 jungle = new Decoration164(Kind.JUNGLE).trees(50)
            .grass(25)
            .flowers(4);
        put(BiomeGenBase.jungle, jungle);
        put(BiomeGenBase.jungleHills, jungle);
    }

    public final Kind kind;
    public int treesPerChunk;
    public int flowersPerChunk = 2;
    public int grassPerChunk = 1;
    public int deadBushPerChunk;
    public int mushroomsPerChunk;
    public int reedsPerChunk;
    public int cactiPerChunk;
    public int sandPerChunk = 1;
    public int sandPerChunk2 = 3;
    public int clayPerChunk = 1;
    public int bigMushroomsPerChunk;
    public int waterlilyPerChunk;
    public boolean generateLakes = true;

    private Decoration164(Kind kind) {
        this.kind = kind;
    }

    private Decoration164 trees(int value) {
        this.treesPerChunk = value;
        return this;
    }

    private Decoration164 flowers(int value) {
        this.flowersPerChunk = value;
        return this;
    }

    private Decoration164 grass(int value) {
        this.grassPerChunk = value;
        return this;
    }

    private Decoration164 deadBushes(int value) {
        this.deadBushPerChunk = value;
        return this;
    }

    private Decoration164 mushrooms(int value) {
        this.mushroomsPerChunk = value;
        return this;
    }

    private Decoration164 reeds(int value) {
        this.reedsPerChunk = value;
        return this;
    }

    private Decoration164 cacti(int value) {
        this.cactiPerChunk = value;
        return this;
    }

    private Decoration164 clay(int value) {
        this.clayPerChunk = value;
        return this;
    }

    private Decoration164 bigMushrooms(int value) {
        this.bigMushroomsPerChunk = value;
        return this;
    }

    private Decoration164 waterlilies(int value) {
        this.waterlilyPerChunk = value;
        return this;
    }

    private static void put(BiomeGenBase biome, Decoration164 decoration) {
        TABLE[biome.biomeID] = decoration;
    }

    public static Decoration164 get(BiomeGenBase biome) {
        Decoration164 decoration = biome == null ? null : TABLE[biome.biomeID];
        return decoration == null ? DEFAULT : decoration;
    }
}
