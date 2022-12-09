package org.com.github.leo_s.command;

import org.bukkit.entity.Player;

public interface ICommand {
    String getName();
    void perform(Player player, String[] args);
}
