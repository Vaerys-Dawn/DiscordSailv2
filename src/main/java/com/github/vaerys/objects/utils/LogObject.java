package com.github.vaerys.objects.utils;

import com.github.vaerys.masterobjects.CommandObject;

public class LogObject extends GuildLogObject {

    long guildID;

    public LogObject(GuildLogObject logObject, long guildID) {
        super(logObject);
        this.guildID = guildID;
    }

    public LogObject(CommandObject command, String type, String name, String contents, long guildID) {
        super(command, type, name, contents);
        this.guildID = guildID;
    }

    public LogObject(String type, String name, String contents, long channelID, long userID, long messageID, long guildID) {
        super(type, name, contents, channelID, userID, messageID);
        this.guildID = guildID;
    }

    public String getOutput() {
        String format = "{\"TYPE\": \"%s\", \"NAME\": \"%s\", \"CONTENTS\": \"%s\", \"GUILD\": %d, \"CHANNEL\": %d, \"USER\": %d, \"MESSAGE\": %d}";
        return String.format(format, type, name, contents, guildID, channelID, userID, messageID);
    }
}
