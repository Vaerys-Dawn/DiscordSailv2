package com.github.vaerys.objects;

public class LogObject extends GuildLogObject {

    long guildID;

    public LogObject(GuildLogObject logObject, long guildID) {
        super(logObject);
        this.guildID = guildID;
    }

    public String getOutput() {
        String format = "{\"TYPE\": \"%s\", \"NAME\": \"%s\", \"CONTENTS\": \"%s\", \"GUILD\": %d, \"CHANNEL\": %d, \"USER\": %d, \"MESSAGE\": %d}";
        return String.format(format, type, name, contents, guildID, channelID, userID, messageID);
    }
}
