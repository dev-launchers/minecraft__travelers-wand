package devlaunchers.eddisond.travelerswand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerListener implements Listener {

	final int maxDirectTravelStep = 8; // max distance player has to be from linked location to travel immediately
	final int maxIndividualTravelStep = 4; // max distance player can tp with each use, when outside of direct travel
											// distance

	@EventHandler
	public void onPlayerLeaveEvent(PlayerQuitEvent event) {
		PlayerData.savePlayerData(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onConnectWand(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		ItemStack mainHand = inventory.getItemInMainHand();

		if ((event.getRightClicked() instanceof org.bukkit.entity.Cow || event.getRightClicked() instanceof Player)
				&& mainHand.getType() == Material.EMERALD) { // switch between org.bukkit.entity.Cow and Player for
																// offline / online testing
			Entity entity = event.getRightClicked();

			if (entity.getUniqueId().equals(PlayerData.getLinkedUUIDForPlayer(player.getUniqueId())))
				return; // Ignore if already linked (for now, might change later)

			PlayerData.setLinkedUUIDForPlayer(player.getUniqueId(), entity.getUniqueId());

			player.sendMessage(player.getUniqueId() + " linked with " + entity.getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onUseWand(PlayerInteractEvent event) {
		event.setCancelled(false);
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		Action action = event.getAction();
		ItemStack mainHand = inventory.getItemInMainHand();

		/* START: JUST FOR TESTING */
		if (action == Action.RIGHT_CLICK_BLOCK && mainHand.getType() == Material.DIAMOND) {
			Block clickedBlock = event.getClickedBlock();
			assert clickedBlock != null;

			List<Block> blocks = BlockUtils.getBlocksInPlane(clickedBlock, 2, 2);
			for (Block b : blocks) {
				b.setType(Material.LIME_WOOL);
			}
		}

		if (action == Action.RIGHT_CLICK_BLOCK && mainHand.getType() == Material.GOLDEN_CARROT) {
			Block clickedBlock = event.getClickedBlock();
			assert clickedBlock != null;

			player.sendMessage("Average light level in area: "
					+ LocationUtils.getAverageSkyLightLevelInArea(clickedBlock, 2, 2, true));
		}

		if (action == Action.RIGHT_CLICK_BLOCK && mainHand.getType() == Material.COMPASS) {
			Block clickedBlock = event.getClickedBlock();
			assert clickedBlock != null;

			List<Block> blocks = BlockUtils.getBlocksInCuboid(clickedBlock, 6, 2, 2);
			blocks.removeIf(block -> block.getType().isAir());

			List<Material> allMaterials = new ArrayList<>();

			for (Block b : blocks) {
				allMaterials.add(b.getType());
			}

			Map<Material, Integer> materialsCount = new HashMap<Material, Integer>();

			for (Material m : allMaterials) {
				if (!materialsCount.containsKey(m)) {
					materialsCount.put(m, 1);
				} else {
					materialsCount.replace(m, materialsCount.get(m) + 1);
				}
			}

			materialsCount.forEach((k, v) -> {
				System.out.println("PERCENTAGE OF " + k.name() + ", " + v * 100 / allMaterials.size() + "%");
			});

			/*
			 * LocationUtils.LocationType locationGuess =
			 * LocationUtils.guessLocation(clickedBlock.getLocation(), 3);
			 * 
			 * if(locationGuess == LocationUtils.LocationType.CAVE) {
			 * player.sendMessage("YOU ARE PROBABLY IN A CAVE!"); } else {
			 * player.sendMessage("you are not in a cave..."); }
			 */
		}

		/* END: JUST FOR TESTING */

		// Teleport (as close as possible) to linked entity
		if (action == Action.RIGHT_CLICK_AIR && mainHand.getType() == Material.EMERALD) {

			// Is wand being used in any other world than the "NORMAL" / Overworld?
			if (!isInWorldEnvironment(player, World.Environment.NORMAL)) {
				player.sendMessage(TravelersWand.getPlugin().getName() + ": Currently only works in the Overworld.");
				return;
			}

			UUID linkedUUID = PlayerData.getLinkedUUIDForPlayer(player.getUniqueId());
			
			// Is player linked yet?
			if (linkedUUID == null) {
				player.sendMessage(TravelersWand.getPlugin().getName()
						+ ": You have to link to another player first, before being able to use this item.");
				return;
			}

			Entity linkedEntity = getEntityInWorldByUUID(linkedUUID, player.getWorld());

			if (linkedEntity == null) {
				player.sendMessage(TravelersWand.getPlugin().getName() + ": Could not get location of linked entity.");
				return;
			}

			Location linkedLocation = linkedEntity.getLocation();
			Location playerLocation = player.getLocation();

			double distanceToLinked = linkedLocation.distance(playerLocation);

			// Player can teleport directly to linked entity
			if (distanceToLinked <= maxDirectTravelStep) {
				// Linked entity is surrounded by solid blocks. Can not teleport closer.
				if (isEntityBlockedIn(linkedEntity)) {
					player.sendMessage(TravelersWand.getPlugin().getName()
							+ ": Linked entity is surrounded by solid blocks on all sides.");
					return;
				}

				Block linkedBlock = linkedLocation.getBlock();
				// Block blockAboveLinked = BlockUtils.getFirstSolidBlockAbove(linkedBlock);
				Block blockAboveLinked = linkedBlock.getRelative(0, 3, 0); // for a future method: modY = (radius of
																			// cube - 1) (Takes into account feet and
																			// blocks up three!)

				boolean isGlassRoof = false;
				boolean isShadedArea = false;

				// Is there a solid block above the players head? Maybe check instead if there
				// is a certain amount of solid blocks above?
				if (blockAboveLinked != null) {
					// List<Block> planeAboveHead = BlockUtils.getBlocksInPlane(blockAboveLinked, 6,
					// 6);
					// String[] reduceFilter = {"PLANKS", "LOG", "WOOD", "SLAB", "BRICKS",
					// "LEAVES"};
					// planeAboveHead = BlockUtils.reduceBlockCollection(planeAboveHead,
					// reduceFilter);
					// player.sendMessage(TravelersWand.getPlugin().getName() + ": There are " +
					// planeAboveHead.size() + " blocks in a plane, above linked entities head.");

					List<Block> cubeAboveHead = BlockUtils.getBlocksInCuboid(blockAboveLinked, 4, 4, 4);
					cubeAboveHead.removeIf(block -> block.getType().isAir());

					isGlassRoof = blockAboveLinked.getLightFromSky() == 15
							&& blockAboveLinked.getType() == Material.GLASS;
					isShadedArea = LocationUtils.getAverageSkyLightLevelInArea(blockAboveLinked, 4, 4, false) <= 13;

					player.sendMessage(
							TravelersWand.getPlugin().getName() + ": There is a block above the linked entities head.");
					player.sendMessage(TravelersWand.getPlugin().getName() + ": There are " + cubeAboveHead.size()
							+ " blocks in a cube, above linked entities head.");

					if (isGlassRoof) {
						player.sendMessage(TravelersWand.getPlugin().getName()
								+ ": Linked entity has glass above its head, and light level == 15: glass roof");

						if (isShadedArea) {
							player.sendMessage(TravelersWand.getPlugin().getName()
									+ ": Linked entity is in a shaded area under a glass roof. Likely inside a house!");
						}
					}
				}

				// Get block linked entity is standing on
				Block linkedFeet = linkedLocation.getBlock().getRelative(BlockFace.DOWN);
				List<List<Block>> pillarsCardinal = BlockUtils.getPillarsInCardinalDirections(linkedFeet, 3, 10);

				player.sendMessage("Pillars around linked entity: " + pillarsCardinal.size());
			}
		}
	}

	private Entity getEntityInWorldByUUID(UUID uuid, World world) {
		for (Entity entity : world.getLivingEntities()) {
			if (entity.getUniqueId().equals(uuid)) {
				return entity;
			}
		}
		return null;
	}

	private boolean isInWorldEnvironment(Entity entity, World.Environment environment) {
		return entity.getWorld().getEnvironment().equals(environment);
	}

	private boolean isEntityBlockedIn(Entity entity) {
		Block entityFeet = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
		List<Block> blocks = BlockUtils.getCardinalNeighborBlocks(entityFeet, 2, 2);
		int blockedCount = 0;

		for (Block b : blocks) {
			if (b.isSolid())
				blockedCount++;
		}

		return blockedCount == blocks.size();
	}

}