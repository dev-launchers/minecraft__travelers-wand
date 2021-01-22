package devlaunchers.eddisond.travelerswand;

import org.bukkit.plugin.java.JavaPlugin;

public final class TravelersWand extends JavaPlugin {

    private static TravelersWand plugin;
    public static TravelersWand getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        /* Create plugin data folder, if it doesn't exist yet */
        if(!plugin.getDataFolder().exists()) {
            try {
                plugin.getDataFolder().mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
