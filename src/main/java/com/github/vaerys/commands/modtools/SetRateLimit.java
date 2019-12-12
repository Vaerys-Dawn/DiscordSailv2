package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class SetRateLimit extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            int max = Integer.parseInt(args);
            if (max <= 0) {
                return "\\> Rate Limit must be larger than 0";
            } else if (max > 10) {
                return "\\> That would be stopped by Discord's Rate Limit.";
            } else {
                command.guild.config.setRateLimit(max);
                return "\\> Guild Rate limit set to **" + max + "** messages per user every 10 seconds.";
            }
        } catch (NumberFormatException e) {
            return "\\> You need to specify a number.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetRateLimit"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the rate limit for your Guild. (Maximum Messages per 10 seconds per person.)";
    }

    @Override
    protected String usage() {
        return "[Max messages per 10 sec]";
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
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
