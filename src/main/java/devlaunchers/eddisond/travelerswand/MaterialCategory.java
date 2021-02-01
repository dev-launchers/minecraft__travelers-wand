package devlaunchers.eddisond.travelerswand;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import com.google.common.collect.ImmutableMap.Builder;

public class MaterialCategory {

    //static Set<Material> building;
    static HashMap<Material, Double> buildingWeighted;

    //static Set<Material> nature;
    static HashMap<Material, Double> natureWeighted;

    //static Set<Material> water;
    static HashMap<Material, Double> waterWeighted;

    //static Set<Material> facility;
    static HashMap<Material, Double> facilityWeighted;

    //static Set<Material> cave;
    static HashMap<Material, Double> caveWeighted;

    private static final ImmutableMap<String, Double> FACILITY =
            new ImmutableMap.Builder<String, Double>()
            .put("FURNACE", 1.0)
            .put("CRAFTING_TABLE", 1.0)
            .put("CHEST", 1.0)
            .put("BED", 1.0)
            .put("ENCHANTING_TABLE", 1.0)
            .put("SMOKER", 1.0)
            .build();

    private static final ImmutableMap<String, Double> BUILDING =
            new ImmutableMap.Builder<String, Double>()
            .put("STONE", 0.5)
            .put("LOG", 0.5)
            .put("BUTTON", 1.0)
            .put("DOOR", 1.0)
            .put("FENCE", 1.0)
            .put("FENCE_GATE", 1.0)
            .put("PLANKS", 1.5)
            .put("PRESSURE_PLATE", 1.0)
            .put("SIGN", 1.0)
            .put("SLAB", 1.5)
            .put("STAIRS", 1.5)
            .put("TRAPDOOR", 1.0)
            .put("WALL_SIGN", 1.0)
            .put("SMOKER", 1.5)
            .put("LANTERN", 1.5)
            .put("BARREL", 1.5)
            .put("BRICK", 1.5)
            .put("GLASS", 1.0)
            .put("COBBLESTONE", 0.5)
            .put("POLISHED", 1.5)
            .put("_BLOCK", 0.5)
            .build();

    private static final ImmutableMap<String, Double> NATURE =
            new ImmutableMap.Builder<String, Double>()
            .put("LAVA", 1.0)
            .put("MOSSY", 1.0)
            .put("STONE", 1.0)
            .put("ICE", 1.0)
            .put("SNOW", 1.0)
            .put("LOG", 1.0)
            .put("CLAY", 1.5)
            .put("LEAVES", 1.5)
            .put("SAPLING", 1.5)
            .put("DIRT", 1.0)
            .put("GRASS", 2.0)
            .put("GRAVEL", 1.0)
            .put("SAND", 1.0)
            .put("DIORITE", 0.5)
            .put("GRANITE", 0.5)
            .put("ANDESITE", 0.5)
            .put("POPPY", 1.0)
            .put("ORCHID", 1.0)
            .put("ALLIUM", 1.0)
            .put("AZURE_BLUET", 1.0)
            .put("TULIP", 1.0)
            .put("DAISY", 1.0)
            .put("CORNFLOWER", 1.0)
            .put("LILY", 1.0)
            .put("MUSHROOM", 1.5)
            .put("VINES", 1.0)
            .build();

    private static final ImmutableMap<String, Double> CAVE =
            new ImmutableMap.Builder<String, Double>()
            .put("STONE", 1.0)
            .put("IRON_ORE", 1.0)
            .put("GOLD_ORE", 1.0)
            .put("COAL_ORE", 1.0)
            .put("DIAMOND_ORE", 1.0)
            .put("REDSTONE_ORE", 1.0)
            .build();

    private static final ImmutableMap<String, Double> WATER =
            new ImmutableMap.Builder<String, Double>()
            .put("WATER", 1.0)
            .put("KELP", 1.0)
            .put("SEAGRASS", 1.0)
            .put("LILY_PAD", 1.0)
            .put("CLAY", 1.0)
            .build();

    private static Set<Material> getNormalCategory(String[] category) {
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

    private static HashMap<Material, Double> getWeightedCategory(ImmutableMap<String, Double> category) {
        HashMap<Material, Double> result = new HashMap<>();

        for(Material materialType : Material.values()) {
            for(String name : category.keySet()) {
                if(materialType.name().contains(name)) {
                    result.put(materialType, category.get(name));
                }
            }
        }

        return result;
    }

    static {
        //building = getNormalCategory(BUILDING.keySet().toArray(new String[0]));
        buildingWeighted = getWeightedCategory(BUILDING);
        //nature = getNormalCategory(NATURE.keySet().toArray(new String[0]));
        natureWeighted = getWeightedCategory(NATURE);
        //water = getNormalCategory(WATER.keySet().toArray(new String[0]));
        waterWeighted = getWeightedCategory(WATER);
        //cave = getNormalCategory(CAVE.keySet().toArray(new String[0]));
        caveWeighted = getWeightedCategory(CAVE);
        //facility = getNormalCategory(FACILITY.keySet().toArray(new String[0]));
        facilityWeighted = getWeightedCategory(FACILITY);

    }
}
