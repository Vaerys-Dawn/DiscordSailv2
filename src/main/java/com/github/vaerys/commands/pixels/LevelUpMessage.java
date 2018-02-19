package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.TagType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class LevelUpMessage extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (args.length() > 100) {
            return "> Message to long.";
        } else {
            if (!args.contains("<level>") || !args.contains("<user>")) {
                return "> Missing <level> and/or <user> tags.";
            }
            command.guild.config.setLevelUpMessage(args);
            return "> New Level Up message set.";
        }
    }

    protected static final String[] NAMES = new String[]{"SetLvlMessage","SetLevelMessage","SetLvlMsg","SetLevelMsg"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the level up message for the server.\n" +
                "**Tags:** " + Utility.listFormatter(TagList.getNames(TagType.LEVEL), true);
    }

    protected static final String USAGE = "[Message]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
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
