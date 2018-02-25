package com.github.vaerys.handlers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.enums.TagType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class CCHandler {

    private final static Logger logger = LoggerFactory.getLogger(CCHandler.class);

    public static void handleCommand(String args, CommandObject command) {
        //cc lockout handling

        List<IChannel> ccDenied = command.guild.getChannelsByType(ChannelSetting.CC_DENIED);
        if (ccDenied.contains(command.channel.get())) {
            RequestHandler.sendMessage("> Custom Command usage has been disabled for this channel.", command.channel);
            return;
        }

        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_USE_CCS)) {
            RequestHandler.sendMessage("> Nothing interesting happens. `(ERROR: 403)`", command.channel.get());
            return;
        }

        SplitFirstObject commandName = new SplitFirstObject(args);
        CCommandObject commandObject = command.guild.customCommands.getCommand(commandName.getFirstWord(), command);

        String ccArgs = commandName.getRest();
        if (ccArgs == null) {
            ccArgs = "";
        }

        if (commandObject == null) return;

        command.guild.sendDebugLog(command, "CUSTOM_COMMAND", commandObject.getName(command), ccArgs);

        String contents = commandObject.getContents(true);
        //shitpost handling
        if (commandObject.isShitPost() && command.guild.config.shitPostFiltering && !GuildHandler.testForPerms(command, Permissions.MANAGE_CHANNELS)) {
            List<IChannel> channels = command.guild.getChannelsByType(ChannelSetting.SHITPOST);
            if (channels.size() != 0 && !channels.contains(command.channel.get())) {
                channels = command.user.getVisibleChannels(channels);
                List<String> channelMentions = Utility.getChannelMentions(channels);
                RequestHandler.sendMessage(Utility.getChannelMessage(channelMentions), command.channel.get());
                return;
            }
        }

        //tag handling
        for (TagObject t : TagList.getType(TagType.CC)) {
            contents = t.handleTag(contents, command, ccArgs);
            if (contents == null) return;
        }
        RequestHandler.sendMessage(contents, command.channel.get());
    }
}