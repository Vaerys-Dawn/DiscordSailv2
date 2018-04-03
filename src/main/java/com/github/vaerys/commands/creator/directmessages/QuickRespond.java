package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 12/06/2017.
 */
public class QuickRespond extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        IUser recipient = command.client.get().getUserByID(Globals.lastDmUserID);
        if (recipient != null) {
            return Respond.sendDM(args, command, recipient, command.user.username + ": ");
        } else {
            return "> no user to respond to.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"QuickRespond", "QR", "R"};
    }

    @Override
    public String description(CommandObject command) {
        return "Responds to the last person to message the bot.";
    }

    @Override
    protected String usage() {
        return "[Message]";
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

    }
}
