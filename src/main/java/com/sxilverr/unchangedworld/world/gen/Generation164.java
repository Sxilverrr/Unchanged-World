package com.sxilverr.unchangedworld.world.gen;

public final class Generation164 {

    private static final ThreadLocal<Boolean> ACTIVE = new ThreadLocal<Boolean>() {

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    private Generation164() {}

    public static boolean isActive() {
        return ACTIVE.get();
    }

    public static void begin() {
        ACTIVE.set(Boolean.TRUE);
    }

    public static void end() {
        ACTIVE.set(Boolean.FALSE);
    }
}
