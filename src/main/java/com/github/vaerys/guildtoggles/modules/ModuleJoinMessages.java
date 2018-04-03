package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.IChannel;

public class ModuleJoinMessages extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.CUSTOM_JOIN_MESSAGES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleJoinMessages = !config.moduleJoinMessages;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleJoinMessages;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleJoinMessages;
    }

    @Override
    public String desc(CommandObject command) {
        return "Allows you to set up custom join messages and have them sent to new users as they join.";
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Custom Join Messages";
    }

    @Override
    public void setup() {
        channels.add(ChannelSetting.JOIN_CHANNEL);
    }

    @Override
    public String stats(CommandObject command) {
        IChannel channel = command.guild.getChannelByType(ChannelSetting.JOIN_CHANNEL);
        StringHandler builder = new StringHandler();
        if (channel != null) {
            builder.append("Join Channel: " + channel.mention());
        }
        if (!builder.isEmpty()) builder.append("\n");
        builder.append("Message count: " + command.guild.channelData.getJoinMessages().size());
        return builder.toString();
    }
}
