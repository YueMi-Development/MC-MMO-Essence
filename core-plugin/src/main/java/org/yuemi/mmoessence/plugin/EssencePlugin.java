package org.yuemi.mmoessence.plugin;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuemi.mmoessence.api.EssenceApi;
import org.yuemi.mmoessence.api.EssenceApiProvider;
import org.yuemi.mmoessence.plugin.command.EssenceCommand;
import org.yuemi.mmoessence.plugin.config.EssenceConfig;
import org.yuemi.mmoessence.plugin.gui.EssenceGuiManager;

public class EssencePlugin extends JavaPlugin {

    private EssenceConfig config;
    private EssenceApiImpl api;
    private EssenceGuiManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Check and migrate config
        new org.yuemi.config.api.ConfigManager(this, "org.yuemi.mmoessence.plugin.config.migration").loadAndMigrate(this);
        reloadConfig();

        // Load configuration
        this.config = new EssenceConfig(this);
        this.config.loadFromConfig(getConfig());

        // Initialize API
        this.api = new EssenceApiImpl(this, config);

        // Register API
        getServer().getServicesManager().register(
                EssenceApi.class,
                api,
                this,
                ServicePriority.Normal
        );

        EssenceApiProvider.setApi(api);

        // Initialize GUI manager
        this.guiManager = new EssenceGuiManager(this, config);
        guiManager.initialize();

        // Register command
        EssenceCommand command = new EssenceCommand(guiManager);
        getCommand("essence").setExecutor(command);
        getCommand("essence").setTabCompleter(command);

        getLogger().info("MmoEssence enabled!");
    }

    @Override
    public void onDisable() {
        EssenceApiProvider.setApi(null);
        if (api != null) {
            getServer().getServicesManager().unregister(EssenceApi.class, api);
        }
        getLogger().info("MmoEssence disabled!");
    }

    public EssenceConfig getEssenceConfig() {
        return config;
    }
}
