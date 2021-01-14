package com.github.vaerys.templates;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;

/**
 * Created by Vaerys on 13/03/2017.
 */
public abstract class SlashCommand extends Command {

    @Override
    public String description(CommandObject command) {
        return "Returns with " + execute(null, null) + ".";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.SLASH;
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
    public String missingArgs(CommandObject command) {
        return null;
    }

    @Override
    public boolean isCall(String args, CommandObject command) {
        SplitFirstObject object = new SplitFirstObject(args);
        return object.getFirstWord().equalsIgnoreCase(getCommand(command));
    }

    @Override
    public String getArgs(String args) {
        return null;
    }

    @Override
    public XEmbedBuilder getCommandInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle(getCommand(command));
        builder.setDescription(description(command));
        if (names.length != 1) {
            builder.addField("Aliases:", Utility.listFormatter(Arrays.asList(names), true), true);
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

    @Override
    public boolean isName(String args, CommandObject command) {
        for (String s : names) {
            if (s.equalsIgnoreCase(args) || args.equalsIgnoreCase("/" + s)) {
                return true;
            }
        }
        return false;
    }
}
