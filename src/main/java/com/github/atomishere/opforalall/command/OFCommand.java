package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class OFCommand extends BukkitCommand implements Listener {
    protected final OpForAll plugin;

    private final String NO_PERMISSION = ChatColor.RED + "You do not have permission to execute that command";

    public OFCommand(OpForAll plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    public abstract boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args);

    @Override
    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!hasPermission(sender)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        Player playerSender;
        try {
            playerSender = (Player) sender;
        } catch(ClassCastException ex) {
            playerSender = null;
        }

        return run(sender, playerSender, commandLabel, args);
    }

    private boolean hasPermission(CommandSender sender) {
        Class<?> clazz = this.getClass();
        CommandInfo commandInfo = clazz.getAnnotation(CommandInfo.class);

        if(commandInfo.source().equals(CommandSource.BOTH)) {
            return sender instanceof ConsoleCommandSender || sender instanceof Player && hasPermission((Player) sender);
        } else if(commandInfo.source().equals(CommandSource.CONSOLE)) {
            return sender instanceof ConsoleCommandSender;
        } else {
            return sender instanceof Player && hasPermission((Player) sender);
        }
    }

    private boolean hasPermission(Player player) {
        Class<?> clazz = this.getClass();
        CommandInfo commandInfo = clazz.getAnnotation(CommandInfo.class);

        Rank rank = plugin.getRankManager().getRank(player.getUniqueId());
        return rank.isGreaterThen(commandInfo.requiresRank());
    }
}
