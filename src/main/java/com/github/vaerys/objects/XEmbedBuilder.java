package com.github.vaerys.objects;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.UserObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class XEmbedBuilder extends EmbedBuilder {

    public XEmbedBuilder() {
    }

    public XEmbedBuilder(UserObject user) {
        withColor(user.color);
    }

    public XEmbedBuilder(CommandObject command) {
        withColor(command.client.bot.color);
    }

    public RequestBuffer.RequestFuture<IMessage> send(IChannel channel) {
        return RequestHandler.sendEmbedMessage("", this, channel);
    }

    public RequestBuffer.RequestFuture<IMessage> send(ChannelObject channel) {
        return RequestHandler.sendEmbedMessage("", this, channel.get());
    }

    @Override
    public EmbedBuilder withTitle(String title) {
        return super.withTitle(Utility.removeMentions(title));
    }

    @Override
    public EmbedBuilder withDesc(String desc) {
        return super.withDesc(Utility.removeMentions(desc));
    }

    @Override
    public EmbedBuilder withDescription(String desc) {
        return super.withDescription(Utility.removeMentions(desc));
    }

    @Override
    public EmbedBuilder appendField(String title, String content, boolean inline) {
        return super.appendField(Utility.removeMentions(title), Utility.removeMentions(content), inline);
    }

    @Override
    public EmbedBuilder withFooterText(String footerText) {
        return super.withFooterText(Utility.removeMentions(footerText));
    }

    @Override
    public EmbedBuilder withAuthorName(String authorName) {
        return super.withAuthorName(Utility.removeMentions(authorName));
    }
}
