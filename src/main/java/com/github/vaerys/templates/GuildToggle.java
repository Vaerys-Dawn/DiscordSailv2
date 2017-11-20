package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.pogos.GuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 20/02/2017.
 */
public abstract class GuildToggle {

    public String affectsType;
    public List<Command> commands = new ArrayList<>();
    public List<GuildSetting> settings = new ArrayList<>();
    public List<ChannelSetting> channels = new ArrayList<>();

    public GuildToggle() {
        setup();
    }

    public void execute(GuildObject guild) {
        guild.removeCommandsByType(affectsType);
        for (Command c : commands) {
            guild.removeCommand(c.names());
        }
        for (ChannelSetting c : channels) {
            guild.removeChannel(c.name());
        }
        for (GuildSetting s : settings) {
            guild.removeToggle(s.name());
        }
    }

    public XEmbedBuilder info(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        if (isModule()) {
            builder.withTitle("Module - " + name());
        } else {
            builder.withTitle("Setting - " + name());
        }
        String fullDesc = desc(command);
        if (statsOnInfo()) {
            String stats = stats(command);
            if (stats != null && !stats.isEmpty()) {
                fullDesc += "\n\n**Stats:**\n" + stats;
            }
        }
        builder.withDesc(fullDesc);
        List<String> commandNames = commands.stream().map(command1 -> command1.getCommand(command)).collect(Collectors.toList());
        commandNames.addAll(Globals.getAllCommands().stream()
                .filter(command1 -> command1.type().equalsIgnoreCase(affectsType))
                .map(command1 -> command1.getCommand(command)).collect(Collectors.toList()));
        commandNames = commandNames.stream().distinct().collect(Collectors.toList());
        List<String> settingNames = settings.stream().map(guildSetting -> guildSetting.name()).collect(Collectors.toList());
        List<String> channelNames = channels.stream().map(channelSetting -> channelSetting.name()).collect(Collectors.toList());

        if (commandNames.size() != 0) {
            builder.appendField("Commands:", "```\n" + Utility.listFormatter(commandNames, true) + Command.spacer + "```", true);
        }

        if (settingNames.size() != 0) {
            builder.appendField("Settings:", "```\n" + Utility.listFormatter(settingNames, true) + Command.spacer + "```", true);
        }

        if (channelNames.size() != 0) {
            builder.appendField("Channels:", "```\n" + Utility.listFormatter(channelNames, true) + Command.spacer + "```", true);
        }

        StringBuilder footer = new StringBuilder();
        if (isModule()) footer.append("Module ");
        else footer.append("Setting ");
        if (get(command.guild.config)) footer.append("Enabled.");
        else footer.append("Disabled.");
        builder.withFooterText(footer.toString());
        return builder;
    }

    public String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append("\n>> Begin Error Report: " + this.getClass().getName() + " <<\n");
        if (name() == null || name().isEmpty()) {
            response.append("> TYPE IS EMPTY.\n");
            isErrored = true;
        }
        if (desc(new CommandObject()) == null || desc(new CommandObject()).isEmpty()) {
            response.append("> DESCRIPTION IS EMPTY.\n");
            isErrored = true;
        }
        response.append(">> End Error Report <<");
        if (isErrored) {
            return response.toString();
        } else {
            return null;
        }
    }

    public abstract String name();

    public abstract boolean toggle(GuildConfig config);

    public abstract boolean get(GuildConfig config);

    public abstract boolean getDefault();

    public abstract String desc(CommandObject command);

    public abstract void setup();

    public abstract boolean isModule();

    public abstract String stats(CommandObject object);

    public abstract boolean statsOnInfo();
}
