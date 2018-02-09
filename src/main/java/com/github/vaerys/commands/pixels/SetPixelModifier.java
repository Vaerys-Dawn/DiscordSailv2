package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
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

    @Override
    public String[] names() {
        return new String[]{"SetPixelMultiplier"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the modifier that pixels are calculated with.";
    }

    @Override
    public String usage() {
        return "[Positive Float]";
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
