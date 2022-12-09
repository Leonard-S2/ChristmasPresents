package org.com.github.leo_s.command.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.com.github.leo_s.command.ICommand;
import org.com.github.leo_s.present.Present;

import static org.com.github.leo_s.Christmas.config;
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
            ItemFlag[] itemFlags = new ItemFlag[]{};
            for(String flag : config.getStringList("present-item.flags")){
                itemFlags = new ItemFlag[]{ItemFlag.valueOf(flag)};
            }
            try {
                Integer.parseInt(args[2]);
            }catch (NumberFormatException e){
                player.sendMessage("§cThe amount must be a number!");
                return;
            }
            new Present(
                    config.getString("present-item.name"),
                    Material.getMaterial(config.getString("present-item.material")),
                    config.getStringList("present-item.lore"), itemFlags).givePresentItem(player, args[1], Integer.parseInt(args[2]));
        }else{
            player.sendMessage("§cUse: §6/present give <player> <amount>");
        }
    }
}
