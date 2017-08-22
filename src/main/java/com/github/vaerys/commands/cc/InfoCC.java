package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class InfoCC implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (c.getName().equalsIgnoreCase(args)) {
                StringBuilder builder = new StringBuilder();
                IUser author = Globals.getClient().getUserByID(c.getUserID());
                XEmbedBuilder embedBuilder = new XEmbedBuilder();
                embedBuilder.withColor(command.client.color);
                String title = "> Here is the information for command: **" + c.getName() + "**\n";
                builder.append("Creator: **@" + author.getName() + "#" + author.getDiscriminator() + "**\n");
                builder.append("Time Run: **" + c.getTimesRun() + "**\n");
                builder.append("Is Locked: **" + c.isLocked() + "**\n");
                builder.append("Is ShitPost: **" + c.isShitPost() + "**");
                embedBuilder.appendField(title,builder.toString(),false);
                Utility.sendEmbedMessage("",embedBuilder,command.channel.get());
                return null;
            }
        }
        return Constants.ERROR_CC_NOT_FOUND;
    }

    @Override
    public String[] names() {
        return new String[]{"CCInfo", "InfoCC"};
    }

    @Override
    public String description() {
        return "Gives you a bit of information about a custom command.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
    }

    @Override
    public String type() {
        return TYPE_CC;
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
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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
