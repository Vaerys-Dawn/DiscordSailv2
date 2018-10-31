package com.github.vaerys.objects;

import com.github.vaerys.masterobjects.CommandObject;

public class GuildLogObject {

    String type;
    String name;
    String contents;
    long channelID;
    long userID;
    long messageID;

    public GuildLogObject(CommandObject command, String type, String name, String contents) {
        this.type = type;
        this.name = name;
        this.contents = contents;
        this.channelID = command.channel.longID;
        this.userID = command.user.longID;
        this.messageID = command.message.longID;
    }

    public GuildLogObject(String type, String name, String contents, long channelID, long userID, long messageID) {
        this.type = type;
        this.name = name;
        this.contents = contents;
        this.channelID = channelID;
        this.userID = userID;
        this.messageID = messageID;
    }

    public GuildLogObject(GuildLogObject logObject) {
        this.type = logObject.type;
        this.name = logObject.name;
        this.contents = logObject.contents;
        this.channelID = logObject.channelID;
        this.userID = logObject.userID;
        this.messageID = logObject.messageID;
    }

    public String getOutput(CommandObject command) {
        String format = "{\"TYPE\": \"%s\", \"NAME\": \"%s\", \"CONTENTS\": \"%s\", \"GUILD\": %d, \"CHANNEL\": %d, \"USER\": %d, \"MESSAGE\": %d}";
        return String.format(format, type, name, contents, command.guild.longID, channelID, userID, messageID);
    }
}
