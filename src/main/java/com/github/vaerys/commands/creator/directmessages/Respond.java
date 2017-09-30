package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.DMCommand;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class Respond implements DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject response = new SplitFirstObject(args);
        IUser recipient = command.client.get().getUserByID(Utility.stringLong(response.getFirstWord()));
        return sendDM(response.getRest(), command, recipient);
    }

    public static String sendDM(String args, CommandObject command, IUser recipient) {
        if (recipient == null) {
            return "> Could Not Send Response, UserID is invalid.";
        }
        if (args == null) {
            return "> Could Not Send Response, Contents cannot be empty.";
        }
        IChannel channel = recipient.getOrCreatePMChannel();
        if (Utility.sendMessage(command.user.username + ": " + args, channel).get() == null) {
            return "> An Error occurred while attempting to run this command.";
        } else {
            return "> Message Sent.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Respond"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends a response to a user.";
    }

    @Override
    public String usage() {
        return "[userID] [Contents]";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}
