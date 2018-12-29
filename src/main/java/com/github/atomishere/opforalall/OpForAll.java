package com.github.atomishere.opforalall;

import com.github.atomishere.opforalall.bans.BanManager;
import com.github.atomishere.opforalall.command.CommandManager;
import com.github.atomishere.opforalall.ranks.RankManager;
import com.github.atomishere.opforalall.service.ServiceLoader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class OpForAll extends JavaPlugin {
    @Getter
    private ServiceLoader serviceLoader;

    @Getter
    private RankManager rankManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private BanManager banManager;

    @Override
    public void onLoad() {
        serviceLoader = new ServiceLoader(this);
        Interfacer.setPlugin(this);
    }

    @Override
    public void onEnable() {
        rankManager = serviceLoader.loadService(RankManager.class);
        commandManager = serviceLoader.loadService(CommandManager.class);
        banManager = serviceLoader.loadService(BanManager.class);

        Utils.initUtils(this);

        serviceLoader.startCriticalServices();
        serviceLoader.startNonCrticicalServices();
    }

    @Override
    public void onDisable() {
        serviceLoader.stopCriticalServices();
        serviceLoader.stopNonCriticalServices();
        serviceLoader.clearServices();
    }
}
