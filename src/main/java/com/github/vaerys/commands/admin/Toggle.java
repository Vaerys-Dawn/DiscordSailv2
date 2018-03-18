package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.HelpModules;
import com.github.vaerys.commands.help.HelpSettings;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleInit;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Toggle extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return getContent(args, command, false);
    }

    public String getContent(String args, CommandObject command, boolean isModule) {
        StringBuilder builder = new StringBuilder();
        if (!args.isEmpty()) {

            GuildToggle toggle = ToggleInit.getGuildToggle(args, isModule);

            if (toggle == null) {
                if (isModule) {
                    builder.append("> Could not find Module \"" + args + "\".\n");
                } else {
                    builder.append("> Could not find Setting \"" + args + "\".\n");
                }
            } else {

                toggle.toggle(command.guild.config);
                command.guild.loadCommandData();

                String mode = toggle.enabled(command.guild.config) ? "enabled" : "disabled";
                String type = toggle.isModule() ? "module" : "setting";
                String helpCommand = toggle.isModule() ? new HelpModules().getUsage(command) : new HelpSettings().getUsage(command);
                return "> **" + toggle.name() + "** is now **" + mode + "**.\n\n" +
                        "To see more info about what this " + type + " " + mode + " you can run **" + helpCommand + "**.";
            }

        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String modifier = isModule ? "Module" : "Setting";
        String title;
        title = "> Here are all of the available " + modifier + "s:\n";

        List<SAILType> typesActive = new LinkedList<>();
        List<SAILType> typesDeactivated = new LinkedList<>();
        for (GuildToggle t : command.guild.toggles) {
            if (t.isModule() == isModule) {
                if (t.enabled(command.guild.config)) typesActive.add(t.name());
                else typesDeactivated.add(t.name());
            }
        }
        Collections.sort(typesActive);
        Collections.sort(typesDeactivated);
        embedBuilder.withTitle(title);

        StringHandler desc = new StringHandler();


        desc.append("**Activated**\n```\n" + spacer + Utility.listEnumFormatter(typesActive, true) + "```\n" +
                "**Deactivated**\n```\n" + spacer + Utility.listEnumFormatter(typesDeactivated, true) + "```\n");
        desc.append("The Command **");
        if (isModule) {
            desc.append(new HelpModules().getUsage(command));
        } else {
            desc.append(new HelpSettings().getUsage(command));
        }
        desc.append("** Can give you extra information about each of the above.\n\n");
        desc.append(missingArgs(command));
        embedBuilder.withDescription(desc.toString());
        RequestHandler.sendEmbedMessage("", embedBuilder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"Setting", "Toggle", "Settings", "Toggles"};
    }

    @Override
    public String description(CommandObject command) {
        return "Toggles the specified setting of the Guild Config.";
    }

    @Override
    protected String usage() {
        return "(Setting)";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
