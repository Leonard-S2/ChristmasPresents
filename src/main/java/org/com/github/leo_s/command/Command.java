package org.com.github.leo_s.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.com.github.leo_s.command.list.CommandGive;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "NullableProblems"})
public class Command implements CommandExecutor {
    private final List<ICommand> command_list = new ArrayList<>();

    public Command() {
        command_list.add(new CommandGive());
    }
    public List<ICommand> getCommand_list() {return command_list;}

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command c, String s, String[] args){
        if(!(sender instanceof Player p)){
            return true;
        }
        if(args.length > 0){
            for(int i = 0; i < command_list.size(); i++){
                if(args[0].equalsIgnoreCase(getCommand_list().get(i).getName())){
                    getCommand_list().get(i).perform(p, args);
                }
            }
        }
        return true;
    }
}
