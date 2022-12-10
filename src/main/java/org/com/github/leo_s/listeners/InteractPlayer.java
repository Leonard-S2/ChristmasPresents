package org.com.github.leo_s.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.com.github.leo_s.Christmas;

import java.lang.reflect.Field;
import java.util.*;

import static org.bukkit.Bukkit.getServer;
import static org.com.github.leo_s.Christmas.config;
import static org.com.github.leo_s.utils.files.Convert.convert;

@SuppressWarnings({"ConstantConditions", "DuplicatedCode", "deprecation"})
public class InteractPlayer implements Listener{
    public static HashMap<Player, Integer> delay_interact = new HashMap<>();
    public static HashMap<World, ArmorStand> armorStandHashMap = new HashMap<>();

    @EventHandler
    public void onInteractArmorStand(PlayerArmorStandManipulateEvent e){
        if(armorStandHashMap.containsKey(e.getRightClicked().getWorld())){
            if(e.getRightClicked().getUniqueId().equals(armorStandHashMap.get(e.getRightClicked().getWorld()).getUniqueId())){
                //send message
                e.getPlayer().sendMessage(convert(config.getString("interact-armorstand")));
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onInteractPlayer(PlayerInteractEntityEvent e){
        if(e.getRightClicked() instanceof Player target) {
            // do something
            Player player = e.getPlayer();
            if(player.getInventory().getItemInMainHand() == null) return;
            if (player.getInventory().getItemInMainHand().getItemMeta() == null) return;
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null) return;
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(
                    config.getString("present-item.name").replace("&", "§"))) {
                //10 seconds is equal to 5 seconds
                //Delay
                if(delay_interact.containsKey(player)) {
                    player.sendMessage(convert(config.getString("delay-present").replace("%delay%", String.valueOf(delay_interact.get(player) / 2))));
                    return;
                }
                delay_interact.put(player, (config.getInt("cooldown-open-present") * 2));

                //If you have more than 1 item, subtract -1 from the quantity of that item
                if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                    player.getInventory().getItemInMainHand().setAmount(
                            player.getInventory().getItemInMainHand().getAmount() - 1);
                } else {
                    //If you have only 1 item, remove it
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }

                //Send a message to the player and the player who received the present
                player.sendMessage(convert(config.getString("received-present-player").replace("%player%", target.getName())));
                target.sendMessage(convert(convert(config.getString("received-present-target").replace("%player%", player.getName()))));
                target.sendMessage(convert(convert(config.getString("opening-present"))));

                //Start Animation to open the present
                executeAnimation(target, player);

                //Start the countdown
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (delay_interact.get(player) == 0) {
                            sendRandomPresent(target);
                            target.playSound(target.getLocation(), Sound.valueOf(config.getString("sound-open-present")), 10, 1);

                            delay_interact.remove(player);
                            cancel();
                        } else {

                            //Prepare sound
                            NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
                            noteBlock.setInstrument(Instrument.PIANO);

                            noteBlock.setNote(Note.natural(0, Note.Tone.A));
                            noteBlock.setNote(Note.natural(0, Note.Tone.B));
                            noteBlock.setNote(Note.natural(0, Note.Tone.C));
                            noteBlock.setNote(Note.natural(0, Note.Tone.D));
                            noteBlock.setNote(Note.natural(0, Note.Tone.E));
                            noteBlock.setNote(Note.natural(0, Note.Tone.F));
                            noteBlock.setNote(Note.natural(0, Note.Tone.G));
                            noteBlock.setPowered(true);

                            //Play sound
                            target.playNote(target.getLocation(), noteBlock.getInstrument(), noteBlock.getNote());
                            delay_interact.put(player, delay_interact.get(player) - 1);
                        }
                    }
                };
                runnable.runTaskTimer(Christmas.instance, 0, 10);
            }
        }
    }

    public void sendRandomPresent(Player p){
        //Get random item for chance
        List<String> items_obtain = new ArrayList<>();
        for (String key : config.getConfigurationSection("items-loot").getKeys(false)) {
            for (String key2 : config.getConfigurationSection("items-loot." + key).getKeys(false)) {
                if (Math.random() <= config.getDouble("items-loot." + key + "." + key2 + ".chance")){
                    ItemStack item = new ItemStack(Material.getMaterial(config.getString("items-loot." + key + "." + key2 + ".material")));
                    item.setAmount(config.getInt("items-loot." + key + "." + key2 + ".amount"));
                    List<String> lore = new ArrayList<>();
                    for (String loreLine : config.getStringList("items-loot." + key + "." + key2 + ".lore")) {
                        lore.add(convert(loreLine));
                    }
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(convert(config.getString("items-loot." + key + "." + key2 + ".name")));
                    meta.setLore(lore);
                    for (String enchantment : config.getStringList("items-loot." + key + "." + key2 + ".enchantments")) {
                        String[] enchantmentSplit = enchantment.split(":");
                        meta.addEnchant(Enchantment.getByName(enchantmentSplit[0]),
                                Integer.parseInt(enchantmentSplit[1]), true);
                    }
                    for (String flag : config.getStringList("items-loot." + key + "." + key2 + ".flags")) {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    }
                    item.setItemMeta(meta);
                    p.getInventory().addItem(item);

                    if(config.getBoolean("items-loot." + key + "." + key2 + ".commands.enabled")){
                        for (String command : config.getStringList("items-loot." + key + "." + key2 + ".commands.commands")) {
                            getServer().dispatchCommand(getServer().getConsoleSender(), command.replace("%player%", p.getName()));
                        }
                    }

                    items_obtain.add(convert(config.getString("items-loot." + key + "." + key2 + ".name")));
                }
            }
        }

        //Not Obtain any item
        if(items_obtain.size() == 0) {
            p.sendMessage(convert(config.getString("not-received-rewards")));
        }else {
            //Obtain items
            for (String item : items_obtain) {
                p.sendMessage(convert(config.getString("yes-received-rewards").replace("%item%", item)));
            }
        }
    }

    public void executeAnimation(Player p, Player target){
        //Generate armor stand
        ArmorStand armorStand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().subtract(0, 1.3, 0), EntityType.ARMOR_STAND);
        armorStand.setCustomName(convert(config.getString("present-name-amor-stand").replace("%player%", p.getName())));
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(false);
        armorStand.setRemoveWhenFarAway(false);


        //Configure the item in the armor stand
        Material material = Material.getMaterial(config.getString("present-item.material"));
        ItemStack item = new ItemStack(material, 1);

        if(material == Material.PLAYER_HEAD){
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            GameProfile profile = new GameProfile(UUID.fromString("3b01e4c7-f07b-4cff-a744-88d7eae47249"), null);
            profile.getProperties().put("textures", new Property("textures", config.getString("present-item.texture")));
            try{
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            item.setItemMeta(skullMeta);
        }

        //Set item
        armorStand.setHelmet(item);
        armorStandHashMap.put(armorStand.getWorld(), armorStand);

        //Animation
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(!delay_interact.containsKey(target)){
                    armorStandHashMap.remove(armorStand.getWorld());
                    armorStand.remove();
                    cancel();
                }else{
                    //Spawn particles in circle
                    for (double i = 0; i < 2 * Math.PI; i += Math.PI / 16) {
                        double x = Math.cos(i) * 0.5;
                        double y = Math.sin(i) * 0.9;
                        double z = Math.sin(i) * 0.5;
                        armorStand.getWorld().spawnParticle(Particle.valueOf(config.getString("particle-open-present")),
                                armorStand.getLocation().add(x, y, z),
                                1, 0, 0, 0, 0);
                    }
                    armorStand.teleport(armorStand.getLocation().add(0, 0.008, 0));
                    armorStand.setRotation(armorStand.getLocation().getYaw() + 0.5f, 0);
                    armorStand.setHeadPose(new EulerAngle(armorStand.getHeadPose().getX() + 0.1,
                            armorStand.getHeadPose().getY() + 0.1, armorStand.getHeadPose().getZ() + 0.1));
                }
            }
        };
        runnable.runTaskTimer(Christmas.instance, 0, 1);
    }
}