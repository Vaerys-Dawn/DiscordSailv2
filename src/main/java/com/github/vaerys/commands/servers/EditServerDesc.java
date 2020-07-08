package com.github.vaerys.commands.servers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class EditServerDesc extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        String desc;
        if (splitArgs.size() < 2) {
            return "\\> Cannot edit server description, missing arguments.";
        }
        desc = args.replaceFirst(Pattern.quote(splitArgs.get(0) + " "), "");
        return command.guild.servers.editServerDesc(command.user.get().getIdLong(), splitArgs.get(0), desc, command.guild.get());
    }

    @Override
    protected String[] names() {
        return new String[]{"EditServerDesc"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit your server description.";
    }

    @Override
    protected String usage() {
        return "[Server Name] [Description]";
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
    protected Permission[] perms() {
        return new Permission[0];
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
