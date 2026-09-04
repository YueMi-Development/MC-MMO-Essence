package org.yuemi.mmoessence.plugin.config.gui;

import java.util.List;
import java.util.Map;

public record GeneralConfig(
    String title,
    int rows,
    Map<String, List<String>> lores,
    Map<String, ElementDisplayConfig> elements
) {
    public static GeneralConfig fromConfig(org.bukkit.configuration.ConfigurationSection section) {
        String title = section.getString("title", "<gold>Elemental Essence");
        int rows = section.getInt("rows", 3);

        // Load reusable lore definitions
        Map<String, List<String>> lores = new java.util.HashMap<>();
        var loresSection = section.getConfigurationSection("lores");
        if (loresSection != null) {
            for (String key : loresSection.getKeys(false)) {
                lores.put(key.toLowerCase(), loresSection.getStringList(key));
            }
        }

        // Load element configurations
        Map<String, ElementDisplayConfig> elements = new java.util.HashMap<>();
        var elementsSection = section.getConfigurationSection("elements");
        if (elementsSection != null) {
            for (String key : elementsSection.getKeys(false)) {
                var elementSection = elementsSection.getConfigurationSection(key);
                if (elementSection != null) {
                    elements.put(key.toUpperCase(), ElementDisplayConfig.fromConfig(key, elementSection, lores));
                }
            }
        }

        return new GeneralConfig(title, rows, lores, elements);
    }

    public List<String> getLore(String key) {
        return lores.getOrDefault(key.toLowerCase(), List.of());
    }

    public List<String> getProgressLore() {
        return getLore("progress-bar");
    }

    public record ElementDisplayConfig(
        String material,
        String color,
        String loreKey,
        List<Integer> slots,
        boolean showProgress,
        List<String> weakness,
        List<String> resolvedLore
    ) {
        public static ElementDisplayConfig fromConfig(String elementName, org.bukkit.configuration.ConfigurationSection section, Map<String, List<String>> allLores) {
            String material = section.getString("material", "PAPER");
            String color = section.getString("color", "#FFFFFF");
            String loreKey = section.getString("lore-key", "element-" + elementName.toLowerCase());
            List<Integer> slots = section.getIntegerList("slots");
            boolean showProgress = section.getBoolean("show-progress", true);
            List<String> weakness = section.getStringList("weakness");

            // Resolve lore from lore-key
            List<String> resolvedLore = allLores.getOrDefault(loreKey.toLowerCase(), List.of());

            return new ElementDisplayConfig(material, color, loreKey, slots, showProgress, weakness, resolvedLore);
        }
    }
}
