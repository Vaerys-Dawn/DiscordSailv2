package com.github.vaerys.commands.help;

import com.github.vaerys.masterobjects.CommandObject;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class SilentReport extends Report {

    @Override
    public String execute(String args, CommandObject command) {
        command.message.delete();
        command.user.queueDm(Report.report(args, command, true));
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"SilentReport"};
    }

    @Override
    public String description(CommandObject command) {
        return "Can be used to send a globalUser report to the server staff.\n" +
                indent + " It will also remove the message used to call the command.";
    }

    @Override
    protected String usage() {
        return new Report().usage;
    }

    @Override
    protected boolean sendTyping() {return false;}

    @Override
    public void init() {

    }
}
