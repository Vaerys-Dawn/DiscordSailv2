package com.github.vaerys.handlers.setupStages;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.commands.admin.Toggle;
import com.github.vaerys.commands.help.HelpSettings;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.guildtoggles.toggles.DebugMode;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;

import java.util.*;
import java.util.stream.Collectors;

public class SettingsStage extends SetupHandler {
    @Override
    public String title() {
        return "Global Settings";
    }

    @Override
    public void stepText(CommandObject command) {
        StringHandler output = new StringHandler();
        output.append("Next step is to pick which settings you want to use.\n")
                .append("There's a lot of settings in modules, and those will be set there.\n")
                .append("Here's a list of the settings that aren't tied to any specific module.\n");

        // Get the settings and modules.
        List<GuildToggle> globalToggles = ToggleList.getToggles(false);
        List<GuildToggle> modules = ToggleList.getToggles(true);
        // Sort settings
        globalToggles.sort(Comparator.comparing(GuildToggle::name));

        // Init additional vars
        List<String> enabled = new ArrayList<>();
        List<String> disabled = new ArrayList<>();
        String format = "\t> **%s** - %s\n";

        // skip debug mode after it's defaulted to off:
        if (!new DebugMode().getDefault()) {
            globalToggles.removeIf(t -> t.name() == SAILType.DEBUG_MODE);
        }

        // Filter toggles that are not part of modules.
        List<SAILType> types = new LinkedList<>();

        modules.forEach(t -> types.addAll(t.settings));

        ListIterator iterator = globalToggles.listIterator();
        while (iterator.hasNext()) {
            GuildToggle toggle = (GuildToggle) iterator.next();
            if (types.contains(toggle.name())) {
                // Is part of a module, remove from list
                iterator.remove();
            } else {
                // is not, get extra information...
                if (toggle.enabled(command.guild.config)) enabled.add(toggle.name().toString());
                else disabled.add(toggle.name().toString());
                // append to list of things.
                output.appendFormatted(format, toggle.name().toString(), toggle.shortDesc(command));
            }
        }

        // Send message
        output.append("\nYou can switch settings on and off with **" +
                Command.get(Toggle.class).getCommand(command) + "** and get more info on each setting with **" +
                Command.get(HelpSettings.class).getCommand(command) + "**.");

        // Append additional enabled/disabled state info.
        XEmbedBuilder embed = new XEmbedBuilder(command);
        embed.withTitle("Global Settings");
        embed.appendField("Enabled", "```" + Utility.listFormatter(enabled, true) + "```", false);
        embed.appendField("Disabled", "```" + Utility.listFormatter(disabled, true) + "```", false);

        RequestHandler.sendEmbedMessage(output.toString(), embed, command.channel);
    }

    @Override
    public boolean execute(CommandObject command) {
        return false;
    }

    @Override
    public SetupStage setupStage() {
        return SetupStage.SETUP_SETTINGS;
    }
}
