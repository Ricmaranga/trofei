package it.pose.trophies;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


public class Trophy implements ConfigurationSerializable {
    private UUID uuid;
    private String id;
    private String displayName;
    private ItemStack item;
    private List<String> lore;
    private Integer slot;
    private transient boolean dirty = false;
    private static final Trophies main = Trophies.getInstance();

    public Trophy() {
        this.uuid = UUID.randomUUID();
        this.id = "Unnamed Trophy";
        this.item = new ItemStack(Material.PAPER);
        this.lore = new ArrayList<>();
        this.slot = -1;
    }

    public Trophy(UUID uuid, String id, String displayName, ItemStack item, List<String> lore, int slot) {
        this.uuid = uuid;
        this.id = id;
        this.displayName = displayName;
        this.item = item;
        this.lore = lore;
        this.slot = slot;
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("id", id);
        map.put("displayName", displayName);

        try {
            String b64 = ItemSerialization.itemStackToBase64(item);
            map.put("item", b64);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to serialize trophy head", ex);
        }

        map.put("slot", slot);
        map.put("lore", lore);
        return map;
    }

    public static Trophy deserialize(Map<String, Object> map) {
        Trophy trophy = new Trophy();

        if (map.containsKey("uuid")) {
            trophy.setUUID(UUID.fromString((String) map.get("uuid")));
        }
        if (map.containsKey("id")) {
            trophy.setId((String) map.get("id"));
        }
        if (map.containsKey("displayName")) {
            trophy.setDisplayName((String) map.get("displayName"));
        }
        if (map.containsKey("slot")) {
            trophy.setSlot((int) map.get("slot"));
        }
        if (map.containsKey("lore")) {
            trophy.setLore((List<String>) map.get("lore"));
        }

        Object itemObj = map.get("item");

        if (itemObj instanceof String) {
            try {
                ItemStack deserializedItem = ItemSerialization.itemStackFromBase64((String) itemObj);
                trophy.setItem(deserializedItem);
            } catch (Exception e) {
                e.printStackTrace();
                trophy.setItem(new ItemStack(Material.STONE));
            }
        } else if (itemObj instanceof ItemStack) {
            trophy.setItem((ItemStack) itemObj);
        } else {
            trophy.setItem(new ItemStack(Material.STONE));
        }

        return trophy;
    }

    public static Trophy fromItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        String displayName = meta.hasDisplayName() ? ChatColor.stripColor(meta.getDisplayName()) : "Unnamed Trophy";
        String id = displayName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        Trophy trophy = new Trophy();

        NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "trophy-uuid");
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            UUID uuid = UUID.fromString(meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
            trophy.setUUID(uuid);
        } else {
            trophy.setUUID(UUID.randomUUID());
        }

        trophy.setUUID(UUID.randomUUID());
        trophy.setId(id);
        trophy.setDisplayName(displayName);
        trophy.setItem(item);
        trophy.setLore(lore);
        trophy.setSlot(-1);

        return trophy;
    }

    public ItemStack toItemStack() {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ColorUtils.colorize(displayName));
        meta.setLore(this.lore);

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore.stream()
                    .map(ColorUtils::colorize)
                    .toList());
        }

        meta.addEnchant(Enchantment.LOYALTY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public void setId(String id) {
        if (!this.id.equals(id)) {
            this.id = id;
            this.displayName = id;
            markDirty();
        }
    }

    public void setItem(ItemStack item) {
        if (!Objects.equals(this.item, item)) {
            this.item = item.clone();
            markDirty();
        }
    }

    public void setSlot(int slot) {
        if (this.slot != slot) {
            this.slot = slot;
            markDirty();
        }
    }

    public void setLore(List<String> lore) {
        if (!this.lore.equals(lore)) {
            this.lore = new ArrayList<>(lore);
            markDirty();
        }
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        markDirty();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        markDirty();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirtyFlag() {
        this.dirty = false;
    }

    public ItemStack createItem() {
        ItemMeta meta = item.getItemMeta();

        NamespacedKey key = new NamespacedKey(main, "trophy-uuid");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());

        meta.setDisplayName(ColorUtils.colorize(displayName));

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore.stream()
                    .map(ColorUtils::colorize)
                    .toList());
        } else {
            meta.setLore(this.lore);
        }

        meta.addEnchant(Enchantment.LOYALTY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getId() {
        return this.id;
    }

    public ItemStack getMaterial() {
        return this.item;
    }

    public List<String> getLore() {
        return new ArrayList<>(this.lore);
    }

    public Integer getSlot() {
        return this.slot;
    }

    public String getDisplayName() {
        return this.displayName;
    }

}
