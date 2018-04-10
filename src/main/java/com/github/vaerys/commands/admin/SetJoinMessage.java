package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class SetJoinMessage extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.guild.config.setJoinMessage(args);
        return "> New Join Message set.";
    }

    @Override
    protected String[] names() {
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
    protected String usage() {
        return "[Message]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
