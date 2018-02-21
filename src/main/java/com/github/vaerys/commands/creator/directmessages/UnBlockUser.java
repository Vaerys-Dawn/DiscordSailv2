package com.github.vaerys.commands.creator.directmessages;

import java.util.ListIterator;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;

public class UnBlockUser extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        GlobalData globalData = Globals.getGlobalData();
        ListIterator iterator = globalData.getBlockedFromDMS().listIterator();
        long userID = Utility.stringLong(args.split(" ")[0]);
        while (iterator.hasNext()){
            long id = (long) iterator.next();
            if (id == userID){
                iterator.remove();
                return "> User Unblocked";
            }
        }
        return "> Could not find user or invalid ID.";
    }

    protected static final String[] NAMES = new String[]{"UnBlockUser","UnBlock"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "unblocks a user";
    }

    protected static final String USAGE = "[UserID]";
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
