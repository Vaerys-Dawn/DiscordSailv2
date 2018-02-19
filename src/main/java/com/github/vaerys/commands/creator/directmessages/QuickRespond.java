package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 12/06/2017.
 */
public class QuickRespond extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        IUser recipient = command.client.get().getUserByID(Globals.lastDmUserID);
        if (recipient != null){
            return Respond.sendDM(args,command,recipient,command.user.username + ": ");
        }else {
            return "> no user to respond to.";
        }
    }

    protected static final String[] NAMES = new String[]{"QuickRespond","QR","R"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Responds to the last person to message the bot.";
    }

    protected static final String USAGE = "[Message]";
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
