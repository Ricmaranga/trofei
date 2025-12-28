package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin";
    }

    @Override
    public String getSyntax() {
        return "/trophies reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        Trophies.getInstance().reloadConfig();
        Lang.reload();
        ConfigManager.reloadTrophiesConfig();
        TrophyManager.loadTrophies();
        PlayerDataManager.loadAll();
        player.sendMessage("§6[§5§lTrophies§r§5]");
        player.sendMessage("§a✔ Config reloaded");
        player.sendMessage("§a✔ Language loaded: §e" + Lang.getActiveLang());
        player.sendMessage("§a✔ Trophies loaded: §e" + TrophyManager.getAllTrophies().size());
    }
}