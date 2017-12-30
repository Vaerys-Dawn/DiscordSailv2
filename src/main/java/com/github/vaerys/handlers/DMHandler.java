package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class DMHandler {

    final static Logger logger = LoggerFactory.getLogger(DMHandler.class);

    public DMHandler(CommandObject command) {
        IChannel channel = command.user.get().getOrCreatePMChannel();
        IChannel ownerDm = command.client.creator.getDmChannel();
        command.message.get().getTimestamp();
        if (command.message.get().getAuthor().isBot()) return;

        if (command.user.isBlockedFromDms()) {
            command.user.sendDm("> You have been blocked from sending DMs to S.A.I.L by the Bot Creator.");
            return;
        }

        if (command.user.longID != Globals.creatorID) {
            String logging = "[" + command.message.get().getAuthor().getLongID() + "] " + command.message.get().getAuthor().getName() + "#" + command.message.get().getAuthor().getDiscriminator() + ": " + command.message.get().toString();
            logger.info(logging);
            Globals.lastDmUserID = command.user.longID;
            if (command.message.get().getAttachments().size() > 0) {
                String attachmemts = "";
                for (IMessage.Attachment a : command.message.get().getAttachments()) {
                    attachmemts = "\n" + a.getUrl();
                }
                RequestHandler.sendMessage(logging + attachmemts, ownerDm);
            } else {
                RequestHandler.sendMessage(logging, ownerDm);
            }
            RequestHandler.sendMessage("> Thank you for your message.", channel);
        }
    }
}
