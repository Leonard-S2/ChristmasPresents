package org.com.github.leo_s.present;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.com.github.leo_s.Christmas.config;
import static org.com.github.leo_s.utils.files.Convert.convert;

@SuppressWarnings({"unused", "FieldMayBeFinal", "FieldCanBeLocal", "ConstantConditions", "DuplicatedCode"})
public class Present {

    public static void givePresentItem(Player p, String target, int amount) {
        Player targetPlayer = Bukkit.getPlayer(target);
        if(targetPlayer != null){
            Material m = Material.valueOf(config.getString("present-item.material"));
            String name = convert(config.getString("present-item.name"));
            List<String> lore = config.getStringList("present-item.lore").stream().map(s -> s.replace("&", "§").replace("%player%", targetPlayer.getName())).toList();
            String texture = config.getString("present-item.texture");

            ItemFlag[] itemFlags = new ItemFlag[]{};
            for(String flag : config.getStringList("present-item.flags")){
                itemFlags = new ItemFlag[]{ItemFlag.valueOf(flag)};
            }

            ItemStack item = new ItemStack(m, amount);

            if(m == Material.PLAYER_HEAD){
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                GameProfile profile = new GameProfile(UUID.fromString("3b01e4c7-f07b-4cff-a744-88d7eae47249"), null);
                profile.getProperties().put("textures", new Property("textures", texture));
                try{
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                skullMeta.setDisplayName(name);
                skullMeta.setLore(lore);
                item.setItemMeta(skullMeta);
            }else{
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
                for(ItemFlag flag : itemFlags){
                    itemMeta.addItemFlags(flag);
                }
                item.setItemMeta(itemMeta);
            }
            if(targetPlayer.getInventory().firstEmpty() != -1) {
                targetPlayer.getInventory().addItem(item);
                targetPlayer.sendMessage("§aYou received a present from " + p.getName());
            }else{
                p.sendMessage("§cThe player " + targetPlayer.getName() + " does not have space in inventory!");
            }
        }
    }
}
