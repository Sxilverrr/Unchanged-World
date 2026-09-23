package com.sxilverr.unchangedworld.world;

import cpw.mods.fml.common.Loader;

public final class Lumi164 {

    private static Boolean loaded;

    private Lumi164() {}

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = Loader.isModLoaded("lumi") || Loader.isModLoaded("lumina");
        }

        return loaded;
    }
}
