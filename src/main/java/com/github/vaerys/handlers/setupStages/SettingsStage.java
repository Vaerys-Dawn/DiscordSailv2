package com.github.vaerys.handlers.setupStages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleInit;
import com.github.vaerys.guildtoggles.toggles.DebugMode;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.GuildToggle;

import java.util.ArrayList;
import java.util.List;

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
                .append("Here's a list of the settings that aren't tied to any specific module.\n")
                .append("\n");

        List<GuildToggle> globalToggles = ToggleInit.getToggles(false);
        List<GuildToggle> modules = ToggleInit.getToggles(true);

        // skip debug mode after it's defaulted to off:
        if (!new DebugMode().getDefault()) {
            globalToggles.removeIf(t -> t.name() == SAILType.DEBUG_MODE);
        }

        for (GuildToggle t : globalToggles) {
            for (GuildToggle m : modules) {
                if (m.settings.contains(t)) {
                    globalToggles.remove(t);
                    break;
                }
            }
        }

        String format = "\t> **%s** - %s\n";
        for (GuildToggle t : globalToggles) {
            output.appendFormatted(format, t.name().toString(), t.shortDesc(command));
        }
        RequestHandler.sendMessage(output.toString(), command.channel);


        List<String> enabled  = new ArrayList<>();
        List<String> disabled = new ArrayList<>();

        for (GuildToggle g : globalToggles) {
            if (g.enabled(command.guild.config)) enabled.add(g.name().toString());
            else disabled.add(g.name().toString());
        }

        XEmbedBuilder embed = new XEmbedBuilder(command);
        embed.withTitle("Global Settings");
        embed.appendField("Enabled", "```" + Utility.listFormatter(enabled, true) + "```", false);
        embed.appendField("Disabled", "```" + Utility.listFormatter(disabled, true) + "```", false);
        embed.send(command.channel);
    }

    @Override
    public boolean execute(CommandObject command) {
        return false;
    }

    @Override
    public int setupStage() {
        return 1;
    }
}
