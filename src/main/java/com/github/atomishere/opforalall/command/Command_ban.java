package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.Utils;
import com.github.atomishere.opforalall.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;

@CommandInfo(requiresRank = Rank.MOD, source = CommandSource.BOTH, description = "Ban a player", usage = "/ban <player> <reason> [time] [-s]")
public class Command_ban extends OFCommand {
    public Command_ban(OpForAll plugin, String name) {
        super(plugin, name);
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args) {
        if(args.length == 2) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            String banReason = args[1];

            if(!banPlayerForCmd(target, banReason, sender)) {
                return true;
            }

            Utils.sendServerMessage(sender.getName() + "has banned " + target.getName(), true);
        } else if(args.length == 3 && args[2].equalsIgnoreCase("-s")) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            String banReason = args[1];

            banPlayerForCmd(target, banReason, sender);
        } else if(args.length == 3) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            String banReason = args[1];
            String timeTillExpiry = args[2];

            if(!banPlayerForCmd(target, banReason, timeTillExpiry, sender)) {
                return true;
            }

            Utils.sendServerMessage(sender.getName() + "has banned " + target.getName(), true);
        } else if(args.length == 4 && args[3].equalsIgnoreCase("-s")) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            String banReason = args[1];
            String timeTillExpiry = args[2];

            banPlayerForCmd(target, banReason, timeTillExpiry, sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
        }

        return true;
    }

    private boolean banPlayerForCmd(Player target, String banReason, CommandSender sender) {
        if(target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return false;
        }

        plugin.getBanManager().createBan(banReason, target.getUniqueId());
        return true;
    }

    private boolean banPlayerForCmd(Player target, String banReason, String timeTillExpiry, CommandSender sender) {
        if(target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return false;
        }

        Calendar cal = Calendar.getInstance();
        if(timeTillExpiry.endsWith("d")) {
            int expiryTime;
            try {
                expiryTime = Integer.parseInt(timeTillExpiry.replace("d", ""));
            } catch(NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Invalid time format");
                return false;
            }

            cal.add(Calendar.DAY_OF_MONTH, expiryTime);
        } else if(timeTillExpiry.endsWith("m")) {
            int expiryTime;
            try {
                expiryTime = Integer.parseInt(timeTillExpiry.replace("m", ""));
            } catch(NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Invalid time format");
                return false;
            }

            cal.add(Calendar.MONTH, expiryTime);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid time format");
            return false;
        }

        Date expiryDate = cal.getTime();

        plugin.getBanManager().createBan(banReason, target.getUniqueId(), expiryDate);
        return true;
    }
}
