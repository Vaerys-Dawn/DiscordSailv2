package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 15/07/2017.
 */
public class TestDM extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
//        for (IChannel c : command.client.getGuildByID(176434793674833920L).getChannels()) {
//            if (("#" + c.getNames()).equalsIgnoreCase(args)) {
//                return c.getNames();
//            }
//        }
//        return "channel could not be found";
//        return command.guild.getToggles().getLongID() + "";
        return "> You've done your testing";
    }

    @Override
    protected String[] names() {
        return new String[]{"Test"};
    }

    @Override
    public String description(CommandObject command) {
        return "Is a test";
    }

    @Override
    protected String usage() {
        return "[args]";
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
