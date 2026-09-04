package org.yuemi.mmoessence.api;

import org.jetbrains.annotations.Nullable;

/**
 * Singleton provider for accessing the EssenceApi.
 */
public final class EssenceApiProvider {

    @Nullable
    private static volatile EssenceApi instance;

    private EssenceApiProvider() {}

    @Nullable
    public static EssenceApi getApi() {
        return instance;
    }

    public static void setApi(EssenceApi api) {
        instance = api;
    }

    public static boolean isAvailable() {
        return instance != null;
    }
}
