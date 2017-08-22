package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.DMCommand;
import com.github.vaerys.main.Globals;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 12/06/2017.
 */
public class QuickRespond implements DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        IUser recipient = command.client.get().getUserByID(Globals.lastDmUserID);
        if (recipient != null){
            return Respond.sendDM(args,command,recipient);
        }else {
            return "> no user to respond to.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"QuickRespond","QR","R"};
    }

    @Override
    public String description() {
        return "Responds to the last person to message the bot.";
    }

    @Override
    public String usage() {
        return "[Message]";
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
