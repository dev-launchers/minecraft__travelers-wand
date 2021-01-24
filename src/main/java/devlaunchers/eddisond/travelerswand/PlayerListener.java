package devlaunchers.eddisond.travelerswand;

import org.bukkit.Bukkit;
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
import java.util.*;


public class PlayerListener implements Listener
{
    final int maxDirectTravelStep = 64; // max distance player has to be from linked location to travel immediately
    final int maxIndividualTravelStep = 32; // max distance player can tp with each use, when outside of direct travel distance

    WandData wandData;

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();
        Player player = CommandUtils.getPlayerByUUID(playerUUID);

        // Create potential .dat file for user
        File file = new File(WandData.getDatFilePath(player));

        // If it does not exist already, actually create it
        if(!file.exists()){
            new WandData(playerUUID).saveFile(WandData.getDatFilePath(player));
        }

        // Initial load of WandData
        wandData = new WandData((WandData) Objects.requireNonNull(WandData.loadFile(WandData.getDatFilePath(player))));
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        WandData.updateRespawnLocation(player, player.getBedSpawnLocation());
        wandData = new WandData((WandData) Objects.requireNonNull(WandData.loadFile(WandData.getDatFilePath(player))));
    }

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();

        if(event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        // Update respawnLocation AND reload wandData file
        WandData.updateRespawnLocation(player, player.getBedSpawnLocation());
        wandData = new WandData((WandData) Objects.requireNonNull(WandData.loadFile(WandData.getDatFilePath(player))));

        Bukkit.getServer().broadcastMessage("Player spawn location set to: " + wandData.playerRespawnLocation);
        player.sendMessage("Player spawn location set to: " + wandData.playerRespawnLocation);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUseWand(PlayerInteractEvent event) {
        event.setCancelled(false);

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Action action = event.getAction();
        ItemStack mainHand = inventory.getItemInMainHand();

        wandData = new WandData((WandData) Objects.requireNonNull(WandData.loadFile(WandData.getDatFilePath(player))));

        // Teleport (as close as possible) to linked entity
        if(action == Action.RIGHT_CLICK_AIR && mainHand.getType() == Material.EMERALD) {
            if(wandData.linkedEntityUUID == null) {
                player.sendMessage(TravelersWand.getPlugin().getName() + ": You have to link to another player first, before being able to use this item.");
                return;
            }

            Location playerLocation = player.getLocation();
            Entity linkedEntity = null;

            for(Entity otherEntity : player.getWorld().getLivingEntities()) {
                if(otherEntity.getUniqueId().equals(wandData.linkedEntityUUID)) {
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
                CommandUtils.teleportPlayer(player, linkedEntityLocation);
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

                CommandUtils.teleportPlayer(player, possibleLocation);


                // Block safeBlock = player.getWorld().getHighestBlockAt(possibleLocation);
                // player.sendMessage("safeBlock at: " + safeBlock.getLocation());

                //player.sendMessage("distanceVec:" + distanceVec);
            }

            player.sendMessage(TravelersWand.getPlugin().getName() + ": you travelled " + blocksTravelled + " blocks.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onConnectWand(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();

        if(event.getRightClicked() instanceof org.bukkit.entity.Cow && mainHand.getType() == Material.EMERALD) { // switch between COW and Player for offline/online testing
            Entity entity = event.getRightClicked();

            if(entity.getUniqueId().equals(wandData.linkedEntityUUID)) return; // Ignore if already linked (for now, might change later)

            WandData.updateLinkedEntity(player, entity.getUniqueId());
            wandData = new WandData((WandData) Objects.requireNonNull(WandData.loadFile(WandData.getDatFilePath(player))));
            player.sendMessage("Linked " + player.getName() + " with " + entity.getName() + ". UUID: " + entity.getUniqueId());
        }
    }

    /**
     * Checks if a location is safe (solid ground with 2 breathable blocks)
     *
     * @param location Location to check
     * @return True if location is safe
     */
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

    /*Say you want the surface blocks within radius r.
    You get the center's x coordinate xc, iterate from x = xc-r to xc+r (inclusive).
    You get the center's z coordinate zc, iterate from z = zc-r to zc+r (inclusive).
    Within the two nested loops, you make sure the block is within the radius ((xc - x) * (xc - x) + (zc - z) * (zc - z) <= r * r).
    If it is, then World#getHighestBlockAt at the x and z and do with it what you please.*/

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

        for(Block block : possibleTravelBlocks) {
            int tempDist = (int)targetLocation.toVector().distance(block.getLocation().toVector());

            if(tempDist < smallestDist) {
                smallestDist = tempDist;
                finalBlock = block;
            }
        }

        return finalBlock;
    }

}