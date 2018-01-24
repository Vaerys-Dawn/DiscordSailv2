package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.XEmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.awt.*;
import java.util.List;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class DMHandler {

    final static Logger logger = LoggerFactory.getLogger(DMHandler.class);

    public DMHandler(CommandObject command) {
        IChannel channel = command.user.get().getOrCreatePMChannel();
        IChannel ownerDm = command.client.creator.getDmChannel();
        if (!command.client.get().isReady()) return;
        if (command.message.get().getAuthor().isBot()) return;

        if (command.user.isBlockedFromDms()) {
            command.user.sendDm("> You have been blocked from sending DMs to S.A.I.L by the Bot Creator.");
            return;
        }

        if (command.user.longID != Globals.creatorID) {
            final String[] logging = {"[" + command.message.get().getAuthor().getLongID() + "] " + command.message.get().getAuthor().getName() + "#" + command.message.get().getAuthor().getDiscriminator() + ": " + command.message.get().toString()};
            if (command.message.getAttachments().size() != 0) {
                command.message.getAttachments().forEach(attachment -> logging[0] += "\n" + attachment.getUrl());
            }
            logger.info(logging[0]);
            Globals.lastDmUserID = command.user.longID;
            if (command.message.getContent().equalsIgnoreCase("> Thank you for your message.")) {
                command.user.sendDm("> That's what I just said, you don't have to repeat it.");
                return;
            }
            if (command.message.getContent().equalsIgnoreCase("> That's what I just said, you don't have to repeat it.")) {
                command.user.sendDm("> Okay, do you like repeating the things bots say?");
                return;
            }
            if (command.message.getContent().equalsIgnoreCase("> Okay, do you like repeating the things bots say?")) {
                command.user.sendDm("> Alright this was funny to begin with but now its gotten too far. next time you do that, im going to block you.");
                return;
            }
            if (command.message.getContent().equalsIgnoreCase("> Alright this was funny to begin with but now its gotten too far. next time you do that, im going to block you.")) {
                command.user.sendDm("> I warned you...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Globals.getGlobalData().getBlockedFromDMS().add(command.user.longID);
                command.user.sendDm("> You were blocked.");
                RequestHandler.sendMessage("> " + command.user.username + " was blocked for being a smart ass.", ownerDm);
                return;
            }

            for (GuildObject guild : Globals.getGuilds()) {
                if (command.message.getContent().startsWith(guild.config.getPrefixCommand())) {
                    command.user.sendDm("> Hey there, looks like you're trying to use a command only available on a guild. unfortunately I can't run guild only commands in my Direct messages.");
                    return;
                } else if (command.message.getContent().startsWith(guild.config.getPrefixCC())) {
                    command.user.sendDm("> Hey there, looks like you're trying to run a custom command im my direct messages, unfortunately they don't work here.");
                    return;
                }
            }
            String content = command.message.getContent();
            XEmbedBuilder builder = new XEmbedBuilder();
            Color color = command.user.getRandomColour();
            builder.withColor(color);
            builder.withAuthorName(command.user.username + " | " + command.user.longID);
            builder.withAuthorIcon(command.user.getAvatarURL());
            int attatchmentStart = 0;
            List<IMessage.Attachment> attachmentList = command.message.getAttachments();
            if (attachmentList.size() != 0 && Utility.isImageLink(attachmentList.get(0).getUrl())) {
                attatchmentStart++;
                builder.withImage(attachmentList.get(0).getUrl());
            }
            if (attachmentList.size() > attatchmentStart) {
                for (int i = attatchmentStart; i < attachmentList.size(); i++) {
                    if (content.length() != 0) {
                        content += "\n";
                    }
                    content += attachmentList.get(i).getUrl();
                }
            }
            builder.withDescription(content);
            builder.send(ownerDm);
            RequestHandler.sendMessage("> Thank you for your message.", channel);

//            if (command.)
//
//                if (command.message.get().getAttachments().size() > 0) {
//                    String attachmemts = "";
//                    for (IMessage.Attachment a : command.message.get().getAttachments()) {
//                        attachmemts = "\n" + a.getUrl();
//                    }
//                    RequestHandler.sendMessage(logging + attachmemts, ownerDm);
//                } else {
//                    RequestHandler.sendMessage(logging, ownerDm);
//                }
//            RequestHandler.sendMessage("> Thank you for your message.", channel);
        }
    }
}
