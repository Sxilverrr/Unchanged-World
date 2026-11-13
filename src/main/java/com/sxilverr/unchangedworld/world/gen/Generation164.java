package com.sxilverr.unchangedworld.world.gen;

import com.sxilverr.unchangedworld.world.Settings164;

public final class Generation164 {

    private static final ThreadLocal<Settings164> SETTINGS = new ThreadLocal<Settings164>();

    private Generation164() {}

    public static boolean isActive() {
        return SETTINGS.get() != null;
    }

    public static boolean isBooks164() {
        Settings164 settings = SETTINGS.get();
        return settings != null && !settings.chestBooks1710;
    }

    public static boolean noDoubleTallGrass() {
        Settings164 settings = SETTINGS.get();
        return settings != null && !settings.doubleTallGrass;
    }

    public static void begin(Settings164 settings) {
        SETTINGS.set(settings);
    }

    public static void end() {
        SETTINGS.remove();
    }
}
