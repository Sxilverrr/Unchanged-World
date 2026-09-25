package com.sxilverr.unchangedworld.world;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import cpw.mods.fml.common.Loader;

public final class EndlessIds164 {

    public static final String MOD_ID = "endlessids";

    private static final String CHUNK_BIOME_HOOK = "com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook";

    private static boolean resolved;
    private static Class<?> chunkBiomeHook;
    private static MethodHandle getBiomeShortArray;

    private EndlessIds164() {}

    private static synchronized void resolve() {
        if (resolved) {
            return;
        }

        resolved = true;

        if (!Loader.isModLoaded(MOD_ID)) {
            return;
        }

        try {
            Class<?> hook = Class.forName(CHUNK_BIOME_HOOK);
            getBiomeShortArray = MethodHandles.lookup()
                .unreflect(hook.getMethod("getBiomeShortArray"));
            chunkBiomeHook = hook;
        } catch (ReflectiveOperationException e) {
            chunkBiomeHook = null;
        }
    }

    public static boolean setBiomes(Chunk chunk, BiomeGenBase[] biomes) {
        resolve();

        if (chunkBiomeHook == null || !chunkBiomeHook.isInstance(chunk)) {
            return false;
        }

        short[] ids;

        try {
            ids = (short[]) getBiomeShortArray.invoke(chunk);
        } catch (Throwable t) {
            return false;
        }

        for (int i = 0; i < ids.length; ++i) {
            ids[i] = (short) biomes[i].biomeID;
        }

        return true;
    }
}
