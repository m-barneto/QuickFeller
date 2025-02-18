package me.mattdokn.quickfeller;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EventListener implements Listener {
    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent e) {
        Block block = e.getBlock();
        // If the block isn't a log then exit out of our handler
        if (!FellerHelper.IsTreeBlockType(block)) return;
        Player player = e.getPlayer();
    
        // Check if the player has permission to use the plugin
        if (!player.hasPermission("quickfeller.use")) return;

        // Check if the player is crouching and if that is a requirement
        if (QuickFeller.instance.getConfig().getBoolean("require-crouch")) {
            // If they aren't crouching, return
            if (!player.isSneaking()) return;
        }
        
        // Get item the user has in main hand
        ItemStack playerTool = player.getInventory().getItemInMainHand();
        
        // If the user needs to have the correct tool return if they aren't holding an axe
        if(!player.hasPermission("quickfeller.ignoretool") || !QuickFeller.instance.getConfig().getBoolean("require-tool")) {
            if (!playerTool.getType().name().contains("AXE")) return;
        }
        
        // Algorithm to collect connecting logs of the same type into a hashmap
        HashMap<Block, Boolean> connectedBlocks = FellerHelper.getConnected(block);
        
        // If the user isn't allowed to ignore durability then worry about tool usage/break warning and take durability from their tool
        if (!player.hasPermission("quickfeller.ignoredurability") || !QuickFeller.instance.getConfig().getBoolean("ignore-tool-durability")) {
            // Take durability from their tool
            int unbLevel = playerTool.getEnchantmentLevel(Enchantment.UNBREAKING);
            int dmgAmount = (unbLevel > 0) ? connectedBlocks.size() / unbLevel : connectedBlocks.size();
            ItemMeta itemMeta = playerTool.getItemMeta();
            if (!(itemMeta instanceof Damageable)) return;
            Damageable tool = (Damageable) itemMeta;
            if (tool.getDamage() + dmgAmount < playerTool.getType().getMaxDurability()) {
                tool.setDamage(tool.getDamage() + dmgAmount);
                playerTool.setItemMeta((ItemMeta) tool);
            } else {
                player.sendMessage(ChatColor.RED + "Not enough durability!");
                return;
            }
        }
        FellerHelper.destroyBlocks(connectedBlocks, player);
        e.setCancelled(true);
    }
}
