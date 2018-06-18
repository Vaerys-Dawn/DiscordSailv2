package com.github.vaerys.objects.utils;


import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SubCommandObject {

    private final String names[];
    private final String usage;
    private final String description;
    private final SAILType type;
    private final Permissions[] permissions;
    private String regex = "";

    public SubCommandObject(String[] name, String usage, String description, SAILType type, Permissions... permissions) {
        this.names = name;
        this.usage = usage;
        this.description = description;
        this.type = type;
        this.permissions = permissions;
    }

    public boolean isSubCommand(CommandObject command) {
        if (!GuildHandler.testForPerms(command, permissions)) return false;

        String toTest = command.message.getContent();
        if (toTest.length() > 200) {
            toTest = StringUtils.truncate(toTest, 200);
        }
        for (String s : names) {
            String regexString = "^(?i)" + Utility.escapeRegex(command.guild.config.getPrefixCommand()) + s + regex + "(.|\n)*";
            if (Pattern.compile(regexString).matcher(toTest).matches()) {
                return true;
            }
        }
        return false;
    }

    public String getArgs(CommandObject command) {
        String toTest = command.message.getContent();
        if (toTest.length() > 200) {
            toTest = StringUtils.truncate(toTest, 200);
        }
        for (String s : names) {
            String regexString = "^(?i)" + Utility.escapeRegex(command.guild.config.getPrefixCommand()) + s + regex + "(.|\n)*";
            if (Pattern.compile(regexString).matcher(toTest).matches()) {
                String regexRemove = "^(?i)" + Utility.escapeRegex(command.guild.config.getPrefixCommand()) + s + regex + " ";
                return command.message.getContent().replaceAll(regexRemove, "");
            }
        }
        return "";
    }

    public String getCommandUsage(CommandObject command) {
        StringHandler usage = new StringHandler();
        StringHandler regexHandler = new StringHandler(regex);
        usage.append(command.guild.config.getPrefixCommand());
        usage.append(names[0]);
        regexHandler.replace("(", "[");
        regexHandler.replace(")", "]");
        regexHandler.replace("|", "/");
        usage.append(regexHandler);
        usage.append(" ");
        if (this.usage != null && !this.usage.isEmpty()) {
            usage.append(this.usage);
        }
        return usage.toString();
    }

    public String[] getNames() {
        return names;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public SAILType getType() {
        return type;
    }

    public Permissions[] getPermissions() {
        return permissions;
    }

    public SubCommandObject appendRegex(String s) {
        this.regex = s;
        return this;
    }

    public String getHelpDesc(CommandObject command) {
        StringHandler builder = new StringHandler();
        builder.append(description + "\n");
        builder.append("**Type: **" + type.toString() + ".");

        // display permissions
        if (permissions != null && permissions.length != 0) {

            builder.append("\n**Perms: **");
            ArrayList<String> permList = new ArrayList<>(permissions.length);
            for (Permissions p : permissions) {
                permList.add(Utility.enumToString(p));
            }
            builder.append(Utility.listFormatter(permList, true));
        }

        if (names.length > 1) {
            List<String> aliases = Arrays.asList(names).stream().map(s -> command.guild.config.getPrefixCommand() + s).collect(Collectors.toList());
            aliases.remove(0);
            builder.append("\n**Aliases:** " + Utility.listFormatter(aliases, true));
        }
        return builder.toString();
    }

    public String getRegex() {
        return regex;
    }

    public String getCommand(CommandObject command) {
        return command.guild.config.getPrefixCommand() + names[0];
    }
}
