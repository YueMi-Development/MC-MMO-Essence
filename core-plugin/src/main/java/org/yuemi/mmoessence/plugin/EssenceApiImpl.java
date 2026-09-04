package org.yuemi.mmoessence.plugin;

import org.yuemi.mmoessence.api.EssenceApi;
import org.yuemi.mmoessence.api.ElementType;
import org.yuemi.mmoessence.plugin.config.EssenceConfig;

import java.util.EnumMap;
import java.util.Map;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class EssenceApiImpl implements EssenceApi {

    private final EssencePlugin plugin;
    private final EssenceConfig config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final org.bukkit.NamespacedKey essenceKey;

    public EssenceApiImpl(EssencePlugin plugin, EssenceConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.essenceKey = new org.bukkit.NamespacedKey(plugin, "essence");
    }

    @Override
    public int getEssence(org.bukkit.entity.Player player, ElementType element) {
        return getPlayerEssenceArray(player)[element.ordinal()];
    }

    @Override
    public int getMaxEssence(org.bukkit.entity.Player player, ElementType element) {
        return config.getMaxEssence();
    }

    @Override
    public void addEssence(org.bukkit.entity.Player player, ElementType element, int amount) {
        int[] essence = getPlayerEssenceArray(player);
        int current = essence[element.ordinal()];
        int max = getMaxEssence(player, element);
        essence[element.ordinal()] = Math.min(current + amount, max);
        savePlayerEssence(player, essence);
    }

    @Override
    public void setEssence(org.bukkit.entity.Player player, ElementType element, int value) {
        int[] essence = getPlayerEssenceArray(player);
        essence[element.ordinal()] = Math.max(0, value);
        savePlayerEssence(player, essence);
    }

    @Override
    public boolean hasEssence(org.bukkit.entity.Player player, ElementType element, int amount) {
        return getEssence(player, element) >= amount;
    }

    @Override
    public double getEssencePercent(org.bukkit.entity.Player player, ElementType element) {
        int current = getEssence(player, element);
        int max = getMaxEssence(player, element);
        if (max == 0) return 0.0;
        return (double) current / max;
    }

    @Override
    public Map<ElementType, Integer> getAllEssence(org.bukkit.entity.Player player) {
        int[] essence = getPlayerEssenceArray(player);
        Map<ElementType, Integer> result = new EnumMap<>(ElementType.class);
        for (ElementType element : ElementType.values()) {
            result.put(element, essence[element.ordinal()]);
        }
        return result;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void sendMessage(org.bukkit.entity.Player player, String message) {
        player.sendMessage(miniMessage.deserialize(message));
    }

    @Override
    public boolean isFeatureEnabled(org.bukkit.entity.Player player) {
        return player.hasPermission("essence.feature");
    }

    private int[] getPlayerEssenceArray(org.bukkit.entity.Player player) {
        int[] defaultEssence = new int[6];
        String stored = player.getPersistentDataContainer().get(essenceKey, org.bukkit.persistence.PersistentDataType.STRING);
        if (stored != null && !stored.isEmpty()) {
            try {
                String[] parts = stored.split(",");
                if (parts.length == 6) {
                    for (int i = 0; i < 6; i++) {
                        defaultEssence[i] = Integer.parseInt(parts[i].trim());
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
        return defaultEssence;
    }

    private void savePlayerEssence(org.bukkit.entity.Player player, int[] essence) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < essence.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(essence[i]);
        }
        player.getPersistentDataContainer().set(essenceKey, org.bukkit.persistence.PersistentDataType.STRING, sb.toString());
    }
}
