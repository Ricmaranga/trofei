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

public class AllTrophiesGUI extends Menu {

    public AllTrophiesGUI() {
        super(Lang.get("gui.list"), ConfigManager.getConfig().getInt("showcase-rows"));
    }

    public static void open(Player player) {
        new AllTrophiesGUI().displayTo(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Trophy trophy : TrophyManager.getAllTrophies().values()) {
            if (trophy.getSlot() != -1) {
                Button trophyBtn = Button.builder()
                        .icon(trophy.getMaterial())
                        .name(trophy.getDisplayName())
                        .lore(trophy.getLore())
                        .onClick(e -> {
                            Player p = (Player) e.getWhoClicked();
                            p.closeInventory();
                            TrophyGUI.open(p, trophy);
                        })
                        .build();

                buttons.put(trophy.getSlot(), trophyBtn);
            }
        }

        return buttons;
    }
}