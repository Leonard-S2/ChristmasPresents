package org.com.github.leo_s.utils.files;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.com.github.leo_s.Christmas;

import java.io.File;

@SuppressWarnings("unused")
public class ConfigFile extends YamlConfiguration {
    private static ConfigFile config;
    private final File config_;

    public static ConfigFile getConfig() {
        if (ConfigFile.config == null) ConfigFile.config = new ConfigFile();
        return ConfigFile.config;
    }

    private Plugin main() {
        return Christmas.instance;
    }

    public ConfigFile() {
        Plugin plugin = this.main();
        this.config_ = new File(plugin.getDataFolder(), "config.yml");
        if (!this.config_.exists()) plugin.saveResource("config.yml", false);
        this.reload();
    }

    public void reload() {
        try {
            super.load(this.config_);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            super.save(this.config_);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        this.load();
        this.reload();
    }
}