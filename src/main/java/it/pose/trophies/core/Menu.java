package it.pose.trophies.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

/**
 * The abstract container for buttons.
 * Any GUI in your plugin (like AdminGUI, TrophyGUI) should extend this class.
 */
public abstract class Menu implements InventoryHolder {

    private final String title;
    private final int size;

    private boolean ignoreClose = false;

    public Menu(String title, int rows) {
        this.title = title;
        this.size = rows * 9;
    }

    public abstract Map<Integer, Button> getButtons(Player player);

    public void displayTo(Player player) {
        Inventory inv = Bukkit.createInventory(this, size, title);
        Map<Integer, Button> buttons = getButtons(player);

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            if (entry.getKey() < size && entry.getValue() != null) {
                inv.setItem(entry.getKey(), entry.getValue().getIcon(player));
            }
        }
        player.openInventory(inv);
    }

    public void refreshContent(Player player) {
        if (player.getOpenInventory().getTopInventory().getHolder() != this) return;

        Inventory inv = player.getOpenInventory().getTopInventory();
        Map<Integer, Button> buttons = getButtons(player);

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            if (entry.getKey() < size && entry.getValue() != null) {
                inv.setItem(entry.getKey(), entry.getValue().getIcon(player));
            }
        }
    }

    public void handleMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        event.setCancelled(true); // Default behavior

        Map<Integer, Button> buttons = getButtons(player);
        int slot = event.getSlot();

        if (buttons.containsKey(slot)) {
            buttons.get(slot).onClick(player, this, event);
        }
    }

    public void ignoreNextClose() {
        this.ignoreClose = true;
    }

    /**
     * Internal check used by the GuiListener.
     */
    public boolean shouldIgnoreClose() {
        return ignoreClose;
    }

    public void onClose(Player player, InventoryCloseEvent event) { }

    @Override
    public Inventory getInventory() {
        return null; // Not used directly
    }
}