package it.pose.trophies;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void checkForUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String latestVersion = reader.readLine();

                if (!plugin.getDescription().getVersion().equalsIgnoreCase(latestVersion)) {
                    plugin.getLogger().warning("A new version is available: " + latestVersion);
                } else {
                    plugin.getLogger().info("No new versions available!");
                }

            } catch (IOException e) {
                plugin.getLogger().warning("Could not check for updates: " + e.getMessage());
            }
        });
    }
}
