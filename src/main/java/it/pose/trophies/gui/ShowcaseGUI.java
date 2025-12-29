package it.pose.trophies.gui;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.core.Button;
import it.pose.trophies.core.Menu;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.Trophy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShowcaseGUI extends Menu {

    public ShowcaseGUI() {
        super(Lang.get("gui.showcase"), ConfigManager.getConfig().getInt("showcase-rows"));
    }

    public static void open(Player player) {
        new ShowcaseGUI().displayTo(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int dim = ConfigManager.getConfig().getInt("showcase-rows") * 9;

        for (int slot = 0; slot < dim; slot++) {
            Trophy trophy = TrophyManager.getTrophy(slot);

            // Case 1: No trophy here, show locked item
            if (trophy == null) {
                buttons.put(slot, getLockedButton(slot));
                continue;
            }

            // Case 2: Trophy exists, but player hasn't unlocked it
            if (!PlayerDataManager.hasUnlocked(player, trophy)) {
                buttons.put(slot, getLockedButton(slot));
            }

            // Case 3: Player has ALREADY placed it
            else if (PlayerDataManager.hasPlaced(player, trophy)) {
                // Just a visual button, no click action needed (or maybe a message)
                buttons.put(slot, Button.builder()
                        .icon(trophy.toItemStack()) // Assuming this returns the item with proper meta
                        .name(trophy.getDisplayName())
                        .lore(trophy.getLore())
                        .build());
            }

            // Case 4: Player unlocked it, but hasn't placed it yet
            else {
                ItemStack needed = trophy.toItemStack();

                // Check if they have the item in inventory
                if (player.getInventory().containsAtLeast(needed, 1)) {
                    buttons.put(slot, getPlaceTrophyButton(trophy));
                } else {
                    // Grey pane showing "You don't have this trophy"
                    buttons.put(slot, Button.builder()
                            .icon(Material.GRAY_STAINED_GLASS_PANE)
                            .name("§7" + trophy.getDisplayName())
                            .lore("§8You don’t have this trophy in your inv")
                            .build());
                }
            }
        }
        return buttons;
    }

    private Button getLockedButton(int slot) {
        FileConfiguration config = Trophies.getInstance().getConfig();
        String path = "slots." + slot;
        String materialName = config.getString(path, "BARRIER");
        Material material = Material.matchMaterial(materialName);
        if (material == null) material = Material.BARRIER;

        return Button.builder()
                .icon(material)
                .name(Lang.msg("player.lockedTrophy").toString())
                .build();
    }

    private Button getPlaceTrophyButton(Trophy trophy) {
        return Button.builder()
                .icon(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .name(trophy.getDisplayName())
                .lore(Lang.get("buttons.place.lore"))
                .onClick(e -> {
                    Player player = (Player) e.getWhoClicked();
                    ItemStack needed = trophy.toItemStack();

                    if (!player.getInventory().containsAtLeast(needed, 1)) {
                        player.sendMessage(Lang.get("player.noTrophyInInv"));
                        return;
                    }

                    if (PlayerDataManager.hasPlaced(player, trophy)) return;

                    player.getInventory().removeItem(needed);
                    PlayerDataManager.markPlaced(player, trophy);

                    player.sendMessage(Lang.get("trophy.placed", Map.of("trophy", trophy.getDisplayName())));

                    new ShowcaseGUI().displayTo(player);
                })
                .build();
    }
}