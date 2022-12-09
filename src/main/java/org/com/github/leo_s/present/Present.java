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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static org.com.github.leo_s.Christmas.config;

@SuppressWarnings({"unused", "FieldMayBeFinal", "FieldCanBeLocal", "ConstantConditions", "DuplicatedCode"})
public class Present {

    public static HashMap<Player, Present> presentHashMap = new HashMap<>();
    private String name;
    private Material material;
    private List<String> lore;
    private ItemFlag[] flags;
    String texture_head;

    public Present(String name, Material material, List<String> lore,
                   ItemFlag[] flags) {
        this.name = name;
        this.material = material;
        this.lore = lore;
        this.flags = flags;
        this.texture_head = config.getString("present-item.texture");

    }

    public void givePresentItem(Player p, String target, int amount) {
        Player targetPlayer = Bukkit.getPlayer(target);
        if(targetPlayer != null){
            ItemStack item = new ItemStack(material, amount);

            if(material == Material.PLAYER_HEAD){
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                GameProfile profile = new GameProfile(UUID.fromString("3b01e4c7-f07b-4cff-a744-88d7eae47249"), null);
                profile.getProperties().put("textures", new Property("textures", texture_head));
                try{
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                skullMeta.setDisplayName(name.replace("&", "§"));
                skullMeta.setLore(lore.stream().map(s -> s.replace("&", "§").replace("%player%", targetPlayer.getName())).toList());
                item.setItemMeta(skullMeta);
            }else{
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(name.replace("&", "§"));
                itemMeta.setLore(lore.stream().map(s -> s.replace("&", "§").replace("%player%", targetPlayer.getName())).toList());
                for(ItemFlag flag : flags){
                    itemMeta.addItemFlags(flag);
                }
                item.setItemMeta(itemMeta);
            }
            if(targetPlayer.getInventory().firstEmpty() != -1) {
                targetPlayer.getInventory().addItem(item);
                targetPlayer.sendMessage("§aYou received a present from " + p.getName());
                presentHashMap.put(targetPlayer, this);
            }else{
                p.sendMessage("§cThe player " + targetPlayer.getName() + " does not have space in inventory!");
            }
        }
    }

    public String getName() {
        return name;
    }
    public Material getMaterial() {
        return material;
    }
    public List<String> getLore() {
        return lore;
    }
}
