package com.github.vaerys.commands.servers;

import java.util.ArrayList;
import java.util.Arrays;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class EditServerName extends Command{
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        if (splitArgs.size() < 2) {
            return "> Cannot Edit Server Name as no new name was specified.";
        }
        return command.guild.servers.editServerName(command.user.longID, splitArgs.get(0), splitArgs.get(1), command.guild.get());
    }

    @Override
    protected String[] names() {
        return new String[]{"EditServerName"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit your server name.";
    }

    @Override
    protected String usage() {
        return "[Server Name] [New Server Name]";
    }

    @Override
    protected SAILType type() {
        return SAILType.SERVERS;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.SERVERS;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
