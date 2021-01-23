package devlaunchers.eddisond.travelerswand;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class WandData extends SerializableData {

    private static final long serialVersionUID = 7883785201904407264L;

    private static final String pluginDataFolderAbsolute = TravelersWand.getPlugin().getDataFolder().getAbsolutePath();

    public final UUID playerUUID;
    public UUID linkedEntityUUID;

    Location playerRespawnLocation;

    // Used for saving
    public WandData(UUID playerUUID) {
        this.playerUUID = playerUUID;
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

    public static void updateRespawnLocation(UUID playerUUID, Location playerRespawnLocation) {
        new WandData(playerUUID, playerRespawnLocation).save(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + playerUUID + ".dat");
        Bukkit.getServer().getLogger().log(Level.INFO, "Data Saved - updateRespawnLocation");
    }

    public static void updateLinkedEntity(UUID playerUUID, UUID linkedUUID) {
        new WandData(playerUUID, linkedUUID).save(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + playerUUID + ".dat");
        Bukkit.getServer().getLogger().log(Level.INFO, "Data Saved - updateLinkedPlayer");
    }

}
