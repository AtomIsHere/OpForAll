package com.github.atomishere.opforalall.service;

import com.github.atomishere.opforalall.OpForAll;
import org.bukkit.event.Listener;

public abstract class OFService implements Listener {
    protected final OpForAll plugin;
    private final boolean critical;
    private boolean status = false;

    public OFService(OpForAll plugin) {
        this.plugin = plugin;
        this.critical = false;
    }

    public OFService(OpForAll plugin, Boolean critical) {
        this.plugin = plugin;
        this.critical = critical;
    }

    public final void startService() {
        onStart();
        status = true;
    }

    public final void stopService() {
        onStop();
        status = false;
    }

    public final boolean started() {
        return status;
    }

    public final boolean isCritical() {
        return critical;
    }

    protected abstract void onStart();
    protected abstract void onStop();
    public abstract String getName();
}
