package org.com.github.leo_s.command.list;

import org.bukkit.entity.Player;
import org.com.github.leo_s.command.ICommand;

import static org.com.github.leo_s.Christmas.config;
import static org.com.github.leo_s.present.Present.givePresentItem;
import static org.com.github.leo_s.utils.files.Convert.convert;

@SuppressWarnings("ConstantConditions")
public class CommandGive implements ICommand {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(!player.hasPermission("christmas.give")){
            player.sendMessage(convert(config.getString("no-permission")));
            return;
        }
        if(args.length == 3) {
            givePresentItem(player, args[1], Integer.parseInt(args[2]));
        }else{
            player.sendMessage("§cUse: §6/present give <player> <amount>");
        }
    }
}
