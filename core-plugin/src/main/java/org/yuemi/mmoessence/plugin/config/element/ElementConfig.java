package org.yuemi.mmoessence.plugin.config.element;

import org.bukkit.Color;
import org.bukkit.DyeColor;

import java.util.List;

public record ElementConfig(
    String name,
    DyeColor dyeColor,
    List<String> lore,
    List<String> weakness
) {
    public Color getColor() {
        return dyeColor.getColor();
    }

    public static ElementConfig fromConfig(String name, org.bukkit.configuration.ConfigurationSection section) {
        String displayName = section.getString("name", "<gray>" + name);
        DyeColor dyeColor = parseDyeColor(section.getString("color", "WHITE"));
        List<String> lore = section.getStringList("lore");
        if (lore.isEmpty()) {
            lore = List.of("<gray>Essence: <white>{current}<gray>/<white>{max}");
        }
        List<String> weakness = section.getStringList("weakness");
        
        return new ElementConfig(displayName, dyeColor, lore, weakness);
    }

    private static DyeColor parseDyeColor(String colorName) {
        try {
            return DyeColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DyeColor.WHITE;
        }
    }
}
