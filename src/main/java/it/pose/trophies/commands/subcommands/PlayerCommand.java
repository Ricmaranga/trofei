package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Trophies;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.gui.ShowcaseGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand extends SubCommand {
    @Override
    public String getName() {
        return "player";
    }

    @Override
    public String getDescription() {
        return "See another player's collection";
    }

    @Override
    public String getSyntax() {
        return "/trophies player <playerName>";
    }

    @Override
    public void perform(Player player, String[] args) {
        ShowcaseGUI.open(Trophies.getInstance().getServer().getPlayer(args[1]));
    }
}