package me.mattdokn.quickfeller;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class FellerHelper {
    private static TreeType getTreeType(String materialName) {
        switch (materialName) {
            case "ACACIA_LOG":
                return TreeType.ACACIA;
            case "BIRCH_LOG":
                return TreeType.BIRCH;
            case "DARK_OAK_LOG":
                return TreeType.DARK_OAK;
            case "JUNGLE_LOG":
                return TreeType.JUNGLE;
            case "OAK_LOG":
                return TreeType.OAK;
            case "SPRUCE_LOG":
                return TreeType.SPRUCE;
            default:
                return null;
        }
    }
    private static void getNeighbors(Block block, HashMap<Block, Boolean> blocks, HashMap<Block, Boolean> toAdd) {
        // Loop through all blocks in a 3x3x3 area around given block
        for (int z = -1; z <= 1; ++z) {
            for (int y = -1; y <= 1; ++y) {
                for (int x = -1; x <= 1; ++x) {
                    // Get the block relative to given block
                    Block rel = block.getRelative(x, y, z);

                    // If relative block isn't same log type or a log at all then skip it
                    TreeType relType = getTreeType(rel.getType().name());
                    if (relType == null || relType != getTreeType(block.getType().name())) continue;

                    // If rel block isnt in hashmap add it to new log hashmap
                    // This prevents use from adding duplicate blocks to the hashmap
                    if (!blocks.containsKey(rel)) toAdd.put(rel, false);
                }
            }
        }

        // Set the given block as checked
        toAdd.put(block, true);
    }

    public static HashMap<Block, Boolean> getConnected(Block block) {
        // Create hashmap to contain logs w/ check/unchecked value attached
        HashMap<Block, Boolean> blocks = new HashMap<>();

        // Put the starting log into the hashmap with unchecked marker
        blocks.put(block, false);

        // While the hashmap contains unchecked logs
        while (blocks.containsValue(false)) {
            // Create hashmap to store new logs
            HashMap<Block, Boolean> toAdd = new HashMap<>();

            // Loop through all unchecked blocks and check their surroundings for new logs
            for (HashMap.Entry<Block, Boolean> entry : blocks.entrySet()) {
                // If the log is unchecked then run getNeighbors() on it
                if (!entry.getValue()) getNeighbors(entry.getKey(), blocks, toAdd);
            }

            // Add all new logs to the main hashmap
            blocks.putAll(toAdd);
        }
        return blocks;
    }
    public static void destroyBlocks(HashMap<Block, Boolean> blocks, Player player) {
        for (HashMap.Entry<Block, Boolean> entry : blocks.entrySet()) {
            for (ItemStack itemStack : entry.getKey().getDrops(player.getInventory().getItemInMainHand(), player)) {
                HashMap <Integer,ItemStack> leftovers = player.getInventory().addItem(itemStack);
                if (leftovers.size() > 0) {
                    for (HashMap.Entry<Integer, ItemStack> itemStackEntry : leftovers.entrySet()) {
                        player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), itemStackEntry.getValue());
                    }
                }
            }
            entry.getKey().setType(Material.AIR);
        }
    }
}





















