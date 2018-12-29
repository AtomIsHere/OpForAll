package com.github.atomishere.opforalall.service;

import com.github.atomishere.opforalall.OpForAll;

public abstract class CustomService extends OFService {
    public CustomService(OpForAll plugin) {
        super(plugin);
    }

    public abstract String getPluginName();
}
