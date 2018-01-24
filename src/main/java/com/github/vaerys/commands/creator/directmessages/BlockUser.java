package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class BlockUser implements DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        long userId = Globals.lastDmUserID;
        if (args != null && !args.isEmpty()) {
            userId = Utility.stringLong(args.split("")[0]);
        }
        GlobalData globalData = Globals.getGlobalData();
        if (userId == Globals.creatorID) {
            return "> What are you doing. Don't try to block yourself.";
        }
        if (Client.getClient().fetchUser(userId) != null){
            globalData.blockUserFromDMS(userId);
            return "> User was blocked";
        }
        return "> Could not find a valid userID";
    }

    @Override
    public String[] names() {
        return new String[]{"BlockUser"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows the author to block a user from sending them DMs.";
    }

    @Override
    public String usage() {
        return "(User ID)";
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
