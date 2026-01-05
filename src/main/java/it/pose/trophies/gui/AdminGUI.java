package it.pose.trophies.gui;

import it.pose.trophies.Lang;
import it.pose.trophies.core.Button;
import it.pose.trophies.buttons.Buttons;
import it.pose.trophies.core.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AdminGUI extends Menu {

    private static final String[] color = {"RED", "BLUE", "GREEN", "LIME", "ORANGE", "PINK", "GRAY", "LIGHT_BLUE", "MAGENTA", "PURPLE"};
    // Pick color once
    private static final ItemStack glassPanel = new ItemStack(Material.valueOf(color[(int) (Math.random() * color.length)] + "_STAINED_GLASS_PANE"));

    public AdminGUI() {
        super(Lang.get("gui.admin"), 3);
    }

    public static void open(Player player) {
        new AdminGUI().displayTo(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Button glass = Button.builder()
                .icon(glassPanel)
                .name(" ")
                .build();

        for (int slot = 0; slot < 27; slot++) {
            if (slot <= 9 || slot >= 17) {
                buttons.put(slot, glass);
            }
        }

        buttons.put(12, Buttons.createTrophy());
        buttons.put(14, Buttons.listAllTrophies());
        buttons.put(18, Buttons.closeButton());

        return buttons;
    }
}