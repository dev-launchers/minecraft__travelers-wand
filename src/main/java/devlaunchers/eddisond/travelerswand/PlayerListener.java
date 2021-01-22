package devlaunchers.eddisond.travelerswand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class PlayerListener implements Listener
{
    double distanceToRespawnLocation;
    final int maxTravelDistance = 256;
    WandData wandData;

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();

        File file = new File(TravelersWand.getPlugin().getDataFolder() + File.separator + playerUUID + ".dat");

        if(!file.exists()){
            // Generate it
            new WandData(playerUUID).save(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + playerUUID + ".dat");
        }

        // Initial load of WandData
        wandData = new WandData((WandData) Objects.requireNonNull(WandData.load(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + playerUUID + ".dat")));
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        WandData.updateRespawnLocation(player.getUniqueId(), player.getBedSpawnLocation());
        wandData = new WandData((WandData) Objects.requireNonNull(WandData.load(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + player.getUniqueId() + ".dat")));
    }

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();

        if(event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        // Update respawnLocation AND reload wandData file
        WandData.updateRespawnLocation(player.getUniqueId(), player.getBedSpawnLocation());
        wandData = new WandData((WandData) Objects.requireNonNull(WandData.load(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + player.getUniqueId() + ".dat")));

        Bukkit.getServer().broadcastMessage("Player spawn location set to: " + wandData.playerRespawnLocation);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUseWand(PlayerInteractEvent event) {
        event.setCancelled(false);

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Action action = event.getAction();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();

        wandData = new WandData((WandData) Objects.requireNonNull(WandData.load(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + player.getUniqueId() + ".dat")));

        if(action == Action.RIGHT_CLICK_AIR && mainHand.getType() == Material.EMERALD) {
            if(wandData.playerRespawnLocation == null) return;
            // travel with the wand
            // Wand.travel()

            Location playerLocation = player.getLocation();
            Location playerRespawnLocation = wandData.playerRespawnLocation;

            Vector playerLocationVector = new Vector(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
            Vector respawnLocationVector = new Vector(playerRespawnLocation.getX(), playerRespawnLocation.getY(), playerRespawnLocation.getZ());
            distanceToRespawnLocation = respawnLocationVector.distance(playerLocationVector);

            if(distanceToRespawnLocation > maxTravelDistance) {
                player.sendMessage("You are too far away.");
            } else {

                // Teleport the player
                Bukkit.getServer().dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "tp " + player.getName() + " "
                                + playerRespawnLocation.getBlockX() + " "
                                + playerRespawnLocation.getBlockY() + " " + playerRespawnLocation.getBlockZ()
                );

                Bukkit.getServer().broadcastMessage(player.getName() + " travelled " + (int)distanceToRespawnLocation + " blocks.");
            }

        } else if(action == Action.RIGHT_CLICK_BLOCK && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
            // recharge the wand
            // Wand.recharge()
            Bukkit.getServer().broadcastMessage(String.valueOf(wandData.playerRespawnLocation));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onConnectWand(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if(event.getRightClicked() instanceof org.bukkit.entity.Cow) {
            Entity entity = event.getRightClicked();

            World world = entity.getWorld();
            Location entityLocation = entity.getLocation();

            WandData.updateRespawnLocation(
                    player.getUniqueId(), new Location(world, entityLocation.getBlockX(), entityLocation.getBlockY() + 1, entityLocation.getBlockZ())
            );

            wandData = new WandData((WandData) Objects.requireNonNull(WandData.load(TravelersWand.getPlugin().getDataFolder().getAbsolutePath() + "/" + player.getUniqueId() + ".dat")));

            Bukkit.getServer().broadcastMessage("wandData respawnlocation: " + wandData.playerRespawnLocation);

            Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(), "spawnpoint " + player.getName() + " " + entityLocation.getBlockX() + " " + entityLocation.getBlockY() + " " + entityLocation.getBlockZ()
            );
        }
    }
}