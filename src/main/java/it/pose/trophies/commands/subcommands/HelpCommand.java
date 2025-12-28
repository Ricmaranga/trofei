package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.Trophy;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class HelpCommand extends SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Yields the help page for the plugin";
    }

    @Override
    public String getSyntax() {
        return Lang.get("usage") + ": /" + Lang.get("default-command") + "help";
    }

    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage("§6[§5§lTrophies§r§6]");
        player.sendMessage(Lang.get("help.admin"));
        player.sendMessage(Lang.get("help.delete"));
        player.sendMessage(Lang.get("help.give"));
        player.sendMessage(Lang.get("help.purge"));
        player.sendMessage(Lang.get("help.reload"));
        player.sendMessage(Lang.get("help.remove"));

        TextComponent message = new TextComponent("Hover or click ");
        TextComponent button = new TextComponent("§ehere ");
        TextComponent messagePt2 = new TextComponent("§rto see all the trophies");
        String trophies = Trophies.trophies.
                values().stream().
                map(Trophy::getId).
                collect(Collectors.joining(", "));
        button.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/tell @p " + (trophies.isEmpty() ? "No trophies" : trophies)));
        button.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(Trophies.trophies.
                        values().stream().
                        map(Trophy::getId).
                        collect(Collectors.joining(", "))).create()));
        message.addExtra(button);
        message.addExtra(messagePt2);
        player.spigot().sendMessage(message);
    }
}