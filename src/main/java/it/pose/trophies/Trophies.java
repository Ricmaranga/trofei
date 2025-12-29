package it.pose.trophies;

import it.pose.trophies.commands.CommandsManager;
import it.pose.trophies.listeners.EventListener;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Trophies extends JavaPlugin {

    private static Trophies instance;
    private boolean emergencyMode = false;

    private ConfigManager configManager;
    private TrophyManager trophyManager;
    private PlayerDataManager playerDataManager;

    public static Map<UUID, Trophy> trophies = new HashMap<>();

    @Override
    public void onEnable() {

        // "=^.^=" \\

        instance = this;
        loadPlugin();

    }

   @Override
   public void onDisable(){

   }

    private void loadPlugin() {

        try {
            emergencyMode = false;

            saveResource("config.yml", false);
            saveResource("trophies.yml", false);
            saveDefaultConfig();

            configManager = new ConfigManager();
            trophyManager = new TrophyManager();
            playerDataManager = new PlayerDataManager();

            PlayerDataManager.loadAll();

            configManager.reloadConfig();
            trophyManager.reloadTrophies();

            ConfigManager.init(this);
            TrophyManager.loadTrophies();

            trophies = TrophyManager.getAllTrophies();

            for (Trophy trophy : trophies.values()){
                if (trophy.getSlot() > getConfig().getInt("showcase-rows") * 9 - 1){
                    getLogger().severe("There is at least a trophy whose slot is not available due to the GUI size");
                    getLogger().severe("Fix the issue and reload the plugin by doing /trophies restart");
                    Bukkit.getPluginManager().disablePlugin(this);
                    shutdown("Incorrect trophy slot");
                }
            }

            getCommand("trophies").setExecutor(new CommandsManager());
            getServer().getPluginManager().registerEvents(new EventListener(), this);

            Lang.init(this);

            new Metrics(this, 25989);
            new UpdateChecker(this, 125457).checkForUpdate();

            getLogger().info("Trophies enabled successfully!");

        } catch (Exception e) {
            shutdown("Failed to startup correctly");
        }
    }


    public static void shutdown(String reason) {

        Trophies instance = getInstance();

        instance.getLogger().severe("========================================");
        instance.getLogger().severe("CRITICAL ERROR: " + reason);
        instance.getLogger().severe("Plugin entering EMERGENCY MODE.");
        instance.getLogger().severe("Use '/trophies restart' to attempt a reload.");
        instance.getLogger().warning("Most of the time this error is due an invalid trophy slot");
        instance.getLogger().warning("Check in the trophies file that every slot is ok");
        instance.getLogger().severe("========================================");

        instance.emergencyMode = true;

        HandlerList.unregisterAll(instance);
    }

    public static Trophies getInstance() {
        return instance;
    }

    public TrophyManager getTrophyManager() {
        return trophyManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public boolean getEmergencyMode(){
        return emergencyMode;
    }

    public void loadPluginPublic(){
        loadPlugin();
    }
}