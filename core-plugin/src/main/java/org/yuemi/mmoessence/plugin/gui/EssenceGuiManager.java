package org.yuemi.mmoessence.plugin.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        int rows = generalConfig.rows();
        int totalSlots = rows * 9;

        // Calculate reserved slots (elements)
        Set<Integer> reservedSlots = new HashSet<>();
        if (generalConfig.elements() != null) {
            for (var entry : generalConfig.elements().entrySet()) {
                reservedSlots.addAll(entry.getValue().slots());
            }
        }

        this.gui = guiApi.createBuilder()
                .title(serializeLegacy(generalConfig.title()))
                .rows(rows)
                // Layer 0: Border - auto-fill all non-reserved slots
                .createLayer("border", 0, layer -> {
                    for (int slot = 0; slot < totalSlots; slot++) {
                        if (!reservedSlots.contains(slot)) {
                            layer.setItem(slot, createBorderItem());
                        }
                    }
                })
                // Layer 1: Elements - place element items at their slots
                .createLayer("elements", 1, layer -> {
                    if (generalConfig.elements() != null) {
                        for (var entry : generalConfig.elements().entrySet()) {
                            String elementName = entry.getKey();
                            GeneralConfig.ElementDisplayConfig displayConfig = entry.getValue();
                            for (int slot : displayConfig.slots()) {
                                if (slot < totalSlots) {
                                    layer.setItem(slot, createElementItem(elementName, displayConfig));
                                }
                            }
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

    private GuiItem createBorderItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(" "));
            item.setItemMeta(meta);
        }
        return GuiItem.builder()
                .item(item)
                .condition(player -> true)
                .build();
    }

    private GuiItem createElementItem(String elementName, GeneralConfig.ElementDisplayConfig displayConfig) {
        return GuiItem.builder()
                .item(player -> createElementItemStack(elementName, displayConfig, player))
                .build();
    }

    private ItemStack createElementItemStack(String elementName, GeneralConfig.ElementDisplayConfig displayConfig, Player player) {
        ElementConfig elementConfig = config.getElement(elementName);
        if (elementConfig == null) {
            return new ItemStack(Material.PAPER);
        }

        ElementType elementType = parseElementType(elementName);
        EssenceApi api = EssenceApiProvider.getApi();

        final int current = api != null && elementType != null ? api.getEssence(player, elementType) : 0;
        final int max = config.getMaxEssence();
        final double percent = max > 0 ? (current * 100.0 / max) : 0;

        // Get material using YueMiLibs item provider
        Material material = parseMaterial(displayConfig.material());

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(miniMessage.deserialize("<white>" + elementConfig.name()));

            // Build lore: base lore + progress lore (if enabled)
            List<String> lore = new java.util.ArrayList<>(displayConfig.resolvedLore());
            if (displayConfig.showProgress()) {
                List<String> progressLore = config.getGeneralConfig().getProgressLore();
                lore.addAll(progressLore.stream()
                        .map(line -> replacePlaceholders(line, current, max, percent))
                        .toList());
            }

            meta.lore(lore.stream()
                    .map(line -> "<!i>" + line)
                    .map(miniMessage::deserialize)
                    .map(c -> (Component) c)
                    .toList());
            item.setItemMeta(meta);
        }

        // Stack size represents current essence level
        item.setAmount(Math.max(1, Math.min(64, current)));
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

    private Material parseMaterial(String materialStr) {
        try {
            return Material.valueOf(materialStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.PAPER;
        }
    }

    private String serializeLegacy(String miniMessageText) {
        Component component = miniMessage.deserialize(miniMessageText);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
