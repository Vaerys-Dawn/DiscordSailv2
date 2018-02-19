package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class SetPixelModifier extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        try {
            float multiplier = Float.parseFloat(args);
            if (multiplier <= 0){
                return "> must be a positive number";
            }
            if (multiplier > 5){
                return "> Cannot set a multiplier over 5x";
            }
            command.guild.config.xpModifier = multiplier;
            return "> Pixel Multiplier is now set to **x" + multiplier + "**.";
        }catch (NumberFormatException e){
            return "> Not a valid Number.";
        }
    }

    protected static final String[] NAMES = new String[]{"SetPixelMultiplier","SetPixelModifier"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the modifier that pixels are calculated with.";
    }

    protected static final String USAGE = "[Positive Float]";
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

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
