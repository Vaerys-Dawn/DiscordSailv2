package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.help.HelpModules;
import com.github.vaerys.commands.help.HelpSettings;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

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
        HelpModules modules = get(HelpModules.class);
        HelpSettings settings = get(HelpSettings.class);

        StringHandler desc = new StringHandler();
        if (!args.isEmpty()) {
            GuildToggle toggle = ToggleList.getGuildToggle(args, isModule);
            if (toggle == null) {
                desc.append(isModule ? "\\> Could not find Module **" : "\\> Could not find Setting **");
                desc.append(args + "**.\n\n");
            } else {
                GuildToggle module = ToggleList.getModuleFromSetting(toggle.name());
                if (module != null && !module.enabled(command.guild.config)) {
                    desc.appendFormatted("\\> Could not toggle Setting **%s**, Module **%s** is disabled.\n\n", args, module.name());
                } else {
                    toggle.toggle(command.guild.config);
                    command.guild.loadCommandData();
                    String mode = toggle.enabled(command.guild.config) ? "enabled" : "disabled";
                    String type = toggle.isModule() ? "module" : "setting";
                    String helpCommand = toggle.isModule() ? modules.getUsage(command) : settings.getUsage(command);
                    return "\\> **" + toggle.name() + "** is now **" + mode + "**.\n\n" +
                            "To see more info about what this " + type + " " + mode + " you can run **" + helpCommand + "**.";
                }
            }
        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String modifier = isModule ? "Module" : "Setting";
        String title;
        title = "\\> Here are all of the available " + modifier + "s:\n";

        List<SAILType> typesActive = new LinkedList<>();
        List<SAILType> typesDeactivated = new LinkedList<>();
        for (GuildToggle t : command.guild.getToggles()) {
            if (t.isModule() == isModule) {
                if (t.enabled(command.guild.config)) typesActive.add(t.name());
                else typesDeactivated.add(t.name());
            }
        }
        Collections.sort(typesActive);
        Collections.sort(typesDeactivated);
        embedBuilder.setTitle(title);

        desc.append("**Activated**\n```\n" + SPACER + Utility.listEnumFormatter(typesActive, true) + "```\n" +
                "**Deactivated**\n```\n" + SPACER + Utility.listEnumFormatter(typesDeactivated, true) + "```\n");
        desc.append("The Command **");
        if (isModule) {
            desc.append(modules.getUsage(command));
        } else {
            desc.append(settings.getUsage(command));
        }
        desc.append("** Can give you extra information about each of the above.\n\n");

        desc.append(missingArgs(command));

        embedBuilder.setDescription(desc.toString());
        embedBuilder.queue(command);
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
        // does nothing
    }
}
