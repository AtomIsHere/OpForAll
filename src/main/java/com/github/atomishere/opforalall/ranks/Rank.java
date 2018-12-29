package com.github.atomishere.opforalall.ranks;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Rank {
    NON_OP("nonOp", null, null, ChatColor.DARK_GRAY),
    OP("op", null, null, ChatColor.GRAY),
    MOD("mod", "MOD", ChatColor.LIGHT_PURPLE, ChatColor.WHITE),
    ADMIN("admin", "ADMIN", ChatColor.DARK_PURPLE, ChatColor.WHITE),
    OWNER("owner", "OWNER", ChatColor.RED, ChatColor.GOLD);

    @Getter
    private final String rankName;
    private final String rankPrefix;
    @Getter
    private final ChatColor rankColor;
    @Getter
    private final ChatColor nameColor;

    private Rank(String rankName, String rankPrefix, ChatColor rankColor, ChatColor nameColor) {
        this.rankName = rankName;
        this.rankPrefix = rankPrefix;
        this.rankColor = rankColor;
        this.nameColor = nameColor;
    }

    public String getRankPrefix() {
        if(rankPrefix == null) {
            return "";
        }

        return ChatColor.DARK_GRAY + "[" + rankColor + rankPrefix + ChatColor.DARK_GRAY + "] ";
    }


    public static Rank getRankByName(String name) {
        for(Rank rank : values()) {
            if(rank.getRankName().equals(name)) {
                return rank;
            }
        }

        throw new NullPointerException();
    }

    private int getOrder() {
        return ordinal();
    }

    public boolean isGreaterThen(Rank rank) {
        return this.getOrder() >= rank.getOrder();
    }
}
