package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.DMCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class Respond extends DMCommand {

    public static String sendDM(String args, DmCommandObject command, User recipient, String prefix) {
        if (recipient == null) {
            return "\\> Could Not Send Response, UserID is invalid.";
        }
        if (args == null) {
            return "\\> Could Not Send Response, Contents cannot be empty.";
        }
        PrivateChannel channel = recipient.openPrivateChannel().complete();
        StringBuilder builder = new StringBuilder(prefix);
        builder.append(args);
        if (command.message.getAttachments().size() != 0) {
            for (Message.Attachment a : command.message.getAttachments()) {
                builder.append("\n").append(a.getUrl());
            }
        }
        if (channel.sendMessage(builder).complete() == null) {
            return "\\> An Error occurred while attempting to run this command.";
        } else {
            return "\\> Message Sent.";
        }
    }

    @Override
    public String executeDm(String args, DmCommandObject command) {
        SplitFirstObject response = new SplitFirstObject(args);
        User recipient = command.client.get().getUserById(Utility.stringLong(response.getFirstWord()));
        return sendDM(response.getRest(), command, recipient, command.globalUser.username + ": ");
    }

    @Override
    protected String[] names() {
        return new String[]{"Respond"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends a response to a user.";
    }

    @Override
    protected String usage() {
        return "[userID] [Contents]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    public void init() {
        // do nothing
    }
}
