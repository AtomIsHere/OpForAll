package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.Utils;
import com.github.atomishere.opforalall.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

@CommandInfo(requiresRank = Rank.ADMIN, source = CommandSource.BOTH, description = "Give someone a rank", usage = "/rank <player> <rank>, /rank <player")
public class Command_rank extends OFCommand {
    private final static Map<Player, Player> tempSession = new HashMap<>();

    public Command_rank(OpForAll plugin, String name) {
        super(plugin, name);
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args) {
        if(args.length == 1 && sender instanceof Player) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if(target == null) {
                playerSender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            Inventory rankGui = Bukkit.getServer().createInventory(playerSender, 9, "Rank Menu");

            ItemStack mod = new ItemStack(Material.LAPIS_BLOCK);
            ItemMeta modMeta = mod.getItemMeta();

            modMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mod");
            mod.setItemMeta(modMeta);
            rankGui.setItem(0, mod);

            ItemStack admin = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta adminMeta = admin.getItemMeta();

            adminMeta.setDisplayName(ChatColor.DARK_PURPLE + "Admin");
            admin.setItemMeta(adminMeta);
            rankGui.setItem(1, admin);

            ItemStack owner = new ItemStack(Material.DIAMOND_BLOCK);
            ItemMeta ownerMeta = owner.getItemMeta();

            ownerMeta.setDisplayName(ChatColor.RED + "Owner");
            owner.setItemMeta(ownerMeta);
            rankGui.setItem(2, owner);

            playerSender.openInventory(rankGui);

            tempSession.put(playerSender, target);
            return true;
        } else if(args.length == 2) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            Rank rank = Rank.getRankByName(args[1]);
            if(target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return true;
            } else if(rank == null) {
                sender.sendMessage(ChatColor.RED + "Rank not found");
                return true;
            }

            plugin.getRankManager().setRank(target.getUniqueId(), rank);
            sender.sendMessage(ChatColor.GREEN + "Success");
            Utils.sendServerMessage(target.getName() + "Rank has been set to " + rank.getRankName(), true);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().getName().equals("Rank Menu")) {
            ItemStack clickedItem = event.getCurrentItem();
            String name = clickedItem.getItemMeta().getDisplayName();

            if(!(event.getWhoClicked() instanceof Player)) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                return;
            }

            Player player = (Player) event.getWhoClicked();
            Rank playerRank = plugin.getRankManager().getRank(player.getUniqueId());
            Player target = tempSession.get(player);
            if(target == null) {
                event.setCancelled(true);
                player.closeInventory();
                return;
            }

            switch(name) {
                case "Mod":
                    if (!playerRank.isGreaterThen(Rank.MOD)) {
                        event.setCancelled(true);
                        player.closeInventory();
                        player.sendMessage("Insufficient Permissions");
                        return;
                    } else {
                        plugin.getRankManager().setRank(target.getUniqueId(), Rank.MOD);
                        Utils.sendServerMessage(target.getName() + "Rank has been set to " + Rank.MOD.getRankName(), true);
                    }
                    break;
                case "Admin":
                    if (!playerRank.isGreaterThen(Rank.ADMIN)) {
                        event.setCancelled(true);
                        player.closeInventory();
                        player.sendMessage("Insufficient Permissions");
                        return;
                    } else {
                        plugin.getRankManager().setRank(target.getUniqueId(), Rank.ADMIN);
                        Utils.sendServerMessage(target.getName() + "Rank has been set to " + Rank.ADMIN.getRankName(), true);
                    }
                    break;
                case "Owner":
                    if (!playerRank.isGreaterThen(Rank.OWNER)) {
                        event.setCancelled(true);
                        player.closeInventory();
                        player.sendMessage("Insufficient Permissions");
                        return;
                    } else {
                        plugin.getRankManager().setRank(target.getUniqueId(), Rank.OWNER);
                        Utils.sendServerMessage(target.getName() + "Rank has been set to " + Rank.OWNER.getRankName(), true);
                    }
                    break;
            }

            player.sendMessage(ChatColor.GREEN + "Success");
            event.setCancelled(true);
            player.closeInventory();
        }
    }
}
