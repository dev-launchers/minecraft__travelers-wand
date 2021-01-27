package devlaunchers.eddisond.travelerswand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerListener implements Listener {

    final int maxDirectTravelStep = 64; // max distance player has to be from linked location to travel immediately
    final int maxIndividualTravelStep = 32; // max distance player can tp with each use, when outside of direct travel distance

    UUID linkedUUID;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if(TravelersWand.getPlayerData() != null) {
            TravelersWand.getPlayerData().createFile(event.getPlayer().getUniqueId());

            System.out.println("playerData and .yml file exist");
            System.out.println("yml file name: " + TravelersWand.getPlayerData().file.getName());

            // uuid is not loaded into memory yet || player data file somehow got removed as the plugin was running
            if(linkedUUID == null) {
                // try to get uuid in string format, from player data
                String uuidString = TravelersWand.getPlayerData().fileConfig.getString("linkedUUID");

                if(uuidString != null) {
                    linkedUUID = UUID.fromString(uuidString);
                }
            } // otherwise, just continue on with the program :)
        }
    }

    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        try {
            // Save "playerUUID".yml file
            TravelersWand.getPlayerData().fileConfig.save(TravelersWand.getPlayerData().file);

            // Attributes, that should not persist in memory after player leaves, are set to null
            if(linkedUUID != null) linkedUUID = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onConnectWand(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();

        if(event.getRightClicked() instanceof Player && mainHand.getType() == Material.EMERALD) { // switch between org.bukkit.entity.Cow and Player for offline / online testing
            Entity entity = event.getRightClicked();

            if(entity.getUniqueId().equals(linkedUUID)) return; // Ignore if already linked (for now, might change later)

            TravelersWand.getPlayerData().fileConfig.set("linkedUUID", entity.getUniqueId().toString());
            linkedUUID = entity.getUniqueId();

            player.sendMessage(player.getUniqueId() + " linked with " + linkedUUID);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUseWand(PlayerInteractEvent event) {
        event.setCancelled(false);

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Action action = event.getAction();
        ItemStack mainHand = inventory.getItemInMainHand();

        // Teleport (as close as possible) to linked entity
        if(action == Action.RIGHT_CLICK_AIR && mainHand.getType() == Material.EMERALD) {
            if(linkedUUID == null || linkedUUID.toString().isEmpty()) {
                player.sendMessage(TravelersWand.getPlugin().getName() + ": You have to link to another player first, before being able to use this item.");
                return;
            }

            Location playerLocation = player.getLocation();
            Entity linkedEntity = null;

            for(Entity otherEntity : player.getWorld().getLivingEntities()) {
                if(otherEntity.getUniqueId().equals(linkedUUID)) {
                    // Teleport the player to linked player/entity
                    linkedEntity = otherEntity;
                }
            }

            if(linkedEntity == null) {
                player.sendMessage(TravelersWand.getPlugin().getName() + ": Could not get location of linked entity.");
                return;
            }

            Location linkedEntityLocation = linkedEntity.getLocation();
            Vector linkedVec = linkedEntityLocation.toVector();
            Vector playerVec = playerLocation.toVector();

            double blocksToTravel = linkedVec.distance(playerVec);
            int blocksTravelled = 0;

            if (blocksToTravel <= maxDirectTravelStep) {
                // Teleport player directly to linked entity
                player.teleport(linkedEntityLocation);

                blocksTravelled = (int)blocksToTravel;
            } else {
                List<Block> possibleTravelBlocks = getRelativeSurfaceBlocks(playerLocation, maxIndividualTravelStep);
                Block travelBlock = getTravelBlock(possibleTravelBlocks, linkedEntityLocation);
                if(travelBlock == null) return;

                Location possibleLocation = new Location(
                        player.getWorld(),
                        travelBlock.getLocation().getX(),
                        player.getWorld().getHighestBlockAt(travelBlock.getLocation()).getY() + 1,
                        travelBlock.getLocation().getZ()
                );

                player.teleport(possibleLocation);
                blocksTravelled = maxIndividualTravelStep;
            }

            player.sendMessage(TravelersWand.getPlugin().getName() + ": you travelled " + blocksTravelled + " blocks.");
        }
    }

    // Doesn't really work... Hmmmm
    public static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        if (!ground.getType().isSolid()) {
            return false; // not solid
        }
        return true;
    }

    private List<Block> getRelativeSurfaceBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();

        int xCenter = (int)location.getX();
        int zCenter = (int)location.getZ();

        for(int x = (xCenter-radius); x <= (xCenter+radius); x++) {
            for(int z = (zCenter-radius); z <= (zCenter+radius); z++) {
                if((xCenter - x) * (xCenter - x) + (zCenter - z) * (zCenter - z) <= radius * radius) { // block is within radius
                    blocks.add(new Location(location.getWorld(), x, location.getY(), z).getBlock());
                }
            }
        }

        return blocks;
    }

    private Block getTravelBlock(List<Block> possibleTravelBlocks, Location targetLocation) {
        //Vector temp = locationTo.toVector().subtract(locationFrom.toVector()).normalize();
        Block finalBlock = null;
        int smallestDist = Integer.MAX_VALUE;

        for (Block block : possibleTravelBlocks) {
            int tempDist = (int) targetLocation.toVector().distance(block.getLocation().toVector());

            if (tempDist < smallestDist) {
                smallestDist = tempDist;
                finalBlock = block;
            }
        }

        return finalBlock;
    }

}