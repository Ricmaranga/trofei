package it.pose.trophies.commands.subcommands;

import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.entity.Player;

public class ListCommand extends SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/trophies list";
    }

    @Override
    public void perform(Player player, String[] args) {
            player.sendMessage(TrophyManager.getAllTrophiesName().toString());
    }
}
