package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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
    public UserObject botUser;


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
        this.channel = new ChannelObject(message.getTextChannel(), guild);
        this.user = new UserObject(message.getMember(), guild);
        this.client = Client.getClientObject();
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject(Message message, Guild guild, TextChannel channel, Member author) {
        if (guild == null) {
            this.guild = new GuildObject();
        } else {
            this.guild = Globals.getGuildContent(guild.getIdLong());
        }
        this.message = new MessageObject(message, this.guild);
        this.channel = new ChannelObject(channel, this.guild);
        this.user = new UserObject(author, this.guild);
        this.client = Client.getClientObject();
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject(GuildObject task, TextChannel channel) {
        this.client = Client.getClientObject();
        this.guild = task;
        this.channel = new ChannelObject(channel, task);
        this.message = null;
        this.user = null;
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject(GuildObject content, TextChannel channel, Member user) {
        this.guild = content;
        this.message = null;
        this.channel = new ChannelObject(channel, guild);
        this.user = new UserObject(user, guild);
        this.client = Client.getClientObject();
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject setAuthor(Member author) {
        this.user = new UserObject(author, guild);
        return this;
    }

    public CommandObject setChannel(TextChannel channel) {
        this.channel = new ChannelObject(channel, guild);
        return this;
    }

    public CommandObject setGuild(Guild guild) {
        this.guild = Globals.getGuildContent(guild.getIdLong());
        this.user = new UserObject(user.getMember(), this.guild);
        this.client = Client.getClientObject();
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
