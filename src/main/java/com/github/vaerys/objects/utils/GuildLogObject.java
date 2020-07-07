package com.github.vaerys.objects.utils;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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

    public GuildLogObject(User user, TextChannel channel, String type, String name, String contents) {
        this.type = type;
        this.name = name;
        this.contents = contents;
        this.messageID = -1;
        this.channelID = channel.getIdLong();
        this.userID = user.getIdLong();
    }

    public String getOutput(GuildObject guild) {
        String format = "{\"TYPE\": \"%s\", \"NAME\": \"%s\", \"CONTENTS\": \"%s\", \"GUILD\": %d, \"CHANNEL\": %d, \"USER\": %d, \"MESSAGE\": %d}";
        return String.format(format, type, name, contents, guild.longID, channelID, userID, messageID);
    }

    public String getOutput(CommandObject command) {
        return getOutput(command.guild);
    }
}
