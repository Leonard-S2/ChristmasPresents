package org.com.github.leo_s.command.list;

import org.bukkit.entity.Player;
import org.com.github.leo_s.command.ICommand;
import org.com.github.leo_s.utils.files.ConfigFile;

import static org.com.github.leo_s.Christmas.config;
import static org.com.github.leo_s.utils.files.Convert.convert;

@SuppressWarnings("ConstantConditions")
public class CommandReload implements ICommand {
    @Override
    public String getName() {
        return "reloas";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(!player.hasPermission("christmas.reload")){
            player.sendMessage(convert(config.getString("no-permission")));
            return;
        }
        if(args.length == 1){
            ConfigFile.getConfig().reload();
            player.sendMessage("§aThe config has been reloaded!");
        }
    }
}
