package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.JoinMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.Random;

public class JoinHandler {


    public static void customJoinMessages(GuildObject content, IUser user) {
        IChannel channel = content.getChannelByType(ChannelSetting.JOIN_CHANNEL);
        if (channel == null) return;
        Random random = new Random();
        List<JoinMessage> joinMessageList = content.channelData.getJoinMessages();
        if (joinMessageList.size() == 0) return;
        JoinMessage message = joinMessageList.get(random.nextInt(joinMessageList.size()));
        String response = message.getContent(new CommandObject(content, channel, user));
        RequestHandler.sendMessage(response, channel);
    }
}
