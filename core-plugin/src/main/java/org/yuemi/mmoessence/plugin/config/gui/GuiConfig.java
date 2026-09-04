package org.yuemi.mmoessence.plugin.config.gui;

import org.bukkit.DyeColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record GuiConfig(
    String title,
    int rows,
    Map<String, Integer> elementSlots,
    DyeColor borderColor,
    List<Integer> borderSlots
) {
    public static GuiConfig fromConfig(org.bukkit.configuration.ConfigurationSection section) {
        String title = section.getString("title", "<gold>Elemental Essence");
        int rows = section.getInt("rows", 3);
        
        Map<String, Integer> elementSlots = new HashMap<>();
        if (section.contains("element-slots")) {
            var elementSlotsSection = section.getConfigurationSection("element-slots");
            if (elementSlotsSection != null) {
                for (String key : elementSlotsSection.getKeys(false)) {
                    elementSlots.put(key.toUpperCase(), elementSlotsSection.getInt(key));
                }
            }
        }
        
        String borderColorStr = section.getString("border-color", "BLACK");
        DyeColor borderColor;
        try {
            borderColor = DyeColor.valueOf(borderColorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            borderColor = DyeColor.BLACK;
        }
        
        List<Integer> borderSlots = section.getIntegerList("border-slots");
        if (borderSlots.isEmpty()) {
            borderSlots = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
        }
        
        return new GuiConfig(title, rows, elementSlots, borderColor, borderSlots);
    }
}
