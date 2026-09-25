package com.sxilverr.unchangedworld.world.gen;

public final class Generation164 {

    private static final ThreadLocal<Boolean> ACTIVE = new ThreadLocal<Boolean>() {

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    private static final ThreadLocal<Boolean> BOOKS = new ThreadLocal<Boolean>() {

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    private Generation164() {}

    public static boolean isActive() {
        return ACTIVE.get();
    }

    public static boolean isBooks164() {
        return BOOKS.get();
    }

    public static void begin(boolean books164) {
        ACTIVE.set(Boolean.TRUE);
        BOOKS.set(books164);
    }

    public static void end() {
        ACTIVE.set(Boolean.FALSE);
        BOOKS.set(Boolean.FALSE);
    }
}
