package it.pose.trophies.gui;

import it.pose.trophies.Lang;
import it.pose.trophies.core.Button;
import it.pose.trophies.core.Menu;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.Trophy;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NoSlotTrophiesGUI extends Menu {

    public NoSlotTrophiesGUI() {
        super(Lang.get("gui.noSlotList-title"), ConfigManager.getConfig().getInt("showcase-rows"));
    }

    public static void open(Player player) {
        new NoSlotTrophiesGUI().displayTo(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (Trophy trophy : TrophyManager.getAllTrophies().values()) {
            if (trophy.getSlot() == -1) {
                // Create the button dynamically
                Button trophyBtn = Button.builder()
                        .icon(trophy.getMaterial())
                        .name(trophy.getDisplayName())
                        .lore(trophy.getLore())
                        .onClick(e -> {
                            Player p = (Player) e.getWhoClicked();
                            p.closeInventory(); // Optional, displayTo usually handles switch
                            TrophyGUI.open(p, trophy); // USE NEW OPEN METHOD
                        })
                        .build();

                buttons.put(index, trophyBtn);
                index++;
            }
        }

        return buttons;
    }
}