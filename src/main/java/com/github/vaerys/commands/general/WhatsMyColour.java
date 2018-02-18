package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.awt.*;

public class WhatsMyColour extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        if (args != null && !args.isEmpty()) {
            user = Utility.getUser(command, args, true, false);
            if (user == null) return "> Could not find user.";
        }
        boolean notAuthor = user.longID != command.user.longID;
        Color color = user.getRandomColour();
        XEmbedBuilder builder = new XEmbedBuilder(color);
        String desc = notAuthor ? "**" + user.displayName + "'s** " : "Your ";
        if (isSubtype(command, names()[2]) || isSubtype(command, names()[3])) {
            desc += "color is : ";
        } else {
            desc += "colour is : ";
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
        return "(@User)";
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