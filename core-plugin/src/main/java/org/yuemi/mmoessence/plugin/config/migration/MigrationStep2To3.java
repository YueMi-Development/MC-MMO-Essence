package org.yuemi.mmoessence.plugin.config.migration;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yuemi.config.api.MigrationStep;

import java.util.List;

public class MigrationStep2To3 implements MigrationStep {
    @Override
    public int getTargetVersion() {
        return 3;
    }

    @Override
    public void migrate(@NotNull FileConfiguration config) {
        if (!config.contains("elements")) {
            config.set("elements.FIRE.name", "<red>Fire");
            config.set("elements.FIRE.color", "RED");
            config.set("elements.FIRE.lore", List.of("<gray>Essence: <red>{current}<gray>/<red>{max}"));
            config.set("elements.FIRE.weakness", List.of("WATER"));

            config.set("elements.WATER.name", "<blue>Water");
            config.set("elements.WATER.color", "BLUE");
            config.set("elements.WATER.lore", List.of("<gray>Essence: <blue>{current}<gray>/<blue>{max}"));
            config.set("elements.WATER.weakness", List.of("FIRE"));

            config.set("elements.EARTH.name", "<green>Earth");
            config.set("elements.EARTH.color", "GREEN");
            config.set("elements.EARTH.lore", List.of("<gray>Essence: <green>{current}<gray>/<green>{max}"));
            config.set("elements.EARTH.weakness", List.of("WIND"));

            config.set("elements.WIND.name", "<white>Wind");
            config.set("elements.WIND.color", "WHITE");
            config.set("elements.WIND.lore", List.of("<gray>Essence: <white>{current}<gray>/<white>{max}"));
            config.set("elements.WIND.weakness", List.of("EARTH"));

            config.set("elements.DARK.name", "<dark_gray>Dark");
            config.set("elements.DARK.color", "GRAY");
            config.set("elements.DARK.lore", List.of("<gray>Essence: <dark_gray>{current}<gray>/<dark_gray>{max}"));
            config.set("elements.DARK.weakness", List.of("LIGHT"));

            config.set("elements.LIGHT.name", "<yellow>Light");
            config.set("elements.LIGHT.color", "YELLOW");
            config.set("elements.LIGHT.lore", List.of("<gray>Essence: <yellow>{current}<gray>/<yellow>{max}"));
            config.set("elements.LIGHT.weakness", List.of("DARK"));
        }
    }
}
