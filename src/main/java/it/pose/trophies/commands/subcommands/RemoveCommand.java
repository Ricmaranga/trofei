package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophy;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


public class RemoveCommand extends SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return Lang.get("usage") + " /" + Lang.get("default-command") + "remove <player> <trophyId>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cUsage: /trophies remove <player> <name>");
            return;
        }

        Trophy trophy = TrophyManager.getTrophyByName(args[2]);
        ItemStack item = trophy.createItem();
        if (trophy  != null) {
            PlayerDataManager.removeTrophy(Bukkit.getPlayer(args[1]), TrophyManager.getTrophyByName(args[2]));
            if (player.getInventory().contains(item)) { player.getInventory().remove(item); }
            player.sendMessage(Lang.msg("command.remove").replace(trophy).replace(player).toString());
        } else {
            player.sendMessage(Lang.msg("trophy.inexistent").toString());
        }
    }

}