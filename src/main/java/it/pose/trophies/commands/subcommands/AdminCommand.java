package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.gui.AdminGUI;
import org.bukkit.entity.Player;

public class AdminCommand extends SubCommand {
    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return Lang.get("usage") + " /" + Lang.get("default-command") + " admin";
    }

    @Override
    public void perform(Player player, String[] args) {
        AdminGUI.open((Player) player);
    }
}
