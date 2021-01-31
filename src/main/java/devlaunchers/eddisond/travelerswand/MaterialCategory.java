package devlaunchers.eddisond.travelerswand;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class MaterialCategory {

    private static final String[] FACILITY = {
            "FURNACE",
            "CRAFTING_TABLE",
            "CHEST",
            "BED",
            "ENCHANTING_TABLE",
            "SMOKER"
    };

    private static final String[] BUILDING = {
            "LOG",
            "BUTTON",
            "DOOR",
            "FENCE",
            "FENCE_GATE",
            "PLANKS",
            "PRESSURE_PLATE",
            "SIGN",
            "SLAB",
            "STAIRS",
            "TRAPDOOR",
            "WALL_SIGN",
            "SMOKER",
            "LANTERN",
            "BARREL",
            "BRICK",
            "GLASS",
            "COBBLESTONE",
            "POLISHED",
            "_BLOCK"
    };

    private static final String[] NATURE = {
            "LAVA",
            "MOSSY",
            "STONE",
            "ICE",
            "SNOW",
            "LOG",
            "CLAY",
            "LEAVES",
            "SAPLING",
            "DIRT",
            "GRASS",
            "GRAVEL",
            "SAND",
            "DIORITE",
            "GRANITE",
            "ANDESITE",
            "POPPY",
            "ORCHID",
            "ALLIUM",
            "AZURE_BLUET",
            "TULIP",
            "DAISY",
            "CORNFLOWER",
            "LILY",
            "MUSHROOM",
            "VINES"
    };

    private static final String[] CAVE = {
            "STONE",
            "IRON_ORE",
            "GOLD_ORE",
            "COAL_ORE",
            "DIAMOND_ORE",
            "REDSTONE_ORE"
    };

    private static final String[] WATER = {
            "WATER",
            "KELP",
            "SEAGRASS",
            "LILY_PAD",
            "CLAY"
    };

    private static Set<Material> getCategory(String[] category) {
        Set<Material> result = new HashSet<>();

        for(Material materialType : Material.values()) {
            for(String name : category) {
                if(materialType.name().contains(name)) {
                    result.add(materialType);
                }
            }
        }

        return result;
    }

    static Set<Material> building = getCategory(BUILDING);
    static Set<Material> nature = getCategory(NATURE);
    static Set<Material> water = getCategory(WATER);
    static Set<Material> facility = getCategory(FACILITY);
    static Set<Material> cave = getCategory(CAVE);
}
