package com.github.vaerys.commands.servers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class EditServerDesc extends Command{
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        String desc;
        if (splitArgs.size() < 2) {
            return "> Cannot edit server description, missing arguments.";
        }
        desc = args.replaceFirst(Pattern.quote(splitArgs.get(0) + " "), "");
        return command.guild.servers.editServerDesc(command.user.get().getLongID(), splitArgs.get(0), desc, command.guild.get());
    }

    protected static final String[] NAMES = new String[]{"EditServerDesc"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit your server description.";
    }

    protected static final String USAGE = "[Server Name] [Description]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.SERVERS;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.SERVERS;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
