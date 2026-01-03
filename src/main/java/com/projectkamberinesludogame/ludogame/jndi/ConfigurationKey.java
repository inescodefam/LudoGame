package com.projectkamberinesludogame.ludogame.jndi;

public enum ConfigurationKey {
    PLAYER_RED_SERVER_PORT("player.red.server.port"),
    PLAYER_BLUE_SERVER_PORT("player.blue.server.port"),
    PLAYER_SINGLE_SERVER_PORT("player.single.server.port"),
    HOSTNAME("host.name"),
    RMI_PORT("rmi.server.port");

    private final String key;

    ConfigurationKey(final String key) {
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
}
