package org.com.github.leo_s;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.com.github.leo_s.command.Command;
import org.com.github.leo_s.listeners.BlockBreak;
import org.com.github.leo_s.listeners.BlockPlace;
import org.com.github.leo_s.listeners.InteractPlayer;
import org.com.github.leo_s.utils.files.ConfigFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.com.github.leo_s.listeners.InteractPlayer.armorStandHashMap;
import static org.com.github.leo_s.utils.files.Convert.convert;

@SuppressWarnings("ConstantConditions")
public class Christmas extends JavaPlugin{
    public static Christmas instance;
    public static FileConfiguration config;
    public String version;
    public String latestversion;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        version = getDescription().getVersion();

        config = ConfigFile.getConfig();
        ConfigFile.getConfig().load();

        getCommand("present").setExecutor(new Command());

        // Register events
        getServer().getPluginManager().registerEvents(new InteractPlayer(), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);

        updateChecker();
        try {
            updateMessages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        //armorStandHashMap([World], [ArmorStand])
        for (ArmorStand armorStand : armorStandHashMap.values()) {
            armorStand.remove();
        }
    }
    public void updateChecker(){
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=106634").openConnection();
            int timed_out = 1250;
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);
            latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (latestversion.length() <= 7) {
                if(!version.equals(latestversion)){
                    Bukkit.getConsoleSender().sendMessage(convert("&c[Christmas] &cThere is a new version available! &e" + latestversion));
                    Bukkit.getConsoleSender().sendMessage(convert("&c[Christmas] &aYou can download it here: &ehttps://www.spigotmc.org/resources/106634"));
                }
            }
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(convert("&c[Christmas] &cFailed to check for a update on spigot."));
        }
    }

    public void updateMessages() throws IOException {
        Path path = Paths.get(getDataFolder() + File.separator + "config.yml");
        String old = new String(Files.readAllBytes(path));

        if(!old.contains("cooldown-open-present:")){
            config.set("cooldown-open-present", 5);
            config.set("particle-open-present", "SNOW_SHOVEL");
            config.set("sound-open-present", "ENTITY_PLAYER_LEVELUP");
            config.set("present-name-amor-stand", "&c¡Merry Christmas &f%player%&c!");
            config.set("version", "2.0");
            ConfigFile.getConfig().save();
        }
        if(!old.contains("interact-armorstand:")){
            config.set("interact-armorstand", "%prefix%&cYou can't open presents yet!");
            ConfigFile.getConfig().save();
        }

        if(!old.contains("max-rewards-per-present:")){
            config.set("max-rewards-per-present", 1);
            ConfigFile.getConfig().save();
        }
    }
}
