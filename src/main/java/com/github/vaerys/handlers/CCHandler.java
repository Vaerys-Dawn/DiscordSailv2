package com.github.vaerys.handlers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.admintags.TagAutoDelete;
import com.github.vaerys.tags.cctags.TagRemoveMentions;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class CCHandler {

    private final static Logger logger = LoggerFactory.getLogger(CCHandler.class);

    private final static ScheduledExecutorService deleter = Executors.newScheduledThreadPool(50);

    public static void handleAdminCC(String args, CommandObject command) {

        SplitFirstObject commandName = new SplitFirstObject(args);
        AdminCCObject cc = command.guild.adminCCs.getCommand(commandName.getFirstWord(), command);

        if (cc == null) return;

        String ccArgs = commandName.getRest();
        if (ccArgs == null) {
            ccArgs = "";
        }

        List<TextChannel> ccDenied = command.guild.getChannelsByType(ChannelSetting.CC_DENIED);
        if (ccDenied.contains(command.guildChannel.get())) {
            command.guildChannel.queueMessage("\\> Custom Command usage has been disabled for this messageChannel.");
            return;
        }

        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_USE_CCS)) {
            command.guildChannel.queueMessage("\\> Nothing interesting happens. `(ERROR: 403)`");
            return;
        }

        command.guild.sendDebugLog(command, "ADMIN_CUSTOM_COMMAND", cc.getName(command), ccArgs);

        String contents = cc.getContents(true);

        List<TagObject> tags = TagList.getType(TagType.CC);
        tags.addAll(TagList.getType(TagType.ADMIN_CC));
        TagList.sort(tags);


        for (TagObject t : tags) {
            try {
                FileHandler.writeToFile(Constants.DIRECTORY_STORAGE + "Error.txt", contents, true);
                contents = t.handleTag(contents, command, ccArgs, cc);
                if (contents == null) return;
            } catch (StackOverflowError e) {
                System.out.println("Error caught");
                return;
            }
        }

        cc.cullKeys();
        TagAutoDelete autoDelete = TagList.getTag(TagAutoDelete.class);
        TagRemoveMentions removeMentions = TagList.getTag(TagRemoveMentions.class);

        if (autoDelete.cont(contents)) {
            try {
                int time = Integer.parseInt(autoDelete.getSubTag(contents));
                contents = autoDelete.removeAllTag(contents);
                contents = removeMentions.handleTag(contents, command, "");
                Message message = command.guildChannel.sendMessage(contents);
                autoDelete(message, time);
                return;
            } catch (NumberFormatException e) {
                //do nothing
            }
        }
        command.guildChannel.queueMessage(contents);
    }

    public static void handleCommand(String args, CommandObject command) {
        //cc lockout handling


        SplitFirstObject commandName = new SplitFirstObject(args);
        CCommandObject commandObject = command.guild.customCommands.getCommand(commandName.getFirstWord(), command);

        String ccArgs = commandName.getRest();
        if (ccArgs == null) {
            ccArgs = "";
        }

        if (commandObject == null) return;

        List<TextChannel> ccDenied = command.guild.getChannelsByType(ChannelSetting.CC_DENIED);
        if (ccDenied.contains(command.guildChannel.get())) {
            command.guildChannel.queueMessage("\\> Custom Command usage has been disabled for this messageChannel.");
            return;
        }

        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_USE_CCS)) {
            command.guildChannel.queueMessage("\\> Nothing interesting happens. `(ERROR: 403)`");
            return;
        }

        command.guild.sendDebugLog(command, "CUSTOM_COMMAND", commandObject.getName(command), ccArgs);

        String contents = commandObject.getContents(true);
        //shitpost handling
        if (commandObject.isShitPost() && command.guild.config.shitPostFiltering && !GuildHandler.testForPerms(command, Permission.MANAGE_CHANNEL)) {
            List<TextChannel> channels = command.guild.getChannelsByType(ChannelSetting.SHITPOST);
            if (channels.size() != 0 && !channels.contains(command.guildChannel.get())) {
                channels = command.user.getVisibleChannels(channels);
                command.guildChannel.queueMessage(Utility.getChannelMessage(channels));
                return;
            }
        }

        //tag handling
        for (TagObject t : TagList.getType(TagType.CC)) {
            contents = t.handleTag(contents, command, ccArgs);
            if (contents == null) return;
        }
        command.guildChannel.queueMessage(contents);

    }

    public static void autoDelete(Message message, int time) {
        deleter.schedule(() -> message.delete().queue(), time, TimeUnit.MINUTES);
    }
}
