package com.github.atomishere.opforalall.ranks;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.service.OFService;
import com.github.atomishere.spigotjson.JsonConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RankManager extends OFService {
    private final Map<UUID, Rank> ranks = new HashMap<>();
    private JsonConfig jsonConfig;

    public RankManager(OpForAll plugin) {
        super(plugin, false);
    }

    @Override
    protected void onStart() {
        File configFile = new File(plugin.getDataFolder(), "ranks.json");
        try {
            jsonConfig = new JsonConfig(configFile);
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load ranks! Failed to read json file", ex);
            return;
        }

        Map<String, String> rankMap = jsonConfig.getMap("ranks", String.class, String.class);
        if(rankMap == null) {
            return;
        }

        for(Map.Entry<String, String> entry : rankMap.entrySet()) {
            UUID playerUUID;
            Rank playerRank;
            try {
                 playerUUID = UUID.fromString(entry.getKey());
                 playerRank = Rank.getRankByName(entry.getValue());
            } catch(NullPointerException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not load rank entry! Invalid data", ex);
                continue;
            }

            ranks.put(playerUUID, playerRank);
        }
    }

    @Override
    protected void onStop() {
        Map<String, String> jsonReadyRanks = new HashMap<>();

        for(Map.Entry<UUID, Rank> entry : ranks.entrySet()) {
            jsonReadyRanks.put(entry.getKey().toString(), entry.getValue().getRankName());
        }

        jsonConfig.set("ranks", jsonReadyRanks);

        try {
            jsonConfig.save();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save rank config! Failed to write to json config.", ex);
        }
    }

    @Override
    public String getName() {
        return "rankManager";
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!ranks.containsKey(player.getUniqueId())) {
            ranks.put(player.getUniqueId(), Rank.OP);
        }
        Rank rank = getRank(player.getUniqueId());

        player.setDisplayName(rank.getRankPrefix() + rank.getNameColor() + player.getName() + ChatColor.GRAY);
    }

    public Rank getRank(UUID uuid) {
        return ranks.get(uuid);
    }

    public void setRank(UUID uuid, Rank rank) {
        ranks.put(uuid, rank);
    }
}
