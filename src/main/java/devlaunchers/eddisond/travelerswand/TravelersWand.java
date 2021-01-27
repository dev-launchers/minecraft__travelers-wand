package devlaunchers.eddisond.travelerswand;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
