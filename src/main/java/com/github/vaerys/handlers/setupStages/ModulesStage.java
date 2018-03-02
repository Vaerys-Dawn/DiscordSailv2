package com.github.vaerys.handlers.setupStages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.Module;
import com.github.vaerys.commands.help.HelpModules;
import com.github.vaerys.guildtoggles.ToggleInit;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.GuildToggle;

import java.util.Comparator;
import java.util.List;

public class ModulesStage extends SetupHandler {

    @Override
    public String title() {
        return "Modules";
    }

    @Override
    public void stepText(CommandObject command) {
        StringHandler output = new StringHandler();
        List<GuildToggle> modules = ToggleInit.getToggles(true);

        output.append("First things first. Obviously.\n");
        output.append("You're going to want to choose which modules you would like to use on your server.\n\n");
        output.append("**Here's a list of modules I have for you to choose from:**\n");

        String moduleItemFormat = "\t> **%s** - %s\n";
        modules.sort(Comparator.comparing(GuildToggle::toString));
        for (GuildToggle module : modules) {
            output.appendFormatted(moduleItemFormat, module.name(), module.shortDesc(command));
        }
        String toggleReminder = "\nYou can toggle a module on and off with the `%s` command.\nIf you want more detailed information about the module, you can use `%s`";
        output.appendFormatted(toggleReminder, new Module().getUsage(command), new HelpModules().getUsage(command));
        //RequestHandler.sendMessage(output.toString(), command.user.getDmChannel());

        XEmbedBuilder embed = new XEmbedBuilder(command);
        StringHandler enabled = new StringHandler();
        StringHandler disabled = new StringHandler();
        for (GuildToggle m : modules) {
            if (m.enabled(command.guild.config)) {
                enabled.append(m.name() + "\n");
            } else {
                disabled.append(m.name() + "\n");
            }
        }

        embed.withTitle("Here's the current list of enabled and disabled modules");
        embed.appendField("Enabled Modules", enabled.toString(), true);
        embed.appendField("Disabled Modules", disabled.toString(), true);
        //embed.send(command.user.getDmChannel());
        RequestHandler.sendEmbedMessage(output.toString(), embed, command.channel);
    }

    @Override
    public int setupStage() {
        return 0;
    }

    @Override
    public boolean execute(CommandObject command) {
        return true;
    }
}
