package org.com.github.leo_s.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

import static org.com.github.leo_s.Christmas.config;

@SuppressWarnings("ConstantConditions")
public class BlockPlace implements Listener{

    @EventHandler
    public void onBlockPlaceChanged(BlockPlaceEvent e){
        ItemMeta itemMeta = e.getItemInHand().getItemMeta();
        if(itemMeta == null) return;
        if(itemMeta.hasDisplayName()){
            if(itemMeta.getDisplayName().equals(config.getString("present-item.name").replace(
                    "&", "§"))){
                e.setCancelled(true);
            }
        }
    }
}
