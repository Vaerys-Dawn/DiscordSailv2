package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.events.TimedEvent;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Hello extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String message = "\\> Hello <globalUser>.";
        TimedEvent event = Globals.getCurrentEvent();
        if (event != null && event.getHelloMessage() != null) message = event.getHelloMessage();
        if (command.user.longID == 153159020528533505L) {
            message = message.replace("<globalUser>", "Mother");
        }
        return message.replace("<globalUser>", command.user.displayName);
    }

    @Override
    protected String[] names() {
        return new String[]{"Hello", "Hi", "Greetings"};
    }

    @Override
    public String description(CommandObject command) {
        return "Says Hello.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
