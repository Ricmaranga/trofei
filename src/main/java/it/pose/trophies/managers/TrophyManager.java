package it.pose.trophies.managers;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class TrophyManager {


    private static final Map<Integer, Trophy> trophiesBySlot = new HashMap<>();

    public TrophyManager() {
        reloadTrophies();
    }

    public static Trophy getTrophyByName(String input) {
        for (Trophy trophy : getAllTrophies().values()) {
            if (Objects.equals(trophy.getId(), input)) {
                return Trophies.trophies.get(trophy.getUUID());
            }
        }
        return null;
    }

    public static Trophy getTrophy(UUID uuid) {
        return Trophies.trophies.get(uuid);
    }

    public static Trophy getTrophy(int slot) {
        for (Trophy trophy : Trophies.trophies.values()) {
            if (trophy.getSlot() != null && trophy.getSlot() == slot) {
                return trophy;
            }
        }
        return null;
    }

    public UUID getUUIDById(String id) {
        for (Trophy trophy : Trophies.trophies.values()) {
            if (trophy.getId().equals(id)) return trophy.getUUID();
        }
        return null;
    }

    public static void saveTrophy(Trophy trophy) {
        FileConfiguration config = ConfigManager.getTrophiesConfig();
        String key = trophy.getUUID().toString();

        config.set("trophies." + key, trophy.serialize());

        ConfigManager.saveTrophiesConfig();

        if (!trophiesBySlot.containsKey(trophy.getSlot())) trophiesBySlot.put(trophy.getSlot(), trophy);
        if (!Trophies.trophies.containsKey(trophy.getUUID())) Trophies.trophies.put(trophy.getUUID(), trophy);

        if (trophy.isDirty()) trophy.clearDirtyFlag();
    }

    public static UUID getUUIDFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "trophy-uuid");

        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            try {
                return UUID.fromString(meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return null;
    }


    public static void loadTrophies() {
        Trophies.trophies.clear();
        trophiesBySlot.clear();

        FileConfiguration config = ConfigManager.getTrophiesConfig();

        if (!config.isConfigurationSection("trophies")) return;

        ConfigurationSection section = config.getConfigurationSection("trophies");

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection trophyData = section.getConfigurationSection(key);

                Trophy trophy = Trophy.deserialize(trophyData.getValues(false));

                Trophies.trophies.put(uuid, trophy);
                trophiesBySlot.put(trophy.getSlot(), trophy);

            } catch (Exception e) {
                Bukkit.getLogger().warning("[Trophies] Failed to load trophy with key: " + key);
                e.printStackTrace();
            }
        }

        checkSlotConflicts();

        Bukkit.getLogger().info("[Trophies] Loaded " + Trophies.trophies.size() + " trophies.");
    }

    public void awardTrophy(Player player, UUID trophyUUID) {

        Trophy trophy = getTrophy(trophyUUID);

        ItemStack trophyItem = trophy.createItem();

        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(trophyItem);

        if (!leftovers.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), trophyItem);
            player.sendMessage("§cInventory full! Trophy dropped at your feet.");
        } else {
            player.sendMessage(Lang.msg("trophy.given").replace(trophy).toString());
        }

        player.updateInventory();

    }

    public void awardTrophyToHotbar(Player player, UUID trophyUUID) {

        Trophy trophy = getTrophy(trophyUUID);

        if (trophy != null) {
            ItemStack trophyItem = trophy.createItem();
            PlayerInventory inv = player.getInventory();

            for (int i = 0; i < 9; i++) {
                if (inv.getItem(i) == null) {
                    inv.setItem(i, trophyItem);
                    player.updateInventory();
                    return;
                }
            }

            awardTrophy(player, trophyUUID);
        }
    }

    public static void deleteTrophy(Trophy trophy) {
        UUID id = trophy.getUUID();
        int slot = trophy.getSlot();

        Trophies.trophies.remove(id);
        trophiesBySlot.remove(slot);

        FileConfiguration config = ConfigManager.getTrophiesConfig();
        config.set("trophies." + id.toString(), null);
        ConfigManager.saveTrophiesConfig();

        PlayerDataManager.removeTrophyFromAllPlayers(id);
    }

    public static Map<UUID, Trophy> getAllTrophies() {
        return Trophies.trophies;
    }

    public static List<String> getAllTrophiesName(){
        return Trophies.trophies.values().stream().map(trophy -> trophy.getId() + " §r(" + trophy.getDisplayName() + "§r) ").toList();
    }

    public void reloadTrophies() {
        File trophies = new File(Trophies.getInstance().getDataFolder(), "trophies.yml");
        FileConfiguration trophiesFile = YamlConfiguration.loadConfiguration(trophies);
        trophiesFile.options().copyDefaults(true);
    }

    public static boolean checkSlot(int slot) {
        return (trophiesBySlot.containsKey(slot) || !((slot >= 0) && (slot <= ConfigManager.getConfig().getInt("showcase-rows") * 9)));
    }

    public static boolean isSlotOccupied(int slot, UUID exclude) {
        return Trophies.trophies.values().stream()
                .anyMatch(t -> t.getSlot() == slot && !t.getUUID().equals(exclude));
    }

    private static void checkSlotConflicts() {
        Map<Integer, List<Trophy>> slotMap = new HashMap<>();

        for (Trophy trophy : Trophies.trophies.values()) {
            slotMap.computeIfAbsent(trophy.getSlot(), s -> new ArrayList<>()).add(trophy);
        }

        for (Map.Entry<Integer, List<Trophy>> entry : slotMap.entrySet()) {
            List<Trophy> list = entry.getValue();
            if (list.size() > 1) {
                Bukkit.getLogger().warning("[Trophies] Slot conflict at slot " + entry.getKey() + ":");
                for (Trophy trophy : list) {
                    Bukkit.getLogger().warning(" - " + trophy.getDisplayName() + " (" + trophy.getUUID() + ")");
                }
            }
        }
    }
}