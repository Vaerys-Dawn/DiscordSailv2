package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.awt.*;

public class WhatsMyColour extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        Color color = command.user.getRandomColour();
        XEmbedBuilder builder = new XEmbedBuilder(color);
        String desc = "";
        String modif;
        if (isSubtype(command, names()[2]) || isSubtype(command, names()[3])) {
            desc += "Your color is : ";
            modif = "color";
        } else {
            desc += "Your colour is : ";
            modif = "colour";
        }
        desc += "**#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase() + "**";
        builder.withDescription(desc);
        builder.send(command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"WhatsMyColour", "MyColour", "WhatsMyColor", "MyColor"};
    }

    @Override
    public String description(CommandObject command) {
        return "returns the seeded colour assigned to your account" +
                "\n**How this works:**\n" +
                "Sail grabs 3 random numbers using your user ID for the seed and then converts it into a colour.\n";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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