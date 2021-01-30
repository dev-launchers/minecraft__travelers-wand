package devlaunchers.eddisond.travelerswand;

import org.bukkit.plugin.java.JavaPlugin;

public final class TravelersWand extends JavaPlugin {

    private static TravelersWand plugin;
    public static TravelersWand getPlugin() {
        return plugin;
    }

    static PlayerData playerData;
    static PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public void onEnable(){
        plugin = this;
        playerData = new PlayerData();

        // Creates the plugins data folder "TravelersWand", if it doesn't exist yet
        if(!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdirs();
        }

        // Register plugin listener(s)
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {}
}
