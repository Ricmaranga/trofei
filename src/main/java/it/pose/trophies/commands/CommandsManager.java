package it.pose.trophies.commands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.subcommands.*;
import it.pose.trophies.gui.ShowcaseGUI;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CommandsManager implements CommandExecutor, TabExecutor {

    private final ArrayList<SubCommand> subcommand = new ArrayList<>();
    private final Logger log = Trophies.getInstance().getLogger();
    private final ArrayList<SubCommand> consoleSubcommands = new ArrayList<>();

    public CommandsManager() {
        subcommand.add(new ReloadCommand());
        subcommand.add(new PurgeCommand());
        subcommand.add(new AdminCommand());
        subcommand.add(new GiveCommand());
        subcommand.add(new RemoveCommand());
        subcommand.add(new HelpCommand());
        subcommand.add(new DeleteCommand());
        subcommand.add(new PlayerCommand());
        subcommand.add(new ListCommand());

        consoleSubcommands.add(new GiveCommand());
        consoleSubcommands.add(new RemoveCommand());
        consoleSubcommands.add(new DeleteCommand());
        consoleSubcommands.add(new ReloadCommand());
        consoleSubcommands.add(new PurgeCommand());
        consoleSubcommands.add(new ListCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 1. Check for Restart Command FIRST
        if (args.length > 0 && args[0].equalsIgnoreCase("restart")) {
            if (!sender.hasPermission("trophies.admin")) {
                sender.sendMessage("§cNo permission.");
                return true;
            }

            sender.sendMessage("§eAttempting to restart Trophies...");
            Trophies.getInstance().loadPluginPublic();

            if (!Trophies.getInstance().getEmergencyMode()) {
                sender.sendMessage("§aTrophies restarted successfully!");
            } else {
                sender.sendMessage("§cRestart failed. Check console for errors.");
            }
            return true;
        }

        if (Trophies.getInstance().getEmergencyMode()) {
            sender.sendMessage("§c§l[!] Trophies is in Emergency Mode due to a critical error.");
            sender.sendMessage("§cThe probable error is given by a trophy's slot which is not valid, check that.");
            sender.sendMessage("§cMost of the time that slot goes over the Trophies GUI limit.");
            sender.sendMessage("§cType §7/trophies restart§c to try again.");
            return true;
        }

        if (sender instanceof Player player) {

            if (args.length == 0) {
                ShowcaseGUI.open(player);
                return true;
            }

            if (!player.hasPermission("trophies.admin")) {
                Lang.get("player.no-permission");
                return true;
            }

            for (SubCommand sub : subcommand) {
                if (sub.getName().equalsIgnoreCase(args[0])) {
                    sub.perform(player, args);
                    return true;
                }
            }

            player.sendMessage(Lang.get("player.invalid-command"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("trophies.admin")) {
            return null;
        }

        int length = args.length;

        if (length == 1) {
            return subcommand.stream()
                    .map(SubCommand::getName)
                    .toList();
        }

        if (length == 2) {
            String firstArg = args[0];
            if (firstArg.equals("give") || firstArg.equals("purge") || firstArg.equals("remove")) {
                return Trophies.getInstance()
                        .getServer()
                        .getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .toList();
            }
            return Collections.emptyList();
        }

        if (length == 3) {
            String firstArg = args[0];

            if (firstArg.equals("give")) {
                return Trophies.trophies.values()
                        .stream()
                        .map(Trophy::getId)
                        .toList();
            }

            if (firstArg.equals("remove")) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    return Collections.emptyList();
                }
                return PlayerDataManager.getTrophies(target)
                        .keySet()
                        .stream()
                        .map(TrophyManager::getTrophy)
                        .filter(Objects::nonNull)
                        .map(Trophy::getId)
                        .distinct()
                        .toList();
            }

            return Collections.emptyList();
        }

        return null;
    }
}
