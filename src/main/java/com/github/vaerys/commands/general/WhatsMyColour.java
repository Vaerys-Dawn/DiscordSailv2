package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.awt.*;

public class WhatsMyColour extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        if (args != null && !args.isEmpty()) {
            user = Utility.getUser(command, args, true, false);
            if (user == null) return "\\> Could not find user.";
        }
        boolean notAuthor = user.longID != command.user.longID;
        Color color = user.getRandomColour();
        XEmbedBuilder builder = new XEmbedBuilder(color);
        String desc = notAuthor ? "**" + user.displayName + "'s** " : "Your ";
        if (isAlias(command, names[2]) || isAlias(command, names[3])) {
            desc += "color is : ";
        } else {
            desc += "colour is : ";
        }
        desc += "**#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase() + "**";
        builder.setDescription(desc);
        builder.send(command.channel);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"WhatsMyColour", "MyColour", "WhatsMyColor", "MyColor"};
    }

    @Override
    public String description(CommandObject command) {
        return "returns the seeded colour assigned to your account" +
                "\n**How this works:**\n" +
                "Sail grabs 3 random numbers using your user ID for the seed and then converts it into a colour.\n";
    }

    @Override
    protected String usage() {
        return "(@User)";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
