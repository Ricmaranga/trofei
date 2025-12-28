package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;

public class PurgeCommand extends SubCommand {
    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "See another player's collection";
    }

    @Override
    public String getSyntax() {
        return "/trophies purge <playerName>";
    }

    @Override
    public void perform(Player player, String[] args) {
        File playerData = new File(Trophies.getInstance().getDataFolder(), "playerData/" + Bukkit.getOfflinePlayer(args[1]).getUniqueId() + ".yml");
        if (playerData.exists()) {
            playerData.delete();
            PlayerDataManager.getPlayersTrophies().remove(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
        }
        player.sendMessage(Lang.get("command.purge", Map.of("player", args[1])));
    }
}
