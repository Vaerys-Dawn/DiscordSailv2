package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class Respond extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject response = new SplitFirstObject(args);
        IUser recipient = command.client.get().getUserByID(Utility.stringLong(response.getFirstWord()));
        return sendDM(response.getRest(), command, recipient, command.user.username + ": ");
    }

    public static String sendDM(String args, CommandObject command, IUser recipient, String prefix) {
        if (recipient == null) {
            return "> Could Not Send Response, UserID is invalid.";
        }
        if (args == null) {
            return "> Could Not Send Response, Contents cannot be empty.";
        }
        IChannel channel = recipient.getOrCreatePMChannel();
        if (command.message.getAttachments().size() != 0) {
            for (IMessage.Attachment a : command.message.getAttachments()) {
                args += "\n" + a.getUrl();
            }
        }
        if (RequestHandler.sendMessage(prefix + args, channel).get() == null) {
            return "> An Error occurred while attempting to run this command.";
        } else {
            return "> Message Sent.";
        }
    }

    protected static final String[] NAMES = new String[]{"Respond"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Sends a response to a user.";
    }

    protected static final String USAGE = "[userID] [Contents]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public void init() {

    }
}
