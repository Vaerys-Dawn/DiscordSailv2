package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
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
        IChannel ownerDm = command.client.creator.getOrCreatePMChannel();
        command.message.get().getTimestamp();
        if (command.message.get().getAuthor().isBot()) {
            return;
        }
        for (long blocked : Globals.getGlobalData().getBlockedFromDMS()) {
            if (command.user.longID == blocked) {
                Utility.sendDM("> You have been blocked from sending DMs to S.A.I.L by the Bot Creator.", blocked);
                return;
            }
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
                Utility.sendMessage(logging + attachmemts, ownerDm);
            } else {
                Utility.sendMessage(logging, ownerDm);
            }
            Utility.sendMessage("> Thank you for your message.", channel);
        }
    }
}
