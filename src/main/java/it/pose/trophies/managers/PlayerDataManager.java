package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import it.pose.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private static final Map<UUID, Map<UUID, Boolean>> playersTrophies = new HashMap<>();


    public static void savePlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        Map<UUID, Boolean> trophies = playersTrophies.get(playerId);
        if (trophies == null) return;

        File folder = new File(Trophies.getInstance().getDataFolder(), "playerData");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, playerId + ".yml");
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, Boolean> entry : trophies.entrySet()) {
            config.set("unlocked." + entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save data for " + player.getName());
            e.printStackTrace();
        }
    }

    public static void savePlayerData(UUID playerId, Map<UUID, Boolean> trophies) {
        File folder = new File(Trophies.getInstance().getDataFolder(), "playerData");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, playerId.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, Boolean> entry : trophies.entrySet()) {
            config.set("unlocked." + entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save data for UUID: " + playerId);
            e.printStackTrace();
        }
    }

    public static void loadAll() {
        playersTrophies.clear();

        File folder = new File(Trophies.getInstance().getDataFolder(), "playerData");
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName().replace(".yml", "");
            UUID playerId;
            try {
                playerId = UUID.fromString(fileName);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Skipping invalid player file: " + fileName);
                continue;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("unlocked");
            if (section == null) continue;

            Map<UUID, Boolean> trophies = new HashMap<>();
            for (String trophyId : section.getKeys(false)) {
                try {
                    UUID trophyUUID = UUID.fromString(trophyId);
                    Boolean state = section.getBoolean(trophyId);
                    trophies.put(trophyUUID, state);
                } catch (IllegalArgumentException ignored) {
                    Bukkit.getLogger().warning("Invalid trophy UUID in " + fileName + ": " + trophyId);
                }
            }

            playersTrophies.put(playerId, trophies);
        }

        Bukkit.getLogger().info("[Trophies] Loaded data for " + playersTrophies.size() + " players.");
    }

    public static void saveAll() {
        for (UUID playerId : playersTrophies.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                savePlayerData(player);
            } else {
                savePlayerData(playerId, playersTrophies.get(playerId));
            }
        }
    }

    public static Map<UUID, Boolean> getTrophies(Player player) {
        return playersTrophies.get(player.getUniqueId());
    }

    public void loadPlayer(UUID uuid) {
        Map<UUID, Boolean> trophies = new HashMap<>();
        File file = new File(Trophies.getInstance().getDataFolder(), "playerData/" + uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection section = data.getConfigurationSection("unlocked");
        if (section != null) {
            for (String trophyId : section.getKeys(false)) {
                trophies.put(UUID.fromString(trophyId), section.getBoolean(trophyId));
            }
        }

        playersTrophies.put(uuid, trophies);
    }


    public static void unlockTrophy(Player player, Trophy trophy, boolean placed) {
        Map<UUID, Boolean> data = getTrophies(player);
        data.put(trophy.getUUID(), placed);
        savePlayerData(player);
    }

    public static boolean hasUnlocked(Player player, Trophy trophy) {
        return getTrophies(player).containsKey(trophy.getUUID());
    }

    public static boolean hasPlaced(Player player, Trophy trophy) {
        return getTrophies(player).get(trophy.getUUID());
    }


    public static void markPlaced(Player player, Trophy trophy) {
        Map<UUID, Boolean> data = getTrophies(player);
        if (data.containsKey(trophy.getUUID())) {
            data.put(trophy.getUUID(), true);
            savePlayerData(player);
        }
    }

    public static void removeTrophyFromAllPlayers(UUID trophyId) {
        for (Map.Entry<UUID, Map<UUID, Boolean>> entry : playersTrophies.entrySet()) {
            entry.getValue().remove(trophyId);
            savePlayerData(entry.getKey(), entry.getValue());
        }
    }

    public static void removeTrophy(String player, Trophy trophy) {
        if (playersTrophies.containsKey(Bukkit.getPlayer(player).getUniqueId())) {
            getTrophies(Bukkit.getPlayer(player)).remove(trophy.getUUID());
            playersTrophies.get(Bukkit.getPlayer(player).getUniqueId()).remove(trophy.getUUID());
        }
    }

    public static Map<UUID, Map<UUID, Boolean>> getPlayersTrophies() {
        return playersTrophies;
    }
}