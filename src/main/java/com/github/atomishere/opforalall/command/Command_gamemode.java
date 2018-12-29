package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Scanner;

@CommandInfo(requiresRank = Rank.OP, source = CommandSource.BOTH, description = "Change your gamemode", usage = "/gamemode <gamemode id> [player]")
public class Command_gamemode extends OFCommand {
    public Command_gamemode(OpForAll plugin, String name) {
        super(plugin, name);
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args) {
        if(args.length == 1) {
            if(playerSender == null) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this");
                return true;
            }

            GameMode gameMode = getGameMode(args[0]);
            if(gameMode == null) {
                playerSender.sendMessage(ChatColor.RED + "Gamemode not found");
                return true;
            }
            playerSender.setGameMode(gameMode);
            playerSender.sendMessage(ChatColor.AQUA + "Your gamemode has been set to " + gameMode.name());
            return true;
        } else if(args.length == 2) {
            GameMode gameMode = getGameMode(args[0]);
            if(gameMode == null) {
                playerSender.sendMessage(ChatColor.RED + "Gamemode not found");
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[1]);
            if(target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }

            target.setGameMode(gameMode);
            target.sendMessage(ChatColor.AQUA + "Your gamemode has been set to " + gameMode.name());
            sender.sendMessage(ChatColor.AQUA + "Set " + target.getName() + "'s gamemode to " + gameMode.name());
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + this.usageMessage);
            return true;
        }
    }

    private boolean isInteger(String string) {
        Scanner sc = new Scanner(string.trim());
        if(!sc.hasNextInt(10)) {
            return false;
        }

        sc.nextInt(10);
        return !sc.hasNext();
    }

    @SuppressWarnings("deprecitation")
    private GameMode getGameMode(String gamemode) {
        if(isInteger(gamemode)) {
            return GameMode.getByValue(Integer.parseInt(gamemode));
        }

        GameMode gameMode;
        try {
            gameMode = GameMode.valueOf(gamemode.toUpperCase());
        } catch(NullPointerException ex) {
            gameMode = null;
        }

        return gameMode;
    }
}
