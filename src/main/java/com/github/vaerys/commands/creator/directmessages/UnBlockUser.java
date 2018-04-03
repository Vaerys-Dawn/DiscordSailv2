package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.templates.DMCommand;

import java.util.ListIterator;

public class UnBlockUser extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        GlobalData globalData = Globals.getGlobalData();
        ListIterator iterator = globalData.getBlockedFromDMS().listIterator();
        long userID = Utility.stringLong(args.split(" ")[0]);
        while (iterator.hasNext()) {
            long id = (long) iterator.next();
            if (id == userID) {
                iterator.remove();
                return "> User Unblocked";
            }
        }
        return "> Could not find user or invalid ID.";
    }

    @Override
    protected String[] names() {
        return new String[]{"UnBlockUser", "UnBlock"};
    }

    @Override
    public String description(CommandObject command) {
        return "unblocks a user";
    }

    @Override
    protected String usage() {
        return "[UserID]";
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
