package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.Utils;
import com.github.atomishere.opforalall.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(requiresRank = Rank.OP, source = CommandSource.BOTH, description = "Op someone on the server", usage = "/op <player name>")
public class Command_op extends OFCommand {
    public Command_op(OpForAll plugin, String name) {
        super(plugin, name);
    }

    @Override
    @SuppressWarnings("depreciation")
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args) {
        if(args.length == 1) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if(target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }

            Utils.opPlayer(target);
            Utils.sendServerMessage(sender.getName() + " has opped " + target.getName(), false);
            return true;
        }
        sender.sendMessage(ChatColor.RED + this.getUsage());
        return true;
    }
}
