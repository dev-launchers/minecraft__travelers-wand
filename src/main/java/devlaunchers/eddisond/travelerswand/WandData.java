package devlaunchers.eddisond.travelerswand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

public class WandData extends SerializableData {

    private static final long serialVersionUID = 7883785201904407264L;

    public final UUID playerUUID;
    public UUID linkedEntityUUID;

    Location playerRespawnLocation;

    // Used for saving
    public WandData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        new WandData(playerUUID).saveFile(getDatFilePath(CommandUtils.getPlayerByUUID(playerUUID)));
    }

    public WandData(UUID playerUUID, UUID linkedUUID) {
        this.playerUUID = playerUUID;
        this.linkedEntityUUID = linkedUUID;
    }

    public WandData(UUID playerUUID, Location playerRespawnLocation) {
        this.playerUUID = playerUUID;
        this.playerRespawnLocation = playerRespawnLocation;
    }

    // Used for loading
    public WandData(WandData loadedSerializableData) {
        this.playerUUID = loadedSerializableData.playerUUID;
        this.linkedEntityUUID  = loadedSerializableData.linkedEntityUUID;
        this.playerRespawnLocation = loadedSerializableData.playerRespawnLocation;
    }

    public static void updateRespawnLocation(Player player, Location playerRespawnLocation) {
        new WandData(player.getUniqueId(), playerRespawnLocation).saveFile(getDatFilePath(player));
        Bukkit.getServer().getLogger().log(Level.INFO, "Data Saved - updateRespawnLocation");
    }

    public static void updateLinkedEntity(Player player, UUID linkedUUID) {
        new WandData(player.getUniqueId(), linkedUUID).saveFile(getDatFilePath(player));
        Bukkit.getServer().getLogger().log(Level.INFO, "Data Saved - updateLinkedPlayer");
    }

    public static String getDatFilePath(Player player) {
        return TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + File.separator + player.getUniqueId() + ".dat";
    }

}
