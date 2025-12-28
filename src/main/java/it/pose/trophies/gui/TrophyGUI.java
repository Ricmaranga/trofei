package it.pose.trophies.gui;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.core.Button;
import it.pose.trophies.buttons.Buttons;
import it.pose.trophies.core.Menu; // Make sure to import this
import it.pose.trophies.Trophy;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TrophyGUI extends Menu {

    private static final String[] COLORS = {
            "RED", "BLUE", "GREEN", "LIME", "ORANGE",
            "PINK", "GRAY", "LIGHT_BLUE", "MAGENTA", "PURPLE"
    };

    private static final ItemStack GLASS_PANEL =
            new ItemStack(Material.valueOf(COLORS[(int) (Math.random() * COLORS.length)] + "_STAINED_GLASS_PANE"));

    private final Trophy trophy;

    public TrophyGUI(Trophy trophy) {
        super(Lang.get("gui.manage"), 3); // 3 Rows
        this.trophy = trophy;
    }

    public static void open(Player player, Trophy trophy) {
        new TrophyGUI(trophy).displayTo(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Button glassButton = Button.builder()
                .icon(GLASS_PANEL)
                .name(" ")
                .build();

        for (int i = 0; i < 27; i++) {
            buttons.put(i, glassButton);
        }

        buttons.put(0, Button.builder().icon(trophy.createItem()).name(trophy.getDisplayName()).lore(trophy.getLore()).build());

        buttons.put(10, Buttons.setName(trophy));
        buttons.put(12, Buttons.setSlot(trophy));
        buttons.put(14, Buttons.setMaterial(trophy));
        buttons.put(16, Buttons.setLore(trophy));

        buttons.put(18, Buttons.closeButton());
        buttons.put(24, Buttons.goBack());
        buttons.put(26, Buttons.deleteTrophy(trophy));

        return buttons;
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent e) {

        int slot = trophy.getSlot();
        int maxSlot = ConfigManager.getConfig().getInt("showcase-rows") * 9 - 1;

        boolean invalidIndex = slot < 0 || slot > maxSlot;
        boolean occupied = !invalidIndex
                && TrophyManager.isSlotOccupied(slot, trophy.getUUID());

        if ((invalidIndex || occupied)) {
            e.getPlayer().sendMessage(Lang.get("trophy.invalidSlot"));
            Bukkit.getScheduler().runTaskLater(
                    Trophies.getInstance(),
                    () -> TrophyGUI.open((Player) e.getPlayer(), trophy),
                    1L
            );
        }
    }
}