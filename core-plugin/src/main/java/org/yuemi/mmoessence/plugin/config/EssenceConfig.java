package org.yuemi.mmoessence.plugin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yuemi.mmoessence.plugin.config.element.ElementConfig;
import org.yuemi.mmoessence.plugin.config.gui.GeneralConfig;
import org.yuemi.mmoessence.plugin.config.gui.StatsConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EssenceConfig {

    private int maxEssence = 100;
    private final Map<String, ElementConfig> elements = new HashMap<>();
    private GeneralConfig generalConfig;
    private StatsConfig statsConfig;
    private final org.bukkit.plugin.java.JavaPlugin plugin;

    public EssenceConfig(org.bukkit.plugin.java.JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadFromConfig(FileConfiguration config) {
        this.maxEssence = config.getInt("max-essence", 100);

        // Load element configurations from main config
        elements.clear();
        if (config.contains("elements")) {
            var elementsSection = config.getConfigurationSection("elements");
            if (elementsSection != null) {
                for (String key : elementsSection.getKeys(false)) {
                    var elementSection = elementsSection.getConfigurationSection(key);
                    if (elementSection != null) {
                        elements.put(key.toUpperCase(), ElementConfig.fromConfig(key, elementSection));
                    }
                }
            }
        }

        // Load GUI configurations from gui folder
        loadGuiConfigs();
    }

    private void loadGuiConfigs() {
        // Save default GUI configs if they don't exist
        saveDefaultGuiConfig("gui/general.yml");
        saveDefaultGuiConfig("gui/stats.yml");
        
        // Load general.yml
        File generalFile = new File(plugin.getDataFolder(), "gui/general.yml");
        FileConfiguration generalConfigFile = YamlConfiguration.loadConfiguration(generalFile);
        this.generalConfig = GeneralConfig.fromConfig(generalConfigFile);
        
        // Load stats.yml
        File statsFile = new File(plugin.getDataFolder(), "gui/stats.yml");
        FileConfiguration statsConfigFile = YamlConfiguration.loadConfiguration(statsFile);
        this.statsConfig = StatsConfig.fromConfig(statsConfigFile);
    }
    
    private void saveDefaultGuiConfig(String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
        }
    }

    public void reload() {
        plugin.reloadConfig();
        loadFromConfig(plugin.getConfig());
    }

    public int getMaxEssence() {
        return maxEssence;
    }

    public Map<String, ElementConfig> getElements() {
        return Map.copyOf(elements);
    }

    public ElementConfig getElement(String elementName) {
        return elements.get(elementName.toUpperCase());
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public StatsConfig getStatsConfig() {
        return statsConfig;
    }
}
