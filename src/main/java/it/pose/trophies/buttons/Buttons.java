package it.pose.trophies.buttons;

import it.pose.trophies.*;
import it.pose.trophies.core.Button;
import it.pose.trophies.core.Menu;
import it.pose.trophies.gui.AdminGUI;
import it.pose.trophies.gui.AllTrophiesGUI;
import it.pose.trophies.gui.TrophyGUI;
import it.pose.trophies.inputs.ChatInputRegistry;
import it.pose.trophies.listeners.EventListener;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.List;

public class Buttons {

    public static Button closeButton() {
        return Button.builder()
                .icon(Material.BARRIER)
                .name("§cClose menu")
                .onClick(e -> e.getWhoClicked().closeInventory())
                .build();
    }

    public static Button createTrophy() {
        return Button.builder()
                .icon(Material.BAMBOO)
                .name("§5§lCreate trophy")
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    player.closeInventory();
                    player.sendMessage("Set the trophy ID");
                    player.sendMessage("This ID will be used to give the trophy to the players");
                    Trophy trophy = new Trophy();
                    ChatInputRegistry.waitFor(
                            player,
                            List.of("id"),
                            input -> {
                                trophy.setId(input);
                                player.sendMessage(Lang.msg("trophy.id").replace(trophy).toString());

                                TrophyGUI.open(player, trophy);

                                TrophyManager.saveTrophy(trophy);
                            });
                })
                .build();
    }

    public static Button listAllTrophies() {
        return Button.builder()
                .icon(Material.BOOK)
                .name(Lang.msg("gui.list").toString())
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    player.closeInventory();
                    AllTrophiesGUI.open(player);
                })
                .build();
    }

    public static Button setName(Trophy trophy) {
        return Button.builder()
                .icon(Material.NAME_TAG)
                .name(Lang.msg("buttons.setName.name").replace(trophy).toString())
                .lore(Lang.msg("buttons.setName.lore").replace(trophy).toString())
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu menu) menu.ignoreNextClose();
                    player.closeInventory();

                    ChatInputRegistry.waitFor(
                            player,
                            List.of("name"),
                            input -> {
                                trophy.setDisplayName(ColorUtils.colorize(input));
                                trophy.markDirty();
                                player.sendMessage(Lang.msg("trophy.name").replace(trophy).toString());
                                Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> TrophyGUI.open(player, trophy));
                            });
                })
                .build();
    }

    public static Button setSlot(Trophy trophy) {
        return Button.builder()
                .icon(Material.REDSTONE_TORCH)
                .name(Lang.get("buttons.setSlot.name"))
                .lore(Lang.msg("buttons.setSlot.lore").replace(trophy).toString())
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu menu) menu.ignoreNextClose();
                    player.closeInventory();
                    ChatInputRegistry.waitFor(
                            player,
                            List.of("slot"),
                            input -> {
                                try {
                                    int slot = Integer.parseInt(input);
                                    if (TrophyManager.checkSlot(slot)) {
                                        player.sendMessage(Lang.get("buttons.setSlot.invalid"));
                                    } else {
                                        trophy.setSlot(slot);
                                        player.sendMessage(Lang.msg("trophy.slot").replace(trophy).toString());
                                        TrophyManager.saveTrophy(trophy);
                                    }
                                } catch (NumberFormatException ex) {
                                    player.sendMessage("Number not valid");
                                    TrophyGUI.open(player, trophy);
                                }
                                Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> TrophyGUI.open(player, trophy));
                            });
                })
                .build();
    }

    public static Button setMaterial(Trophy trophy) {
        return Button.builder()
                .icon(trophy.getMaterial())
                .name(Lang.get("buttons.setMaterial.name"))
                .lore(Lang.get("buttons.setMaterial.lore"))
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    ItemStack cursor = e.getCursor();

                    if (cursor == null || cursor.getType().isAir()) {
                        player.sendMessage(Lang.get("buttons.setMaterial.failed"));
                        return;
                    }

                    ItemStack raw = cursor.clone();
                    ItemMeta meta = raw.getItemMeta();
                    if (meta != null && meta.hasLore()) {
                        List<String> lore = meta.getLore();
                        if (lore.remove(Lang.msg("buttons.setMaterial.lore").replace(trophy).toString())) {
                            meta.setLore(lore);
                        }
                    }
                    raw.setItemMeta(meta);
                    trophy.setItem(raw);

                    String serialized;
                    try {
                        serialized = ItemSerialization.itemStackToBase64(raw);
                    } catch (IOException ioe) {
                        player.sendMessage("§cFailed to serialize item for storage.");
                        ioe.printStackTrace();
                        return;
                    }
                    ConfigManager.getConfig().set("trophies." + trophy.getUUID() + ".item", serialized);

                    TrophyManager.saveTrophy(trophy);

                    player.sendMessage(Lang.msg("trophy.material").replace(trophy).toString());

                    Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> TrophyGUI.open(player, trophy));
                })
                .build();
    }

    public static Button setLore(Trophy trophy) {
        return Button.builder()
                .icon(Material.PAPER)
                .name(Lang.get("buttons.setLore.name"))
                .lore(Lang.get("buttons.setLore.lore"))
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu menu) menu.ignoreNextClose();
                    player.closeInventory();

                    ChatInputRegistry.waitFor(
                            player,
                            List.of("lore"),
                            input -> {
                                List<String> lore = List.of(ChatColor.translateAlternateColorCodes('&', input).split("\\\\"));
                                trophy.setLore(lore);
                                trophy.markDirty();
                                player.sendMessage(Lang.msg("trophy.lore").toString());
                                Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> TrophyGUI.open(player, trophy));
                            });

                })
                .build();
    }

    public static Button goBack() {
        return Button.builder()
                .icon(Material.BLAZE_ROD)
                .name(Lang.get("buttons.back"))
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    player.closeInventory();
                    AdminGUI.open(player);
                })
                .build();
    }

    public static Button deleteTrophy(Trophy trophy) {
        return Button.builder()
                .icon(Material.BARRIER)
                .name(Lang.msg("buttons.delete.name").toString())
                .lore(Lang.msg("buttons.delete.lore").toString())
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu menu) menu.ignoreNextClose();
                    EventListener.finalizingTrophies.add(player.getUniqueId());
                    TrophyManager.deleteTrophy(trophy);
                    player.sendMessage(Lang.msg("trophy.deleted").toString());
                    player.closeInventory();
                })
                .build();
    }
}