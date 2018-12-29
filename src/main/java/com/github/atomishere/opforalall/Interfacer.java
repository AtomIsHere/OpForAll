package com.github.atomishere.opforalall;

import lombok.Getter;

public class Interfacer {
    @Getter
    private static OpForAll plugin;

    public static void setPlugin(OpForAll plugin) {
        if(Interfacer.plugin == null) {
            Interfacer.plugin = plugin;
        }
    }
}
