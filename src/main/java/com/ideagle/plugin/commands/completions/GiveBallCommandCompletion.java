package com.ideagle.plugin.commands.completions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveBallCommandCompletion implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1){
            return Arrays.asList("brittle", "basic", "perfect");
        }

        if(args.length == 2){
            ArrayList<String> players = new ArrayList<>();
            for (Player player :
                    Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return players;
        }

        return new ArrayList<>();
    }


}
