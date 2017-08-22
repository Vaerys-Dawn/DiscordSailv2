package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class DMHandler {

    final static Logger logger = LoggerFactory.getLogger(DMHandler.class);

    public DMHandler(CommandObject command) {
        command.message.get().getTimestamp();
        if (command.message.get().getAuthor().isBot()) {
            return;
        }
        for (String blocked : Globals.getGlobalData().getBlockedFromDMS()) {
            if (command.message.get().getAuthor().getStringID().equals(blocked)) {
                Utility.sendDM("> You have been blocked from sending DMs to S.A.I.L by the Bot Creator.", blocked);
                return;
            }
        }
        if (!command.message.get().getAuthor().getStringID().equals(Globals.creatorID)) {
            String logging = "[" + command.message.get().getAuthor().getStringID() + "] " + command.message.get().getAuthor().getName() + "#" + command.message.get().getAuthor().getDiscriminator() + ": " + command.message.get().toString();
            logger.info(logging);
            Globals.lastDmUserID = command.message.get().getAuthor().getLongID();
            if (command.message.get().getAttachments().size() > 0) {
                String attachmemts = "";
                for (IMessage.Attachment a : command.message.get().getAttachments()) {
                    attachmemts = "\n" + a.getUrl();
                }
                Utility.sendDM(logging + attachmemts, Globals.creatorID);
            } else {
                Utility.sendDM(logging, Globals.creatorID);
            }
        }
    }
}
