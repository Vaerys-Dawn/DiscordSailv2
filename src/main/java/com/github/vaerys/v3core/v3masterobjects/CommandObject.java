package com.github.vaerys.v3core.v3masterobjects;

import com.github.vaerys.main.Globals;
import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vaerys on 29/01/2017.
 */
public class CommandObject {

    final static Logger logger = LoggerFactory.getLogger(CommandObject.class);
    //master objects
    public GuildObject guild;
    public UserObject user;
    public MessageObject message;
    public ChannelObject channel;
    public ClientObject client;


    // message MUST block
    //

    public CommandObject(Message message) {
        if (message == null) {
            throw new IllegalStateException("Message should never be null.");
        }
        if (message.getGuild() == null) {
            this.guild = new GuildObject();
        } else {
            this.guild = Globals.getGuildContent(message.getGuild().getIdLong());
        }
        this.message = new MessageObject(message, guild);
        this.channel = new ChannelObject(message.getChannel(), guild);
        this.user = new UserObject(message.getAuthor(), guild);
        this.client = new ClientObject(guild);
    }

    public CommandObject() {
        guild = new GuildObject();
        user = new UserObject(null, null);
        message = new MessageObject(null, null);
        channel = new ChannelObject(null, null);
        client = new ClientObject(null);
    }

    public CommandObject(Message message, Guild guild, TextChannel channel, IUser author) {
        if (guild == null) {
            this.guild = new GuildObject();
        } else {
            this.guild = Globals.getGuildContent(guild.getIdLong());
        }
        this.message = new MessageObject(message, this.guild);
        this.channel = new ChannelObject(channel, this.guild);
        this.user = new UserObject(author, this.guild);
        this.client = new ClientObject(this.guild);
    }

    public CommandObject(GuildObject task, TextChannel channel) {
        this.client = task.client;
        this.guild = task;
        this.channel = new ChannelObject(channel, task);
        this.message = null;
        this.user = null;
    }

    public CommandObject(GuildObject content, TextChannel channel, IUser user) {
        this.guild = content;
        this.message = null;
        this.channel = new ChannelObject(channel, guild);
        this.user = new UserObject(user, guild);
        this.client = new ClientObject(guild);
    }

    public CommandObject setAuthor(IUser author) {
        this.user = new UserObject(author, guild);
        return this;
    }

    public CommandObject setChannel(TextChannel channel) {
        this.channel = new ChannelObject(channel, guild);
        return this;
    }

    public CommandObject setGuild(Guild guild) {
        this.guild = Globals.getGuildContent(guild.getIdLong());
        this.user = new UserObject(user.get(), this.guild);
        this.client = new ClientObject(this.guild);
        return this;
    }

    public CommandObject setMessage(Message message) {
        this.message = new MessageObject(message, guild);
        return this;
    }

    public String getMessageLink() {
        return String.format("https://discordapp.com/channels/%d/%d/%d", guild.longID, channel.longID, message.longID);
    }
}
