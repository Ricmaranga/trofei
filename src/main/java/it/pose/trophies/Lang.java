package it.pose.trophies;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static it.pose.trophies.ColorUtils.colorize;

public class Lang {

    private static final Map<String, FileConfiguration> languages = new HashMap<>();
    private static String activeLang = "en";

    // Regex for Hex colors in the format &#RRGGBB
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static void init(JavaPlugin plugin) {
        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) langFolder.mkdirs();

        List<String> builtinLangs = List.of("lang_en.yml", "lang_fr.yml", "lang_it.yml");

        for (String fileName : builtinLangs) {
            File target = new File(langFolder, fileName);
            if (!target.exists()) {
                plugin.saveResource("languages/" + fileName, false);
            }
        }

        File[] files = langFolder.listFiles((dir, name) -> name.startsWith("lang_") && name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String code = file.getName().replace("lang_", "").replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            languages.put(code.toLowerCase(), config);
        }

        activeLang = plugin.getConfig().getString("language", "en").toLowerCase();
        if (!languages.containsKey(activeLang)) {
            plugin.getLogger().warning("[Trophies] Language not found: " + activeLang + ", falling back to 'en'.");
            activeLang = "en";
        }
    }

    public static String get(String key) {
        FileConfiguration langFile = languages.getOrDefault(activeLang, languages.get("en"));
        String raw = langFile.getString(key);
        // Use the new color() method here
        return colorize(raw != null ? raw : "&cMissing key: " + key);
    }

    // --- MAIN METHOD: Handles the actual replacement logic ---
    public static String get(String key, Object... args) {
        String msg = get(key);
        for (int i = 0; i < args.length; i += 2) {
            String placeholder = String.valueOf(args[i]);
            String val = (i + 1 < args.length && args[i + 1] != null) ? String.valueOf(args[i + 1]) : "";
            msg = msg.replace("%" + placeholder + "%", val);
        }
        return msg;
    }

    public static Message msg(String key) {
        return new Message(get(key));
    }

    public static class Message {
        private String text;

        public Message(String text) {
            this.text = text;
        }

        public Message replace(String placeholder, Object value) {
            String val = value == null ? "" : String.valueOf(value);
            this.text = this.text.replace("%" + placeholder + "%", val);
            return this;
        }

        public Message replace(Trophy trophy) {
            if (trophy == null) return this;
            replace("trophy", trophy.getDisplayName());
            replace("trophyId", trophy.getId());
            replace("trophySlot", trophy.getSlot());
            replace("trophyMaterial", trophy.getMaterial().getType());
            replace("trophyLore", trophy.getLore());
            return this;
        }

        public Message replace(Player player) {
            if (player == null) return this;
            replace("player", player.getName());
            replace("playerDisplayname", player.getDisplayName());
            return this;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public static String getActiveLang() {
        return activeLang;
    }

    public static void reload() {
        init(Trophies.getInstance());
    }
}