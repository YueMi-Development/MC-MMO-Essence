package org.yuemi.mmoessence.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * API for managing elemental stats (Essence) for players.
 * 
 * Essence represents elemental affinity levels stored in player PDC.
 */
public interface EssenceApi {

    int getEssence(@NotNull Player player, @NotNull ElementType element);

    int getMaxEssence(@NotNull Player player, @NotNull ElementType element);

    void addEssence(@NotNull Player player, @NotNull ElementType element, int amount);

    void setEssence(@NotNull Player player, @NotNull ElementType element, int value);

    boolean hasEssence(@NotNull Player player, @NotNull ElementType element, int amount);

    double getEssencePercent(@NotNull Player player, @NotNull ElementType element);

    @NotNull Map<ElementType, Integer> getAllEssence(@NotNull Player player);

    boolean isAvailable();

    void sendMessage(@NotNull Player player, @NotNull String message);

    boolean isFeatureEnabled(@NotNull Player player);
}
