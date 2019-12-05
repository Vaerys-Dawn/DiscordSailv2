package com.github.vaerys.handlers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.JoinMessage;
import com.github.vaerys.objects.adminlevel.MutedUserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.Random;

public class JoinHandler {

    /**
     * Send A special message to the specified JoinMessage channel that welcomes the user to the server.
     *
     * @param user    The user that joined. ({@link UserObject})
     * @param content The Guild that the user joined. ({@link GuildObject})
     */
    public static void customJoinMessages(GuildObject content, IUser user) {
        IChannel channel = content.getChannelByType(ChannelSetting.JOIN_CHANNEL);
        if (channel == null) return;
        Random random = Globals.getGlobalRandom();
        List<JoinMessage> joinMessageList = content.channelData.getJoinMessages();
        if (joinMessageList.size() == 0) return;
        JoinMessage message = joinMessageList.get(random.nextInt(joinMessageList.size()));
        String response = message.getContent(new CommandObject(content, channel, user));
        RequestHandler.sendMessage(response, channel);
    }

    /**
     * Checks to see if the user's account is brand new or not and send a message if true. (new = account is younger than 5 hours)
     *
     * @param user    The user that joined. ({@link UserObject})
     * @param content The Guild that the user joined. ({@link GuildObject})
     * @param event   The event that calls this. ({@link UserJoinEvent})
     */
    public static void checkNewUsers(GuildObject content, UserJoinEvent event, UserObject user) {
        long difference = event.getJoinTime().toEpochMilli() - event.getUser().getCreationDate().toEpochMilli();
        if (StringUtils.containsIgnoreCase(user.username,"igg-games.com")){
            RequestHandler.roleManagement(user, content, content.config.getMutedRoleID(), true);
        }
        if ((5 * 60 * 60 * 1000) > difference) {
            ProfileObject profileObject = user.addProfile(content);
            profileObject.getSettings().add(UserSetting.JOIN_WHILE_NEW);
            IChannel admin = content.getChannelByType(ChannelSetting.ADMIN_LOG);
            if (admin == null) {
                admin = event.getGuild().getDefaultChannel();
            }
            if (admin != null) {
                RequestHandler.sendMessage("> New user " + user.mention() + " has a creation time less than 5 hours ago.", admin);
            }
        }
    }

    /**
     * Sends a special message to the user when the join the server.
     *
     * @param user    The user that joined. ({@link UserObject})
     * @param content The Guild that the user joined. ({@link GuildObject})
     * @param event   The event that calls this. ({@link UserJoinEvent})
     */
    public static void sendWelcomeMessage(GuildObject content, UserJoinEvent event, UserObject user) {
        String message = content.config.getJoinMessage();
        message = message.replace("<server>", event.getGuild().getName());
        message = message.replace("<user>", event.getUser().getName());
        user.sendDm(message);
    }

    /**
     * Automatically re-mutes users that were muted when they left and their mute timer hasnt timed out.
     *
     * @param user    The user that joined. ({@link UserObject})
     * @param content The Guild that the user joined. ({@link GuildObject})
     * @param event   The event that calls this. ({@link UserJoinEvent})
     */
    public static void autoReMute(UserJoinEvent event, GuildObject content, UserObject user) {
        for (MutedUserObject u : content.users.mutedUsers) {
            if (u.getID() == event.getUser().getLongID()) {
                IChannel admin = content.getChannelByType(ChannelSetting.ADMIN);
                if (admin != null) {
                    RequestHandler.sendMessage("> Looks like " + user.mention() + " has returned, I have muted them again for you.", admin);
                }
                RequestHandler.roleManagement(user, content, content.config.getMutedRoleID(), true);
            }
        }
    }
}
