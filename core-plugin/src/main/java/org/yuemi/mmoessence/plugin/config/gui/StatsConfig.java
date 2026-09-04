package org.yuemi.mmoessence.plugin.config.gui;

import org.bukkit.DyeColor;

import java.util.List;
import java.util.Map;

public record StatsConfig(
    Map<String, ElementDisplayConfig> elements
) {
    public static StatsConfig fromConfig(org.bukkit.configuration.ConfigurationSection section) {
        var elementsSection = section.getConfigurationSection("elements");
        Map<String, ElementDisplayConfig> elements = new java.util.HashMap<>();
        
        if (elementsSection != null) {
            for (String key : elementsSection.getKeys(false)) {
                var elementSection = elementsSection.getConfigurationSection(key);
                if (elementSection != null) {
                    elements.put(key.toUpperCase(), ElementDisplayConfig.fromConfig(elementSection));
                }
            }
        }
        
        return new StatsConfig(elements);
    }
    
    public record ElementDisplayConfig(
        String name,
        DyeColor color,
        List<String> lore,
        int slot,
        List<String> weakness
    ) {
        public static ElementDisplayConfig fromConfig(org.bukkit.configuration.ConfigurationSection section) {
            String name = section.getString("name", "<white>Element");
            
            String colorStr = section.getString("color", "WHITE");
            DyeColor color;
            try {
                color = DyeColor.valueOf(colorStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                color = DyeColor.WHITE;
            }
            
            List<String> lore = section.getStringList("lore");
            if (lore.isEmpty()) {
                lore = List.of("<gray>Essence Level: {current}/{max}");
            }
            
            int slot = section.getInt("slot", 0);
            List<String> weakness = section.getStringList("weakness");
            
            return new ElementDisplayConfig(name, color, lore, slot, weakness);
        }
    }
}
