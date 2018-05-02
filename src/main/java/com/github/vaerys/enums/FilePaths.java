package com.github.vaerys.enums;

public enum FilePaths {
    GUILD_CONFIG("Guild_Config.json"),
    CUSTOM_COMMANDS("Custom_Commands.json"),
    SERVERS("Servers.json"),
    CHARACTERS("Characters.json"),
    COMPETITION("Competition.json"),
    GUILD_USERS("Guild_Users.json"),
    CHANNEL_DATA("Channel_Data.json"),
    GUILD_LOG("Guild_Log.json"),
    GLOBAL_DATA("Global_Data.json"),
    CONFIG("Config.json"),
    DAILY_MESSAGES("DailyMessages.json"),
    EVENTS("Events.json"),
    ADMIN_CCS("Admin_CCs.json");

    private String name;

    FilePaths(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
