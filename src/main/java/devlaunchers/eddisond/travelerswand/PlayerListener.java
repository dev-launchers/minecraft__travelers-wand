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
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Objects;

public class PlayerListener implements Listener
{
    Location respawnLocation;
    Location distanceToRespawnLocation;

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();

        if(event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Bukkit.getScheduler().runTaskLater(
                TravelersWand.getPlugin(), () -> Bukkit.getServer().broadcastMessage(player.getName() + " set bed spawn location."),
                100/20
        );

        Location bedSpawnLocation = player.getBedSpawnLocation();
        Bukkit.getServer().broadcastMessage("Player spawn location set to: " + bedSpawnLocation);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUseWand(PlayerInteractEvent event) {
        event.setCancelled(false);

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Action action = event.getAction();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();

        if(respawnLocation != null && action == Action.RIGHT_CLICK_AIR && mainHand.getType() == Material.EMERALD) {
            // travel with the wand
            // Wand.travel()

            Location pLoc = player.getLocation();
            Location rLoc = respawnLocation;
            Vector pVec = new Vector(pLoc.getX(), pLoc.getY(), pLoc.getZ());
            Vector rVec = new Vector(rLoc.getX(), rLoc.getY(), rLoc.getZ());
            double dVec = rVec.distance(pVec);

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tp " + player.getName() + " " + respawnLocation.getBlockX() + " " + respawnLocation.getBlockY() + " " + respawnLocation.getBlockZ());
            Bukkit.getServer().broadcastMessage(player.getName() + " travelled " + dVec + " blocks.");

        } else if(action == Action.RIGHT_CLICK_BLOCK && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
            // recharge the wand
            // Wand.recharge()
        }
    }

    /*
    Or you could use https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Bukkit.html#dispatchCommand(org.bukkit.command.CommandSender, java.lang.String)
    with Bukkit.getConsoleCommandSender() as CommandSender. Then dispatch the command "/spawnPoint playerName locX locY locZ" and that should work.*/

    @EventHandler(priority = EventPriority.HIGH)
    public void onConnectWand(PlayerInteractEntityEvent event) {
        // EVENT THAT HANDLES CONNECTING TWO PLAYERS WANDS / THEIR SPAWNS WITH EACH OTHER
        Player player = event.getPlayer();

        if(event.getRightClicked() instanceof org.bukkit.entity.Cow) {
            Entity entity = event.getRightClicked();

            World world = entity.getWorld();
            Location loc = entity.getLocation();
            respawnLocation = new Location(world, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()); // for future calculations?

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawnpoint " + player.getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
        }
    }

}