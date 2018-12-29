package com.github.atomishere.opforalall;

import com.github.atomishere.opforalall.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.inventivetalent.particle.ParticleEffect;

public final class Utils {
    private static OpForAll plugin;

    private Utils() {
        throw new AssertionError();
    }

    public static void initUtils(OpForAll plugin) {
        if(Utils.plugin == null) {
            Utils.plugin = plugin;
        }
    }

    public static void opPlayer(Player player) {
        Rank playerRank = plugin.getRankManager().getRank(player.getUniqueId());
        if(!playerRank.isGreaterThen(Rank.OP)) {
            plugin.getRankManager().setRank(player.getUniqueId(), Rank.OP);
        }
    }

    public static void deOpPlayer(Player player) {
        Rank playerRank = plugin.getRankManager().getRank(player.getUniqueId());
        if(playerRank == Rank.OP) {
            plugin.getRankManager().setRank(player.getUniqueId(), Rank.NON_OP);
        }
    }

    public static void sendServerMessage(String message, boolean important) {
        if(important) {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "(SERVER MESSAGE) - " + message);
        } else {
            Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "(SERVER MESSAGE) - " + message);
        }
    }

    public static void punishEffect(Player player) {
        player.getWorld().strikeLightning(player.getLocation());
        ParticleEffect.EXPLOSION_HUGE.send(Bukkit.getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
        player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 10, 1);
        player.setHealth(0);
    }
}
