package org.yuemi.mmoessence.plugin.config;

public class EssenceConfig {

    private int maxEssence = 100;

    public void loadFromConfig(org.bukkit.configuration.file.FileConfiguration config) {
        this.maxEssence = config.getInt("max-essence", 100);
    }

    public int getMaxEssence() {
        return maxEssence;
    }
}
