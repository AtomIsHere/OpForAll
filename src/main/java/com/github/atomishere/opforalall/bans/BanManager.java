package com.github.atomishere.opforalall.bans;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.service.OFService;
import com.github.atomishere.spigotjson.JsonConfig;
import com.github.atomishere.spigotjson.JsonSection;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class BanManager extends OFService {
    private final Map<UUID, Ban> bans = new HashMap<>();
    private JsonConfig config;

    public BanManager(OpForAll plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        File configFile =  new File(plugin.getDataFolder(), "bans.json");
        try {
            config = new JsonConfig(configFile);
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load bans! Failed to read json file", ex);
            return;
        }

        JsonSection bansSection = config.getSection("bans");
        if(bansSection == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for(Object key : bansSection.getKeySet()) {
            JsonSection banSection = bansSection.getSection(key);
            if(banSection == null) {
                continue;
            }

            String reason = banSection.getString("reason");
            String stringUUID = banSection.getString("player");

            Boolean isPermanent = banSection.getBoolean("permanent");
            Date date = null;
            if(isPermanent) {
                String dateAsString = banSection.getString("expiryDate");
                if(dateAsString == null) {
                    plugin.getLogger().severe("Expiry Date not found in a Temporary Ban");
                    continue;
                }

                try {
                    date = sdf.parse(dateAsString);
                } catch(ParseException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Unable to parse date in a temporary ban. Invalid date format!", ex);
                    continue;
                }
            }

            if(reason == null || stringUUID == null) {
                plugin.getLogger().severe("Invalid ban entry in ban config");
                continue;
            }

            UUID player = UUID.fromString(stringUUID);

            Ban banEntry;
            if(date == null) {
                banEntry = new Ban(reason, player);
            } else {
                banEntry = new Ban(reason, player, date);
            }

            bans.put(player, banEntry);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Calendar cal = Calendar.getInstance();
                    Date currentDate = cal.getTime();
                    for(Ban ban : bans.values()) {
                        if(ban.isPermanent()) {
                            continue;
                        }

                        Date expiryDate = ban.getExpiryDate();

                        if(expiryDate.after(currentDate)) {
                            revokeBan(ban.getPlayer());
                        }
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0, 72000);
        }
    }

    @Override
    protected void onStop() {
        JsonSection bansSection = config.createSection("bans");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for(Ban ban : bans.values()) {
            JsonSection banSection = bansSection.createSection(ban.getPlayer().toString());

            banSection.set("reason", ban.getReason());
            banSection.set("player", ban.getPlayer().toString());
            if(!ban.isPermanent()) {
                banSection.set("permanent", false);
                banSection.set("expiryDate", sdf.format(ban.getExpiryDate()));
            } else {
                banSection.set("permanent", true);
            }
        }
    }

    public boolean isBanned(UUID player) {
        return bans.containsKey(player);
    }

    public Ban getBan(UUID player) {
        return bans.get(player);
    }

    public void revokeBan(UUID player) {
        bans.remove(player);
    }

    public void createBan(String reason, UUID player) {
        Ban ban = new Ban(reason, player);
        bans.put(player, ban);
    }

    public void createBan(String reason, UUID player, Date expiryDate) {
        Ban ban = new Ban(reason, player, expiryDate);
        bans.put(player, ban);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if(isBanned(event.getPlayer().getUniqueId())) {
            Player player = event.getPlayer();
            Ban ban = getBan(player.getUniqueId());

            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("You are banned for: " + ban.getReason());
            player.sendMessage("You can still join the server but you just can't interact with anything.");
        }
    }

    @EventHandler
    public void onCommandPreProcessEvent(PlayerCommandPreprocessEvent event) {
        if(isBanned(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't execute commands when your banned!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        if(isBanned(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't talk when your banned");
            event.setCancelled(true);
        }
    }

    @Override
    public String getName() {
        return "banManager";
    }
}
