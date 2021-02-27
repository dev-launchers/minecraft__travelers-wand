package devlaunchers.eddisond.travelerswand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

public class BlockUtils {

    static Block getFirstSolidBlockAbove(Block lookFrom) {
        BlockFace direction = BlockFace.UP;

        System.out.println("lookFrom Y: " + lookFrom.getY());

        for(int i = 1; i <= 256 - lookFrom.getY(); i++) {
            Block b = lookFrom.getRelative(direction.getModX(), direction.getModY()*i, direction.getModZ());
            if(b.isSolid()) {
                System.out.println("First solid block found at: " + b.getY());
                return b;
            }
        }

        return null;
    }

    static Block getFirstSolidBlockInDirection(Block lookFrom, BlockFace direction, int depth) {
        for(int i = 1; i <= depth; i++) {
            Block b = lookFrom.getRelative(direction.getModX()*i, direction.getModY()*i, direction.getModZ()*i);
            if(b.isSolid()) return b;
        }
        return null;
    }

    static List<List<Block>> getPillarsInCardinalDirections(Block clickedBlock, int height, int depth) {
        List<List<Block>> pillars = new ArrayList<>();
        List<BlockFace> directions = Arrays.asList(
                BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST,
                BlockFace.SOUTH, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST,
                BlockFace.EAST,
                BlockFace.WEST
        );

        for(BlockFace direction : directions) {
            List<Block> pillar = getFirstPillarInDirection(clickedBlock,direction, height, depth);
            if(pillar != null && pillar.size() == height) {
                pillars.add(pillar);
            }
        }

        return pillars;
    }

    static List<Block> getFirstPillarInDirection(Block lookFrom, BlockFace direction, int height, int depth) {
        // If first block in direction isn't solid, don't even loop.
        Block b = getFirstSolidBlockInDirection(lookFrom.getRelative(BlockFace.UP), direction, depth);
        if(b == null) return null;

        List<Block> pillar = new ArrayList<>();
        pillar.add(b);

        for(int i = 1; i < height; i++) {
            Block relative = b.getRelative(0, i, 0);

            if(!pillar.contains(relative)) { // Block is not in pillar yet, consider adding...
                if(relative.isSolid()) {
                    pillar.add(relative); // Is solid - add to pillar.
                } else {
                    break; // Is not solid - reached top of pillar.
                }
            }
        }

        return pillar;
    }

    static List<Block> getBlocksInPlane(Block center, int widthX, int widthZ) {
        List<Block> blocks = new ArrayList<>();
        for(double x = center.getLocation().getX() - widthX; x <= center.getLocation().getX() + widthX; x++) {
            for(double z = center.getLocation().getZ() - widthZ; z <= center.getLocation().getZ() + widthZ; z++) {
                Location loc = new Location(center.getWorld(), x, center.getY(), z);
                blocks.add(loc.getBlock());
            }
        }
        return blocks;
    }

    static List<Block> getCardinalNeighborBlocks(Block start, int reach, int height) {
        List<Block> blocks = new ArrayList<>();

        for(int i = 1; i <= reach; i++) {
            for(int j = 1; j <= height; j++) {
                blocks.add(start.getRelative(i, j, 0));
                blocks.add(start.getRelative(-i, j, 0));
                blocks.add(start.getRelative(0, j, i));
                blocks.add(start.getRelative(0, j, -i));
            }
        }

        return blocks;
    }

    static double getPercentTypeInCollection(HashMap<Material, Double> weightedType, List<Block> collection) {
        double countSame = 0.0;

        for(Block b : collection) {
            for(Material m : weightedType.keySet()) {
                if (b.getType().name().contains(m.name())) {

                    countSame += (weightedType.get(m) / weightedType.size());
                }
            }
        }

        return Math.max(countSame/* / weightedType.size()*/, 0.0);
    }

    static List<Block> reduceBlockCollection(List<Block> collection, String[] reduceFilter) {

        List<Block> reducedCollection = new ArrayList<>();

        for(Block b : collection) {
            for(String f : reduceFilter) {
                if(b.getType().name().endsWith(f)) reducedCollection.add(b);
            }
        }

        return reducedCollection;
    }

    static List<Block> getBlocksInCuboid(Block start, int widthX, int widthZ, int height) {
        List<Block> blocks = new ArrayList<Block>();
        for(double x = start.getLocation().getX() - widthX; x <= start.getLocation().getX() + widthX; x++){
            for(double y = start.getLocation().getY() - height; y <= start.getLocation().getY() + height; y++){
                for(double z = start.getLocation().getZ() - widthZ; z <= start.getLocation().getZ() + widthZ; z++){
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }

    static List<Block> getBlocksInRadius(Location location, int radius) {
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

    static Block getTravelBlock(List<Block> possibleTravelBlocks, Location targetLocation) {
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
