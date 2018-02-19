package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class SetRateLimit extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            int max = Integer.parseInt(args);
            if (max <= 0){
                return "> Rate Limit must be larger than 0";
            }else if (max > 10){
                return "> That would be stopped by Discord's Rate Limit.";
            }else{
                command.guild.config.setRateLimit(max);
                return "> Guild Rate limit set to **" + max + "** messages per user every 10 seconds.";
            }
        }catch (NumberFormatException e){
            return "> You need to specify a number.";
        }
}

    protected static final String[] NAMES = new String[]{"SetRateLimit"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the rate limit for your Guild. (Maximum Messages per 10 seconds per person.)";
    }

    protected static final String USAGE = "[Max messages per 10 sec]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
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

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
