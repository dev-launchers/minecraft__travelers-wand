package devlaunchers.eddisond.travelerswand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandUtils {

    public static Player getPlayerByUUID(UUID playerUUID) {
        return Bukkit.getServer().getPlayer(UUID.fromString(String.valueOf(playerUUID)));
    }

    public static void setPlayerSpawn(Player player, Location location) {
        Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "spawnpoint "
                        + player.getName()
                        + " "
                        + location.getBlockX()
                        + " "
                        + location.getBlockY()
                        + " "
                        + location.getBlockZ()
        );
    }

    public static void teleportPlayer(Player player, Location location) {
        Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "tp "
                        + player.getName()
                        + " "
                        + location.getBlockX()
                        + " "
                        + location.getBlockY()
                        + " "
                        + location.getBlockZ()
        );
    }

}
