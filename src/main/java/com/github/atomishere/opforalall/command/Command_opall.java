package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.Utils;
import com.github.atomishere.opforalall.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(requiresRank = Rank.MOD, source = CommandSource.BOTH, description = "Op everyone on the server", usage =  "/opall")
public class Command_opall extends OFCommand {
    public Command_opall(OpForAll plugin, String name) {
        super(plugin, name);
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args) {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            Utils.opPlayer(player);
        }

        Utils.sendServerMessage(sender.getName() + " - Opped all players", false);
        return true;
    }
}
