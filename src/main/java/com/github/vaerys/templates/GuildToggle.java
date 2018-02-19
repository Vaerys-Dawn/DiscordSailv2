package com.github.vaerys.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public abstract class GuildToggle {

    public SAILType affectsType;
    public List<Command> commands = new ArrayList<>();
    public List<GuildSetting> settings = new ArrayList<>();
    public List<ChannelSetting> channels = new ArrayList<>();

    public GuildToggle() {
        setup();
    }

    public void execute(GuildObject guild) {
        guild.removeCommandsByType(affectsType);
        for (Command c : commands) {
            guild.removeCommand(c.names);
        }
        for (ChannelSetting c : channels) {
            guild.removeChannelSetting(c.toString());
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
                .filter(command1 -> command1.type() == affectsType)
                .map(command1 -> command1.getCommand(command)).collect(Collectors.toList()));
        commandNames = commandNames.stream().distinct().collect(Collectors.toList());
        List<SAILType> settingNames = settings.stream().map(guildSetting -> guildSetting.name()).collect(Collectors.toList());
        List<ChannelSetting> channelNames = channels.stream().map(channelSetting -> channelSetting).collect(Collectors.toList());

        if (commandNames.size() != 0) {
            builder.appendField("Commands:", "```\n" + Utility.listFormatter(commandNames, true) + Command.spacer + "```", true);
        }

        if (settingNames.size() != 0) {
            builder.appendField("Settings:", "```\n" + Utility.listEnumFormatter(settingNames, true) + Command.spacer + "```", true);
        }

        if (channelNames.size() != 0) {
            builder.appendField("Channels:", "```\n" + Utility.listEnumFormatter(channelNames, true) + Command.spacer + "```", true);
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
        boolean isError = false;
        response.append(Utility.formatError(this));
        String prefix;
        if (isModule()) prefix = "Module";
        else prefix = "Setting";
        if (name() == null) {
            response.append("   > " + prefix + " type is empty.\n");
            isError = true;
        }
        if (desc(new CommandObject()) == null || desc(new CommandObject()).isEmpty()) {
            response.append("   > " + prefix + " description is empty.\n");
            isError = true;
        }
        if (isError) {
            return response.toString();
        } else {
            return null;
        }
    }

    public abstract SAILType name();

    public abstract boolean toggle(GuildConfig config);

    public abstract boolean get(GuildConfig config);

    public abstract boolean getDefault();

    public abstract String desc(CommandObject command);

    public abstract void setup();

    public abstract boolean isModule();

    public abstract String stats(CommandObject object);

    public abstract boolean statsOnInfo();
}
