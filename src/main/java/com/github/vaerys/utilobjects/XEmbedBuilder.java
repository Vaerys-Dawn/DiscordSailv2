package com.github.vaerys.utilobjects;

import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

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

    public MessageAction send(TextChannel channel) {
        return channel.sendMessage(this.build());
    }

    public MessageAction send(ChannelObject channel) {
        return channel.get().sendMessage(this.build());
    }

    public MessageAction send(CommandObject command) {
        return command.channel.get().sendMessage(this.build());
    }

    public MessageAction send(String s, TextChannel channel) {
        return channel.sendMessage(s).embed(this.build());
    }

    public MessageAction send(String s, ChannelObject channel) {
        return channel.get().sendMessage(s).embed(this.build());
    }

    public MessageAction send(String s, CommandObject command) {
        return command.channel.get().sendMessage(s).embed(this.build());
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
    public EmbedBuilder setAuthor(String authorName) {
        return super.setAuthor(Utility.removeMentions(authorName));
    }

}
