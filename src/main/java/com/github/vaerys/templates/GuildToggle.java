package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.entities.AbstractMessage;
import net.dv8tion.jda.internal.entities.DataMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 20/02/2017.
 */
public abstract class GuildToggle {

    public SAILType affectsType;
    public List<Class<? extends Command>> commands = new ArrayList<>();
    public List<SAILType> settings = new ArrayList<>();
    public List<ChannelSetting> channels = new ArrayList<>();

    public void execute(GuildObject guild) {
        guild.removeCommandsByType(affectsType);
        for (Class<? extends Command> c : commands) {
            guild.removeCommand(Command.get(c).names);
        }
        for (ChannelSetting c : channels) {
            guild.removeChannelSetting(c);
        }
        for (SAILType s : settings) {
            guild.removeToggle(s);
        }
    }

    public XEmbedBuilder info(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        if (isModule()) {
            builder.setTitle("Module - " + name());
        } else {
            builder.setTitle("Setting - " + name());
        }
        String fullDesc = desc(command);
        if (statsOnInfo()) {
            String stats = stats(command);
            if (stats != null && !stats.isEmpty()) {
                fullDesc += "\n\n**Stats:**\n" + stats;
            }
        }
        builder.setDescription(fullDesc);
        List<String> commandNames = commands.stream().map(c -> Command.get(c).getCommand(command)).collect(Collectors.toList());
        commandNames.addAll(CommandList.getAll().stream()
                .filter(command1 -> command1.type() == affectsType)
                .map(command1 -> command1.getCommand(command)).collect(Collectors.toList()));
        commandNames = commandNames.stream().distinct().collect(Collectors.toList());
        List<SAILType> settingNames = settings;
        List<ChannelSetting> channelNames = channels.stream().collect(Collectors.toList());

        if (commandNames.size() != 0) {
            builder.addField("Commands:", "```\n" + Utility.listFormatter(commandNames, true) + Command.SPACER + "```", true);
        }

        if (settingNames.size() != 0) {
            builder.addField("Settings:", "```\n" + Utility.listEnumFormatter(settingNames, true) + Command.SPACER + "```", true);
        }

        if (channelNames.size() != 0) {
            builder.addField("Channels:", "```\n" + Utility.listEnumFormatter(channelNames, true) + Command.SPACER + "```", true);
        }

        StringBuilder footer = new StringBuilder();
        if (isModule()) footer.append("Module ");
        else footer.append("Setting ");
        if (enabled(command.guild.config)) footer.append("Enabled.");
        else footer.append("Disabled.");
        builder.setFooter(footer.toString());
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
//        if (desc(new CommandObject(Globals.defaultMessage, Globals.defaultMessage.getGuild())) == null || desc(new CommandObject(Globals.defaultMessage, Globals.defaultMessage.getGuild())).isEmpty()) {
//            response.append("   > " + prefix + " description is empty.\n");
//            isError = true;
//        }
        if (isError) {
            return response.toString();
        } else {
            return null;
        }
    }

    public abstract SAILType name();

    public abstract boolean toggle(GuildConfig config);

    public abstract boolean enabled(GuildConfig config);

    public abstract boolean getDefault();

    public abstract String desc(CommandObject command);

    public abstract String shortDesc(CommandObject command);

    public abstract void setup();

    public abstract boolean isModule();

    public abstract String stats(CommandObject command);

    public abstract boolean statsOnInfo();

    public static GuildToggle get(Class obj){
        return ToggleList.getToggle(obj);
    }
}
