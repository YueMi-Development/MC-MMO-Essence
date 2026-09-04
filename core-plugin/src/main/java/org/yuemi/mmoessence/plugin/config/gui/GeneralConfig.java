package org.yuemi.mmoessence.plugin.config.gui;

import org.bukkit.DyeColor;

import java.util.List;
import java.util.Map;

public record GeneralConfig(
    String title,
    int rows,
    DyeColor borderColor,
    List<Integer> borderSlots,
    boolean showProgressBar,
    List<Integer> progressBarSlots
) {
    public static GeneralConfig fromConfig(org.bukkit.configuration.ConfigurationSection section) {
        String title = section.getString("title", "<gold>Elemental Essence");
        int rows = section.getInt("rows", 3);
        
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
        
        boolean showProgressBar = section.getBoolean("show-progress-bar", true);
        List<Integer> progressBarSlots = section.getIntegerList("progress-bar-slots");
        if (progressBarSlots.isEmpty()) {
            progressBarSlots = List.of(19, 20, 21, 22, 23, 24, 25);
        }
        
        return new GeneralConfig(title, rows, borderColor, borderSlots, showProgressBar, progressBarSlots);
    }
}
