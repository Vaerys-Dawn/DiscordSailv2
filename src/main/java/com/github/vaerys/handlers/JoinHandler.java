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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Random;

public class JoinHandler {

    /**
     * Send A special message to the specified JoinMessage messageChannel that welcomes the globalUser to the server.
     *
     * @param user    The globalUser that joined. ({@link UserObject})
     * @param content The Guild that the globalUser joined. ({@link GuildObject})
     */
    public static void customJoinMessages(GuildObject content, Member user) {
        TextChannel channel = content.getChannelByType(ChannelSetting.JOIN_CHANNEL);
        if (channel == null) return;
        Random random = Globals.getGlobalRandom();
        List<JoinMessage> joinMessageList = content.channelData.getJoinMessages();
        if (joinMessageList.size() == 0) return;
        JoinMessage message = joinMessageList.get(random.nextInt(joinMessageList.size()));
        String response = message.getContent(new CommandObject(content, channel, user));
        channel.sendMessage(response).queue();
    }

    /**
     * Checks to see if the globalUser's account is brand new or not and send a message if true. (new = account is younger than 5 hours)
     *
     * @param user    The globalUser that joined. ({@link UserObject})
     * @param content The Guild that the globalUser joined. ({@link GuildObject})
     * @param event   The event that calls this. ({@link GuildMemberJoinEvent})
     */
    public static void checkNewUsers(GuildObject content, GuildMemberJoinEvent event, UserObject user) {
        long difference = event.getMember().getTimeJoined().toInstant().toEpochMilli() - event.getUser().getTimeCreated().toInstant().toEpochMilli();
        if (StringUtils.containsIgnoreCase(user.username, "igg-games.com")) {
            content.get().addRoleToMember(user.getMember(), content.getRoleById(content.config.getMutedRoleID())).complete();
        }
        if ((5 * 60 * 60 * 1000) > difference) {
            ProfileObject profileObject = user.addProfile();
            profileObject.getSettings().add(UserSetting.JOIN_WHILE_NEW);
            TextChannel admin = content.getChannelByType(ChannelSetting.ADMIN_LOG);
            if (admin == null) {
                admin = event.getGuild().getDefaultChannel();
            }
            if (admin != null) {
                admin.sendMessage("> New globalUser " + user.mention() + " has a creation time less than 5 hours ago.").queue();
            }
        }
    }

    /**
     * Sends a special message to the globalUser when the join the server.
     *
     * @param user    The globalUser that joined. ({@link UserObject})
     * @param content The Guild that the globalUser joined. ({@link GuildObject})
     * @param event   The event that calls this. ({@link GuildMemberJoinEvent})
     */
    public static void sendWelcomeMessage(GuildObject content, GuildMemberJoinEvent event, UserObject user) {
        String message = content.config.getJoinMessage();
        message = message.replace("<server>", event.getGuild().getName());
        message = message.replace("<globalUser>", event.getUser().getName());
        user.queueDm(message);
    }

    /**
     * Automatically re-mutes users that were muted when they left and their mute timer hasnt timed out.
     *
     * @param user    The globalUser that joined. ({@link UserObject})
     * @param content The Guild that the globalUser joined. ({@link GuildObject})
     * @param event   The event that calls this. ({@link GuildMemberJoinEvent})
     */
    public static void autoReMute(GuildMemberJoinEvent event, GuildObject content, UserObject user) {
        for (MutedUserObject u : content.users.mutedUsers) {
            if (u.getID() == event.getUser().getIdLong()) {
                TextChannel admin = content.getChannelByType(ChannelSetting.ADMIN);
                if (admin != null) {
                    RequestHandler.sendMessage("\\> Looks like " + user.mention() + " has returned, I have muted them again for you.", admin);
                }
                RequestHandler.roleManagement(user, content, content.config.getMutedRoleID(), true);
            }
        }
    }
}
