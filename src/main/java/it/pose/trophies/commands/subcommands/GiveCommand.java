package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GiveCommand extends SubCommand {

    private final Trophies main = Trophies.getInstance();
    private final TrophyManager trophyManager = main.getTrophyManager();

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give a trophy to a player";
    }

    @Override
    public String getSyntax() {
        return Lang.get("usage") + " /" + Lang.get("default-command") + "give <player> <trophyName>";
    }

    @Override
    public void perform(Player player, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§cPlayer not found!");
        }

        UUID uuid = Trophies.getInstance().getTrophyManager().getUUIDById(args[2]);
        Trophy trophy = TrophyManager.getTrophy(uuid);

        if (trophy == null) {
            player.sendMessage("§cTrophy not found!");
        }

        trophyManager.awardTrophyToHotbar(target, uuid);

        PlayerDataManager.unlockTrophy(target, trophy, false);

        player.sendMessage(Lang.msg("command.give").replace(trophy).replace(target).toString());
    }
}
