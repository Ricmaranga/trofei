package it.pose.trophies.inputs;

import it.pose.trophies.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputRegistry {

    private static class Pending {
        final String prompt;
        final Consumer<String> callback;

        Pending(String prompt, Consumer<String> cb) {
            this.prompt = prompt;
            this.callback = cb;
        }
    }

    private static final Map<UUID, Pending> listeners = new HashMap<>();

    public static void waitFor(Player player, List<String> prompts, Consumer<String> callback) {
        for (String prompt : prompts) {
            listeners.put(player.getUniqueId(), new Pending(prompt, callback));
            player.sendMessage(ChatColor.YELLOW + Lang.get("input." + prompt));
        }
        player.sendMessage(Lang.get("input.cancel"));
    }

    public static void handle(Player player, String message) {
        Pending p = listeners.remove(player.getUniqueId());
        if (p == null) return;

        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Lang.get("input.canceled"));
        } else {
            p.callback.accept(message);
        }
    }

    public static boolean has(Player player) {
        return listeners.containsKey(player.getUniqueId());
    }
}