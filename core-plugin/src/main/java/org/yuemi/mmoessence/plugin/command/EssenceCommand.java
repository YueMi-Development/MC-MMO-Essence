package org.yuemi.mmoessence.plugin.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuemi.mmoessence.plugin.gui.EssenceGuiManager;

import java.util.List;

public class EssenceCommand implements CommandExecutor, TabCompleter {

    private final EssenceGuiManager guiManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public EssenceCommand(EssenceGuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return true;
        }

        if (!player.hasPermission("essence.gui")) {
            player.sendMessage(Component.text("You don't have permission to use this command."));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("essence.admin")) {
                player.performCommand("mmoessence reload");
                return true;
            }
        }

        if (!guiManager.isGuiAvailable()) {
            player.sendMessage(Component.text("Essence GUI is not available."));
            return true;
        }

        guiManager.openGui(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("essence.admin")) {
            return List.of("reload");
        }
        return List.of();
    }
}
