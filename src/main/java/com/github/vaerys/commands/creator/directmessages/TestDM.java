package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 15/07/2017.
 */
public class TestDM extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
//        for (IChannel c : command.client.getGuildByID(176434793674833920L).getChannels()) {
//            if (("#" + c.getName()).equalsIgnoreCase(args)) {
//                return c.getName();
//            }
//        }
//        return "channel could not be found";
//        return command.guild.get().getLongID() + "";
        return "> You've done your testing";
    }

    protected static final String[] NAMES = new String[]{"Test"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Is a test";
    }

    protected static final String USAGE = "[args]";
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
