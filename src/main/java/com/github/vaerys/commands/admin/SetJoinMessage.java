package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class SetJoinMessage implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        command.guild.config.setJoinMessage(args);
        return "> New Join Message set.";
    }

    @Override
    public String[] names() {
        return new String[]{"SetJoinMessage"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for the setting of the message that shows when a user joins the server.\n" +
                "**Available tags**\n" +
                "<server> = Server Name\n" +
                "<user> = User's Name";
    }

    @Override
    public String usage() {
        return "[Message]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
