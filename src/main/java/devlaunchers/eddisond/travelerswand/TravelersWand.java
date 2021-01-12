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

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
