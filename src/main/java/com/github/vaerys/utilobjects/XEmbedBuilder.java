package com.github.vaerys.utilobjects;

import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class XEmbedBuilder extends EmbedBuilder {

    public XEmbedBuilder() {
    }

    public XEmbedBuilder(UserObject user) {
        setColor(user.color);
    }

    public XEmbedBuilder(Color color) {
        setColor(color);
    }

    public XEmbedBuilder(CommandObject command) {
        setColor(command.client.bot.color);
    }

    public void queue(TextChannel channel) {
        channel.sendMessage(this.build()).queue();
    }

    public void queue(ChannelObject channel) {
        channel.queueMessage(this.build());
    }

    public void queue(CommandObject command) {
        command.channel.queueMessage(this.build());
    }

    public void queue(String s, TextChannel channel) {
        channel.sendMessage(s).embed(this.build()).queue();
    }

    public void queue(String s, ChannelObject channel) {
        channel.queueMessage(s,this.build());
    }

    public void queue(String s, CommandObject command) {
        command.channel.queueMessage(s,this.build());
    }

    public Message send(TextChannel channel) {
        return channel.sendMessage(this.build()).complete();
    }

    public Message send(ChannelObject channel) {
        return channel.sendMessage(this.build());
    }

    public Message send(CommandObject command) {
        return command.channel.sendMessage(this.build());
    }

    public Message send(String s, TextChannel channel) {
        return channel.sendMessage(s).embed(this.build()).complete();
    }

    public Message send(String s, ChannelObject channel) {
        return channel.sendMessage(s,this.build());
    }

    public Message send(String s, CommandObject command) {
        return command.channel.sendMessage(s, this.build());
    }

    @Override
    public EmbedBuilder setTitle(String title) {
        return super.setTitle(Utility.removeMentions(title));
    }

    public EmbedBuilder setDescription(String desc) {
        return super.setDescription(Utility.removeMentions(desc));
    }


    @Override
    public EmbedBuilder addField(String title, String content, boolean inline) {
        return super.addField(Utility.removeMentions(title), Utility.removeMentions(content), inline);
    }

    @Override
    public EmbedBuilder setFooter(String footerText) {
        return super.setFooter(Utility.removeMentions(footerText));
    }

    @Override
    public EmbedBuilder setFooter(String footerText, String footerImageURL) {
        return super.setFooter(Utility.removeMentions(footerText), footerImageURL);
    }

    @Override
    public EmbedBuilder setAuthor(String authorName) {
        return super.setAuthor(Utility.removeMentions(authorName));
    }

    @Override
    public EmbedBuilder setAuthor(String authorName, String avatarImageURL) {
        return super.setAuthor(Utility.removeMentions(authorName), avatarImageURL);
    }

}
