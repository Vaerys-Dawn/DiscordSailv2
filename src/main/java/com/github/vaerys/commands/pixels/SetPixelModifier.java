package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class SetPixelModifier extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            float multiplier = Float.parseFloat(args);
            if (multiplier <= 0) {
                return "> must be a positive number";
            }
            if (multiplier > 5) {
                return "> Cannot set a multiplier over 5x";
            }
            command.guild.config.xpModifier = multiplier;
            return "> Pixel Multiplier is now set to **x" + multiplier + "**.";
        } catch (NumberFormatException e) {
            return "> Not a valid Number.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetPixelMultiplier", "SetPixelModifier"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the modifier that pixels are calculated with.";
    }

    @Override
    protected String usage() {
        return "[Positive Float]";
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
        return true;
    }

    @Override
    public void init() {

    }
}
