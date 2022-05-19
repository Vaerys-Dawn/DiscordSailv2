package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class BlockUser extends DMCommand {

    @Override
    public String executeDm(String args, DmCommandObject command) {
        long userId = Globals.lastDmUserID;
        if (args != null && !args.isEmpty()) {
            userId = Utility.stringLong(args.split(" ")[0]);
        }
        GlobalData globalData = Globals.getGlobalData();
        if (userId == Globals.creatorID) {
            return "\\> What are you doing. Don't try to block yourself.";
        }
        if (command.client.get().getUserById(userId) != null) {
            globalData.blockUserFromDMS(userId);
            return "\\> User was blocked";
        }
        return "\\> Could not find a valid userID";
    }

    @Override
    protected String[] names() {
        return new String[]{"BlockUser"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows the author to block a user from sending them DMs.";
    }

    @Override
    protected String usage() {
        return "(User ID)";
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
