package com.github.vaerys.commands.general;

import java.awt.Color;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

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
        if (isSubtype(command, names[2]) || isSubtype(command, names[3])) {
            desc += "color is : ";
        } else {
            desc += "colour is : ";
        }
        desc += "**#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase() + "**";
        builder.withDescription(desc);
        builder.send(command.channel);
        return null;
    }

    protected static final String[] NAMES = new String[]{"WhatsMyColour", "MyColour", "WhatsMyColor", "MyColor"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "returns the seeded colour assigned to your account" +
                "\n**How this works:**\n" +
                "Sail grabs 3 random numbers using your user ID for the seed and then converts it into a colour.\n";
    }

    protected static final String USAGE = "(@User)";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
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