package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class CCHandler {

    private final static Logger logger = LoggerFactory.getLogger(CCHandler.class);

    public static void handleCommand(String args, CommandObject command) {
        //cc lockout handling
        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_USE_CCS)) {
            Utility.sendMessage("> Nothing interesting happens. `(ERROR: 403)`", command.channel.get());
            return;
        }

        SplitFirstObject commandName = new SplitFirstObject(args);
        CCommandObject commandObject = command.guild.customCommands.getCommand(commandName.getFirstWord(), command);

        String ccArgs = commandName.getRest();
        if (ccArgs == null) {
            ccArgs = "";
        }

        if (commandObject == null) return;

        logger.debug(Utility.loggingFormatter(command, "CUSTOM_COMMAND", commandObject.getName(command), ccArgs));

        String contents = commandObject.getContents(true);
        //shitpost handling
        if (commandObject.isShitPost() && command.guild.config.shitPostFiltering && !Utility.testForPerms(command, Permissions.MANAGE_CHANNELS)) {
            List<IChannel> channels = command.guild.config.getChannelsByType(Command.CHANNEL_SHITPOST, command.guild);
            if (channels.size() != 0 && !channels.contains(command.channel.get())) {
                channels = command.user.getVisibleChannels(channels);
                List<String> channelMentions = Utility.getChannelMentions(channels);
                Utility.sendMessage(Utility.getChannelMessage(channelMentions), command.channel.get());
                return;
            }
        }

        //tag handling
        for (TagObject t : TagList.getType(TagList.CC)) {
            contents = t.handleTag(contents, command, ccArgs);
            if (contents == null) return;
        }
        Utility.sendMessage(contents, command.channel.get());
    }
}