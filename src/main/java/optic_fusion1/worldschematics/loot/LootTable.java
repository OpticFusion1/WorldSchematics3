package optic_fusion1.worldschematics.loot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import optic_fusion1.worldschematics.util.MapUtils;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import worldschematics.WorldSchematics;
import worldschematics.util.DebugLogger;

public class LootTable {

    private Chest chest;
    private File dataFolder = WorldSchematics.instance().getDataFolder();
    private String lootTable;
    private File lootFile;
    private FileConfiguration lootConfig;
    private int maxItems;
    private int minItems;
    private boolean overwriteContents;

    public LootTable(Chest chest, String lootTable, boolean overwriteContents) {
        DebugLogger.log("=== Created Loot Table for Chest===", DebugLogger.DebugType.LOOTTABLE);
        this.chest = chest;
        this.lootTable = lootTable;
        this.overwriteContents = overwriteContents;

        // Load the loot table config file
        lootFile = new File(dataFolder, "/loottables/" + lootTable + ".yml");
        lootConfig = YamlConfiguration.loadConfiguration(lootFile);

        DebugLogger.log("Path of loot file is " + lootFile.getAbsolutePath(), DebugLogger.DebugType.LOOTTABLE);

        minItems = lootConfig.getInt("options.minItems", 1);
        maxItems = lootConfig.getInt("options.maxItems", 1);

        // Max sure maxItems is not over 27
        if (maxItems > 27) {
            maxItems = 27;
            DebugLogger.log("maxItems for loot table " + lootTable + " is above 27, the maximum amount of items allowes in a chest!", DebugLogger.DebugType.MISC);
        }

    }

    // Fill a chest with loot from a loot table
    public void fillChest() {
        Map<LootItem, Double> items = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        ConfigurationSection section = lootConfig.getConfigurationSection("loot");
        DebugLogger.log("Generating loot", DebugLogger.DebugType.LOOTTABLE);

        if (section == null) {
            DebugLogger.log("loot file is empty or does not exist!", DebugLogger.DebugType.LOOTTABLE);
            return;
        }

        // Go through each item and add it and its chance to the map
        DebugLogger.log("Items in Config:", DebugLogger.DebugType.LOOTTABLE);

        for (String configItemString : section.getKeys(false)) {
            DebugLogger.log("Config Item: " + configItemString);
            LootItem item = new LootItem(configItemString, section);
            item.randomize();
            DebugLogger.log("-- " + item.itemStack().getType().toString());
            items.put(item, item.chance());
        }

        // Now we place the items in the chest. Iterate through slots in chance minAmount to maxAmount of times
        Inventory chestInventory = chest.getBlockInventory();

        // Empty inventory if option is set
        if (overwriteContents == true) {
            chestInventory.clear();
        }

        if (chest.getBlock().getType() == Material.AIR) {
            DebugLogger.log("Chest is actually an air block. This normally shouldn't happen!", DebugLogger.DebugType.LOOTTABLE);
            return;
        }

        DebugLogger.log("Chest empty, placing loot", DebugLogger.DebugType.LOOTTABLE);
        int amount = ThreadLocalRandom.current().nextInt(minItems, maxItems + 1);
        DebugLogger.log("Amount of item stacks being placed: " + amount, DebugLogger.DebugType.LOOTTABLE);
        // Lets place the items randomly in the chest
        for (int i = 0; i < amount; i++) {
            // Chests have 27 slots
            int slot = random.nextInt(26);

            ItemStack itemStack = chestInventory.getItem(slot);
            LootItem lootItem = MapUtils.getFromWeightedMap(items);
            lootItem.randomize();

            // If a slot returns null then its empty
            if (itemStack != null) {
                i--;
            }

            if (lootItem.itemStack() != null) {
                chestInventory.setItem(slot, lootItem.itemStack());
            }

        }
    }

}
