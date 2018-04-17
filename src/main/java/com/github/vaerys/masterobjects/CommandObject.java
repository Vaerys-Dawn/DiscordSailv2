package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;


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


    public CommandObject(IMessage message) {
        if (message == null) {
            throw new IllegalStateException("Message should never be null.");
        }
        if (message.getGuild() == null) {
            this.guild = new GuildObject();
        } else {
            this.guild = Globals.getGuildContent(message.getGuild().getLongID());
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

    public CommandObject(IMessage message, IGuild guild, IChannel channel, IUser author) {
        if (guild == null) {
            this.guild = new GuildObject();
        } else {
            this.guild = Globals.getGuildContent(guild.getLongID());
        }
        this.message = new MessageObject(message, this.guild);
        this.channel = new ChannelObject(channel, this.guild);
        this.user = new UserObject(author, this.guild);
        this.client = new ClientObject(this.guild);
    }

    public CommandObject(GuildObject task, IChannel channel) {
        this.client = task.client;
        this.guild = task;
        this.channel = new ChannelObject(channel, task);
        this.message = null;
        this.user = null;
    }

    public CommandObject(GuildObject content, IChannel channel, IUser user) {
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

    public CommandObject setChannel(IChannel channel) {
        this.channel = new ChannelObject(channel, guild);
        return this;
    }

    public CommandObject setGuild(IGuild guild) {
        this.guild = Globals.getGuildContent(guild.getLongID());
        this.user = new UserObject(user.get(), this.guild);
        this.client = new ClientObject(this.guild);
        return this;
    }

    public CommandObject setMessage(IMessage message) {
        this.message = new MessageObject(message, guild);
        return this;
    }
}
