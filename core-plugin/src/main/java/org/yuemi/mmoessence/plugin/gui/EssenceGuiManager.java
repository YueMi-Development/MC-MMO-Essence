package org.yuemi.mmoessence.plugin.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yuemi.libs.api.gui.*;
import org.yuemi.mmoessence.api.EssenceApi;
import org.yuemi.mmoessence.api.ElementType;
import org.yuemi.mmoessence.api.EssenceApiProvider;
import org.yuemi.mmoessence.plugin.EssencePlugin;
import org.yuemi.mmoessence.plugin.config.EssenceConfig;
import org.yuemi.mmoessence.plugin.config.element.ElementConfig;
import org.yuemi.mmoessence.plugin.config.gui.GeneralConfig;
import org.yuemi.mmoessence.plugin.config.gui.StatsConfig;

import java.util.List;

public class EssenceGuiManager {

    private final EssencePlugin plugin;
    private final EssenceConfig config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private Gui gui;

    public EssenceGuiManager(EssencePlugin plugin, EssenceConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void initialize() {
        GuiApi guiApi = GuiProvider.getApi();
        
        if (guiApi == null) {
            plugin.getLogger().warning("GuiApi not available. GUI disabled.");
            return;
        }

        GeneralConfig generalConfig = config.getGeneralConfig();
        
        this.gui = guiApi.createBuilder()
                .title(serializeLegacy(generalConfig.title()))
                .rows(generalConfig.rows())
                .createLayer("border", 0, layer -> {
                    for (int slot : generalConfig.borderSlots()) {
                        layer.setItem(slot, createBorderItem(generalConfig.borderColor()));
                    }
                })
                .createLayer("elements", 1, layer -> {
                    StatsConfig statsConfig = config.getStatsConfig();
                    if (statsConfig != null && statsConfig.elements() != null) {
                        for (var entry : statsConfig.elements().entrySet()) {
                            String elementName = entry.getKey();
                            StatsConfig.ElementDisplayConfig displayConfig = entry.getValue();
                            layer.setItem(displayConfig.slot(), createElementItem(elementName, displayConfig));
                        }
                    }
                })
                .closePolicy(ClosePolicy.CLOSE)
                .build();
    }

    public void openGui(Player player) {
        if (gui != null) {
            gui.open(player);
        }
    }

    public boolean isGuiAvailable() {
        return gui != null;
    }

    private GuiItem createBorderItem(DyeColor color) {
        Material material = getGlassPaneMaterial(color);
        
        return GuiItem.builder()
                .item(new ItemStack(material))
                .condition(player -> true)
                .build();
    }

    private GuiItem createElementItem(String elementName, StatsConfig.ElementDisplayConfig displayConfig) {
        return GuiItem.builder()
                .item(player -> createElementItemStack(elementName, displayConfig, player))
                .build();
    }

    private ItemStack createElementItemStack(String elementName, StatsConfig.ElementDisplayConfig displayConfig, Player player) {
        ElementConfig elementConfig = config.getElement(elementName);
        if (elementConfig == null) {
            return new ItemStack(Material.PAPER);
        }

        ElementType elementType = parseElementType(elementName);
        EssenceApi api = EssenceApiProvider.getApi();
        
        final int finalCurrent = api != null && elementType != null ? api.getEssence(player, elementType) : 0;
        final int max = config.getMaxEssence();
        final double finalPercent = max > 0 ? (finalCurrent * 100.0 / max) : 0;

        Material material = getGlassPaneMaterial(displayConfig.color());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(miniMessage.deserialize(displayConfig.name()));
            meta.lore(displayConfig.lore().stream()
                    .map(line -> replacePlaceholders(line, finalCurrent, max, finalPercent))
                    .map(miniMessage::deserialize)
                    .map(Component.class::cast)
                    .toList());
            item.setItemMeta(meta);
        }

        item.setAmount(Math.max(1, finalCurrent));
        return item;
    }

    private String replacePlaceholders(String lore, int current, int max, double percent) {
        return lore.replace("{current}", String.valueOf(current))
                   .replace("{max}", String.valueOf(max))
                   .replace("{percent}", String.format("%.1f", percent));
    }

    private ElementType parseElementType(String name) {
        try {
            return ElementType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Material getGlassPaneMaterial(DyeColor color) {
        return switch (color) {
            case WHITE -> Material.WHITE_STAINED_GLASS_PANE;
            case ORANGE -> Material.ORANGE_STAINED_GLASS_PANE;
            case MAGENTA -> Material.MAGENTA_STAINED_GLASS_PANE;
            case LIGHT_BLUE -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            case YELLOW -> Material.YELLOW_STAINED_GLASS_PANE;
            case LIME -> Material.LIME_STAINED_GLASS_PANE;
            case PINK -> Material.PINK_STAINED_GLASS_PANE;
            case GRAY -> Material.GRAY_STAINED_GLASS_PANE;
            case LIGHT_GRAY -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
            case CYAN -> Material.CYAN_STAINED_GLASS_PANE;
            case PURPLE -> Material.PURPLE_STAINED_GLASS_PANE;
            case BLUE -> Material.BLUE_STAINED_GLASS_PANE;
            case BROWN -> Material.BROWN_STAINED_GLASS_PANE;
            case GREEN -> Material.GREEN_STAINED_GLASS_PANE;
            case RED -> Material.RED_STAINED_GLASS_PANE;
            case BLACK -> Material.BLACK_STAINED_GLASS_PANE;
        };
    }

    private String serializeLegacy(String miniMessageText) {
        Component component = miniMessage.deserialize(miniMessageText);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
