package it.pose.trophies.core;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


public abstract class Button {

    public abstract ItemStack getIcon(Player player);

    public abstract void onClick(Player player, Menu menu, InventoryClickEvent event);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ItemStack icon = new ItemStack(Material.STONE);
        private String name = null;
        private List<String> lore = new ArrayList<>();
        private Consumer<InventoryClickEvent> clickHandler;

        public Builder icon(Material material) {
            this.icon = new ItemStack(material);
            return this;
        }

        public Builder icon(ItemStack item) {
            this.icon = item.clone();
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lore(String... lines) {
            this.lore.addAll(Arrays.asList(lines));
            return this;
        }

        public Builder lore(List<String> lines) {
            this.lore.addAll(lines);
            return this;
        }

        public Builder onClick(Consumer<InventoryClickEvent> handler) {
            this.clickHandler = handler;
            return this;
        }

        public Button build() {
            return new Button() {
                @Override
                public ItemStack getIcon(Player player) {
                    ItemStack finalItem = icon.clone();
                    ItemMeta meta = finalItem.getItemMeta();
                    if (meta != null) {
                        if (name != null) meta.setDisplayName(name);
                        if (!lore.isEmpty()) meta.setLore(lore);
                        finalItem.setItemMeta(meta);
                    }
                    return finalItem;
                }

                @Override
                public void onClick(Player player, Menu menu, InventoryClickEvent event) {
                    if (clickHandler != null) {
                        clickHandler.accept(event);
                    }
                }
            };
        }
    }
}