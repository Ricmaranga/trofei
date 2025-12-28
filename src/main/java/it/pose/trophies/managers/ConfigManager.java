package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private static FileConfiguration config;

    private static File trophiesFile;
    private static FileConfiguration trophiesConfig;

    public ConfigManager() {
        reloadConfig();
    }

    public static void init(JavaPlugin plugin) {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        trophiesFile = new File(plugin.getDataFolder(), "trophies.yml");
        if (!trophiesFile.exists()) {
            try {
                trophiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        trophiesConfig = YamlConfiguration.loadConfiguration(trophiesFile);
    }

    public void reloadConfig() {
        File configFile = new File(Trophies.getInstance().getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        config.options().copyDefaults(true);
    }

    public static FileConfiguration getTrophiesConfig() {
        return trophiesConfig;
    }

    public static void saveTrophiesConfig() {
        try {
            trophiesConfig.save(trophiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadTrophiesConfig() {
        trophiesConfig = YamlConfiguration.loadConfiguration(trophiesFile);
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}