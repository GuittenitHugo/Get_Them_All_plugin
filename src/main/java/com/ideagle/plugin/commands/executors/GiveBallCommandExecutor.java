package com.ideagle.plugin.commands.executors;

import com.ideagle.plugin.catchballs.BasicBall;
import com.ideagle.plugin.catchballs.BrittleBall;
import com.ideagle.plugin.catchballs.Catchball;
import com.ideagle.plugin.catchballs.PerfectBall;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.ArrayLen;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GiveBallCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("getthemall.command.giveball")){
            Player receiver = null;
            int qty = 1;
            if(args.length >= 1){

                /* If only 1 argument is specified, then
                * we know that the sender wants a single ball */
                if (args.length == 1){
                    /* If the one executing the command is not a player,
                     * then a player should have been specified */
                    if(sender instanceof Player) {
                        receiver = ((Player) sender).getPlayer();
                    } else {
                        sender.sendMessage(ChatColor.RED + "You must specify a player.");
                        return false;
                    }
                }

                /* If there are only 2 arguments, either
                a player or an amount has been specified */
                if (args.length == 2){
                    String arg1 = args[1];

                    /* Trying to parse second argument as an integer.
                    * If it doesn't work, it's likely that this argument
                    * is the receiver's player name */
                    try {
                        qty = Integer.parseInt(arg1);

                        /* If the one executing the command is not a player,
                        * then a player should have been specified */
                        if(sender instanceof Player) {
                            receiver = ((Player) sender).getPlayer();
                        } else {
                            sender.sendMessage(ChatColor.RED + "You must specify a player.");
                            return false;
                        }
                    }
                    catch (Exception ignored){
                        receiver = Bukkit.getPlayer(arg1);

                        /* If the receiver isn't online, the command won't
                        * be able to give an item */
                        if (receiver == null){
                            sender.sendMessage(ChatColor.RED + "Specified player isn't online.");
                            return false;
                        }
                    }
                }

                /* If 3 arguments are specified, the last 2 are
                * player and quantity of catchballs*/
                if (args.length == 3){
                    /* Trying to parse the quantity. If it doesn't work,
                    * then an error is shown to the sender */
                    try {
                        qty = Integer.parseInt(args[2]);
                    } catch (Exception ignored){
                        sender.sendMessage(ChatColor.RED + "Specified quantity isn't valid.");
                        return false;
                    }

                    /* Trying to find the player. If it doesn't work,
                     * then an error is shown to the sender */
                    receiver = Bukkit.getPlayer(args[1]);
                    if (receiver == null){
                        sender.sendMessage(ChatColor.RED + "Specified player isn't online.");
                        return false;
                    }

                }
                /* If too many arguments are specified, we don't want to handle anything */
                if (args.length > 3) {
                    sender.sendMessage(ChatColor.RED + "Too many arguments.");
                    return false;
                }

            }
            /* Too few arguments have been specified */
            else{
                sender.sendMessage(ChatColor.RED + "Too few arguments.");
                return false;
            }

            /* catchball type handling */

            List<String> validBallTypes = Arrays.asList(
                    "brittle", "basic", "perfect"
            );

            if (validBallTypes.contains(args[0].toLowerCase(Locale.ROOT))){

                ItemStack catchballItem = new ItemStack(Material.SNOWBALL, 1);
                NBTItem catchallItemNBT = new NBTItem(catchballItem);

                switch (args[0]){
                    case "brittle":
                        catchallItemNBT = new BrittleBall().toNBTItem(catchallItemNBT);
                        break;
                    case "basic":
                        catchallItemNBT = new BasicBall().toNBTItem(catchallItemNBT);
                        break;
                    case "perfect":
                        catchallItemNBT = new PerfectBall().toNBTItem(catchallItemNBT);
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Something went wrong.\n");
                        return false;
                }

                /* Giving handling */
                catchballItem = catchallItemNBT.getItem();

                if (receiver != null) {
                    do {
                        catchballItem.setAmount(
                                Math.min(
                                        qty,
                                        catchballItem.getMaxStackSize()
                                )
                        );

                        if (receiver.getInventory().firstEmpty() > -1) {
                            receiver.getInventory().addItem(catchballItem);
                        } else {
                            receiver.getWorld().dropItem(receiver.getLocation(), catchballItem);
                        }

                        qty -= Math.min(qty, catchballItem.getMaxStackSize());

                    } while (qty > 0);



                    return true;
                }
                return false;
            }
        }
        sender.sendMessage(command.getPermissionMessage());
        return false;
    }
}
