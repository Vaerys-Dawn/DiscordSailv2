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
public class CommandObject{

    final static Logger logger = LoggerFactory.getLogger(CommandObject.class);
    //master objects
    public GuildObject guild;
    public UserObject user;
    public GuildChannelObject guildChannel;
    public MessageObject message;
    public ClientObject client;
    public UserObject botUser;

    public CommandObject(Message message, Guild guild) {
        this.client = Client.getClientObject();
        this.message = new MessageObject(message);
        this.guild = Globals.getGuildContent(guild.getIdLong());
        this.guildChannel = new GuildChannelObject(message.getTextChannel(), this.guild);
        this.user = new UserObject(message.getMember(), this.guild);
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject(GuildObject task, TextChannel channel) {
        this.client = Client.getClientObject();
        this.guild = task;
        this.guildChannel = new GuildChannelObject(channel, this.guild);
        this.user = null;
        this.message = null;
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject(GuildObject content, TextChannel channel, Member user) {
        this.client = Client.getClientObject();
        this.guild = content;
        this.message = null;
        this.guildChannel = new GuildChannelObject(channel, this.guild);
        this.user = new UserObject(user, guild);
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject(DmCommandObject command, Guild guild) {
        this.client = Client.getClientObject();
        this.message = command.message;
        this.guild = Globals.getGuildContent(guild.getIdLong());
        this.guildChannel = new GuildChannelObject(this.guild.get().getDefaultChannel(), this.guild);
        this.user = new UserObject(command.globalUser, this.guild);
        this.botUser = new UserObject(client.bot, this.guild);
    }

    public CommandObject setAuthor(Member author) {
        this.user = new UserObject(author, guild);
        return this;
    }

    public CommandObject setGuildChannel(TextChannel guildChannel) {
        this.guildChannel = new GuildChannelObject(guildChannel, this.guild);
        return this;
    }

    public CommandObject setGuild(Guild guild) {
        this.guild = Globals.getGuildContent(guild.getIdLong());
        this.user = new UserObject(user.getMember(), this.guild);
        this.client = Client.getClientObject();
        return this;
    }

    public CommandObject setMessage(Message message) {
        this.message = new MessageObject(message);
        return this;
    }

    public String getMessageLink() {
        return String.format("https://discordapp.com/channels/%d/%d/%d", guild.longID, guildChannel.longID, message.longID);
    }
}
