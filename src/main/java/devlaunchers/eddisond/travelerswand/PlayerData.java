package devlaunchers.eddisond.travelerswand;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.UUID;

public class PlayerData {
    File file;
    FileConfiguration fileConfig;

    void createFile(UUID uuid) {
        file = new File(TravelersWand.getPlugin().getDataFolder(), uuid + ".yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);

        try {
            fileConfig.save(file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
