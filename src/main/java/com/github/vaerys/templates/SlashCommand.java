package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Arrays;

/**
 * Created by Vaerys on 13/03/2017.
 */
public abstract class SlashCommand extends Command {

    public String description(CommandObject command) {
        return "Returns with " + execute(null, null) + ".";
    }

    public String usage() {
        return null;
    }


    public String type() {
        return TYPE_SLASH;
    }


    public String channel() {
        return null;
    }


    public Permissions[] perms() {
        return new Permissions[0];
    }


    public boolean requiresArgs() {
        return false;
    }

    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }

    public String dualDescription() {
        return null;
    }

    public String dualUsage() {
        return null;
    }

    public String dualType() {
        return null;
    }

    public Permissions[] dualPerms() {
        return new Permissions[0];
    }

    @Override
    public String getCommand(CommandObject command) {
        return "/" + names[0];
    }

    @Override
    public String getCommand(CommandObject command, int i) {
        return "/" + names[i];
    }

    @Override
    public String getUsage(CommandObject command) {
        return getCommand(command);
    }

    @Override
    public String getDualUsage(CommandObject command) {
        return getCommand(command);
    }

    @Override
    public String missingArgs(CommandObject command) {
        return null;
    }

    @Override
    public boolean isCall(String args, CommandObject command) {
        SplitFirstObject object = new SplitFirstObject(args);
        return object.getFirstWord().equalsIgnoreCase(getCommand(command));
    }

    @Override
    public String getArgs(String args, CommandObject command) {
        return null;
    }

    @Override
    public XEmbedBuilder getCommandInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(getCommand(command));
        builder.withDescription(description(command));
        if (names.length != 1) {
            builder.appendField("Aliases:", Utility.listFormatter(Arrays.asList(names), true), true);
        }
        return builder;
    }

    @Override
    public String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append("\n>> Begin Error Report: " + this.getClass().getName() + " <<\n");
        if (names.length == 0 || names[0].isEmpty()) {
            response.append("> NAME IS EMPTY.\n");
            isErrored = true;
        }
        response.append(">> End Error Report <<");
        if (isErrored) {
            return response.toString();
        } else {
            return null;
        }
    }
}
