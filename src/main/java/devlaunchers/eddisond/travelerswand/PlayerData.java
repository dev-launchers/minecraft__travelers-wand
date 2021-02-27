package devlaunchers.eddisond.travelerswand;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerData {

	private static HashMap<UUID, UUID> linkedUUIDs = new HashMap<UUID, UUID>();

	public static UUID getLinkedUUIDForPlayer(UUID player) {
		if (!linkedUUIDs.containsKey(player)) {
			YamlConfiguration conf = loadYamlConfiguration(player);
			if (conf.contains("linkedUUID")) {
				linkedUUIDs.put(player, UUID.fromString(conf.getString("linkedUUID")));
			} else {
				linkedUUIDs.put(player, null);
			}
		}
		return linkedUUIDs.get(player);
	}
	
	public static void setLinkedUUIDForPlayer(UUID player, UUID link) {
		linkedUUIDs.put(player, link);
	}

	public static void savePlayerData(UUID player) {
		if (linkedUUIDs.containsKey(player) && linkedUUIDs.get(player) != null) {
			YamlConfiguration conf = new YamlConfiguration();
			conf.set("linkedUUID", linkedUUIDs.get(player).toString());
			saveYamlConfiguration(player, conf);
		} else {
			File file = new File(TravelersWand.getPlugin().getDataFolder(), player + ".yml");
			if (file.exists()) {
				file.delete();
			}
		}
	}

	private static YamlConfiguration loadYamlConfiguration(UUID uuid) {
		File file = new File(TravelersWand.getPlugin().getDataFolder(), uuid + ".yml");
		if (!file.exists()) {
			return new YamlConfiguration();
		}

		return YamlConfiguration.loadConfiguration(file);
	}

	private static void saveYamlConfiguration(UUID uuid, YamlConfiguration configToSave) {
		File file = new File(TravelersWand.getPlugin().getDataFolder(), uuid + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			configToSave.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
