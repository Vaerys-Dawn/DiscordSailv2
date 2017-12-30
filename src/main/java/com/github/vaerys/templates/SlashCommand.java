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
public interface SlashCommand extends Command {

    @Override
    default String description(CommandObject command) {
        return "Returns with " + execute(null, null) + ".";
    }

    @Override
    default String usage() {
        return null;
    }

    @Override
    default String type() {
        return TYPE_SLASH;
    }

    @Override
    default String channel() {
        return null;
    }

    @Override
    default Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    default boolean requiresArgs() {
        return false;
    }

    @Override
    default boolean doAdminLogging() {
        return false;
    }

    @Override
    default String dualDescription() {
        return null;
    }

    @Override
    default String dualUsage() {
        return null;
    }

    @Override
    default String dualType() {
        return null;
    }

    @Override
    default Permissions[] dualPerms() {
        return new Permissions[0];
    }

    @Override
    default String getCommand(CommandObject command) {
        return "/" + names()[0];
    }

    @Override
    default String getUsage(CommandObject command) {
        return getCommand(command);
    }

    @Override
    default String getDualUsage(CommandObject command) {
        return getCommand(command);
    }

    @Override
    default String missingArgs(CommandObject command) {
        return null;
    }

    @Override
    default boolean isCall(String args, CommandObject command) {
        SplitFirstObject object = new SplitFirstObject(args);
        return object.getFirstWord().equalsIgnoreCase(getCommand(command));
    }

    @Override
    default String getArgs(String args, CommandObject command) {
        return null;
    }

    @Override
    default XEmbedBuilder getCommandInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(getCommand(command));
        builder.withDescription(description(command));
        if (names().length != 1) {
            builder.appendField("Aliases:", Utility.listFormatter(Arrays.asList(names()),true),true);
        }
        return builder;
    }

    @Override
    default String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append("\n>> Begin Error Report: " + this.getClass().getName() + " <<\n");
        if (names().length == 0 || names()[0].isEmpty()) {
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
