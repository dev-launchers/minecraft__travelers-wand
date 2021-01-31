package devlaunchers.eddisond.travelerswand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;

public class LocationUtils {

    enum LocationType {
        OUTSIDE_BUILDING,
        INSIDE_BUILDING,
        CAVE
    }

    static LocationType guessLocation(Location location, int dimensionsToCheck) {
        LocationType locationType = null;

        Block guessFrom = location.getBlock().getRelative(0, dimensionsToCheck-1, 0);
        List<Block> cubeToCheck = BlockUtils.getBlocksInCube(guessFrom, dimensionsToCheck);
        cubeToCheck.removeIf(block -> block.getType().isAir());

        // EXACT MATCH, STARTS WITH | ENDS WITH, CONTAINS
        double[] weights = {1.0, 0.5, 0.5, 0.25};

        double facilityPercent = BlockUtils.getPercentTypeInCollection(MaterialCategory.facility, cubeToCheck, weights);
        double buildingPercent = BlockUtils.getPercentTypeInCollection(MaterialCategory.building, cubeToCheck, weights);
        double naturePercent = BlockUtils.getPercentTypeInCollection(MaterialCategory.nature, cubeToCheck, weights);
        double waterPercent = BlockUtils.getPercentTypeInCollection(MaterialCategory.water, cubeToCheck, weights);
        int averageSkyLightLevel = getAverageSkyLightLevelInArea(guessFrom, dimensionsToCheck/2, dimensionsToCheck/2, true);
        int averageBlockLightLevel = getAverageBlockLightLevelInArea(guessFrom, dimensionsToCheck/2, dimensionsToCheck/2, false);

        if(guessFrom.getY() <= 60 && naturePercent >= 0.65 && averageSkyLightLevel <= 1) locationType = LocationType.CAVE;

        return locationType;
    }

    static int getAverageBlockLightLevelInArea(Block center, int widthX, int widthZ, boolean ignoreSolid) {
        List<Block> blocksBelowCenter = BlockUtils.getBlocksInPlane(center.getRelative(BlockFace.DOWN), widthX, widthZ);
        int cumulativeLightLevel = 0;
        int blocksToAverage = blocksBelowCenter.size();

        for(Block block : blocksBelowCenter) {
            if((ignoreSolid && block.getType().isSolid()) && (block.getType() == Material.GLASS || block.getType() == Material.LAVA)) { // Consider Glass solid
                if(blocksToAverage > 1) blocksToAverage--;
            }
            else if(block.getType() == Material.GLASS) {
                cumulativeLightLevel += 14;
            } else {
                cumulativeLightLevel += block.getLightFromBlocks();
            }
        }

        return cumulativeLightLevel / blocksToAverage;
    }

    static int getAverageSkyLightLevelInArea(Block center, int widthX, int widthZ, boolean ignoreSolid) {
        List<Block> blocksBelowCenter = BlockUtils.getBlocksInPlane(center.getRelative(BlockFace.DOWN), widthX, widthZ);
        int cumulativeLightLevel = 0;
        int blocksToAverage = blocksBelowCenter.size();

        for(Block block : blocksBelowCenter) {
            if((ignoreSolid && block.getType().isSolid()) && block.getType() == Material.GLASS) { // Consider Glass solid
                if(blocksToAverage > 1) blocksToAverage--;
            }
            else if(block.getType() == Material.GLASS) {
                cumulativeLightLevel += 14;
            } else {
                cumulativeLightLevel += block.getLightFromSky();
            }
        }

        return cumulativeLightLevel / blocksToAverage;
    }

    static boolean isLocationShaded(Block block, int depthCheckX, int depthCheckZ) {
        return getAverageSkyLightLevelInArea(block, depthCheckX, depthCheckZ, true) <= 13;
    }

}
