package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
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

    @Override
    protected String[] names() {
        return new String[]{"SetLvlMessage","SetLevelMessage","SetLvlMsg","SetLevelMsg"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the level up message for the server.\n" +
                "**Tags:** " + Utility.listFormatter(TagList.getNames(TagType.LEVEL), true);
    }

    @Override
    protected String usage() {
        return "[Message]";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
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
        return false;
    }

    @Override
    public void init() {

    }
}
