package devlaunchers.eddisond.travelerswand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SerializableData implements Serializable {

    public boolean saveFile(String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            GZIPOutputStream gzOut = new GZIPOutputStream(fileOut);
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(gzOut);

            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static SerializableData loadFile(String filePath) {
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            GZIPInputStream gzIn = new GZIPInputStream(fileIn);
            BukkitObjectInputStream in = new BukkitObjectInputStream(gzIn);

            SerializableData serializableData = (SerializableData) in.readObject();
            in.close();
            return serializableData;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
