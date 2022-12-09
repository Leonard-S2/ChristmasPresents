package org.com.github.leo_s.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

import static org.com.github.leo_s.Christmas.config;

@SuppressWarnings("ConstantConditions")
public class BlockBreak implements Listener{

    @EventHandler
    public void onBlockPlaceChanged(BlockBreakEvent e){
        ItemMeta itemMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
        if(itemMeta == null) return;
        if(itemMeta.hasDisplayName()){
            if(itemMeta.getDisplayName().equals(config.getString("present-item.name").replace(
                    "&", "§"))){
                e.setCancelled(true);
            }
        }
    }
}
