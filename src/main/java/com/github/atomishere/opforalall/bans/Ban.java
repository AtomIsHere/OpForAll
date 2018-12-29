package com.github.atomishere.opforalall.bans;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

//Simple Container Class. Ignore this.
public class Ban {
    @Getter
    private final String reason;
    @Getter
    private final UUID player;
    @Getter
    private final boolean isPermanent;
    @Getter
    private final Date expiryDate;

    public Ban(String reason, UUID player) {
        this.reason = reason;
        this.player = player;
        this.isPermanent = true;
        this.expiryDate = null;
    }

    public Ban(String reason, UUID player, Date expiryDate) {
        this.reason = reason;
        this.player = player;
        this.isPermanent = false;
        this.expiryDate = expiryDate;
    }
}
