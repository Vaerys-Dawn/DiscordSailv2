package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.OffenderObject;
import com.github.vaerys.pogos.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;


/**
 * This Class Handles all of the commands that the bot can run not including custom commands.
 */


@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class MessageHandler {

    private FileHandler handler = new FileHandler();

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(String args, CommandObject command, boolean isPrivate) {
        if (!isPrivate) {
            if (checkForInvites(command)) return;
            if (checkMentionCount(command)) return;
            if (rateLimiting(command)) return;
            XpHandler.grantXP(command);
            if (command.guild.config.artPinning) {
                if (command.guild.config.autoArtPinning) {
                    ArtHandler.pinMessage(command);
                }
            }
            if (command.guild.config.moduleCC) {
                if (args.toLowerCase().startsWith(command.guild.config.getPrefixCC().toLowerCase())) {
                    new CCHandler(args, command);
                }
            }
        }
        // do the command stuff
        if (handleCommand(command, args)) return;
        if (isPrivate) {
            new DMHandler(command);
        }
    }

    //Command Handler
    private boolean handleCommand(CommandObject command, String args) {
        List<Command> commands = new ArrayList<>(command.guild.commands);
        IChannel currentChannel = command.channel.get();
        String commandArgs;
        for (Command c : commands) {
            if (c.isCall(args, command)) {
                commandArgs = c.getArgs(args, command);
                //log command
                logger.debug(Utility.loggingFormatter(command, "COMMAND"));
                //test if user has permissions
                if (!Utility.testForPerms(command, c.perms())) {
                    Utility.sendMessage(command.user.notAllowed, currentChannel);
                    return true;
                }
                //check if it is a valid channel
                if (!currentChannel.isPrivate()) {
                    if (c.channel() != null && !Utility.canBypass(command.user.get(), command.guild.get())) {
                        List<IChannel> channels = command.guild.config.getChannelsByType(c.channel(), command.guild);
                        if (!channels.contains(command.channel.get()) && channels.size() != 0) {
                            List<String> list = Utility.getChannelMentions(command.user.getVisibleChannels(channels));
                            if (list.size() == 0) {
                                Utility.sendMessage("> You do not have access to any channels that you are able to run this command in.", currentChannel);
                            } else if (list.size() > 1) {
                                Utility.sendMessage("> Command must be performed in any of the following channels: \n" + Utility.listFormatter(list, true), currentChannel);
                            } else {
                                Utility.sendMessage("> Command must be performed in: " + list.get(0), currentChannel);
                            }
                            return true;
                        }
                    }
                }
                if (c.requiresArgs() && (commandArgs == null || commandArgs.isEmpty())) {

                    Utility.sendMessage(Utility.getCommandInfo(c, command), currentChannel);
                    return true;
                }
                //command logging
                handleLogging(command, c, commandArgs);
//                if (!command.channel.get().getTypingStatus()) {
//                    command.channel.get().toggleTypingStatus();
//                }
                String response = c.execute(commandArgs, command);
                Utility.sendMessage(response, currentChannel);
                return true;
            }
        }
        return false;
    }

    public static void handleLogging(CommandObject commandObject, Command command, String args) {
        if (!command.doAdminLogging() && !commandObject.guild.config.adminLogging) {
            return;
        } else if (!commandObject.guild.config.generalLogging) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("> **@" + commandObject.user.username + "** Has Used Command `" + command.getCommand(commandObject) + "`");
        if (args != null && !args.isEmpty()) {
            builder.append(" with args: `" + args + "`");
        }
        builder.append(" in channel " + commandObject.channel.get().mention() + ".");
        Utility.sendLog(builder.toString(), commandObject.guild, command.doAdminLogging());
    }

    private boolean checkMentionCount(CommandObject command) {
        IMessage message = command.message.get();
        GuildConfig guildconfig = command.guild.config;
        IUser author = command.user.get();
        List<IRole> oldRoles = command.user.roles;
        IGuild guild = command.guild.get();

        if (message.toString().contains("@everyone") || message.toString().contains("@here")) {
            return false;
        }
        if (guildconfig.maxMentions) {
            if (message.getMentions().size() > 8) {
                Utility.deleteMessage(message);
                int i = 0;
                boolean offenderFound = false;
                for (OffenderObject o : guildconfig.getRepeatOffenders()) {
                    if (author.getLongID() == o.getID()) {
                        guildconfig.addOffence(o.getID());
                        offenderFound = true;
                        i++;
                        if (o.getCount() >= Globals.maxWarnings) {
                            Utility.roleManagement(author, guild, guildconfig.getMutedRoleID(), true);
                            command.client.get().getDispatcher().dispatch(new UserRoleUpdateEvent(guild, author, oldRoles, command.user.get().getRolesForGuild(guild)));
                            Utility.sendMessage("> " + author.mention() + " Has been Muted for repeat offences of spamming Mentions.", command.channel.get());
                        }
                    }
                }
                if (!offenderFound) {
                    guildconfig.addOffender(new OffenderObject(author.getLongID()));
                }
                String response = "> #mentionAdmin# " + author.mention() + "  has attempted to post more than " + guildconfig.getMaxMentionLimit() + " Mentions in a single message.";
                IRole roleToMention = command.guild.getRoleByID(guildconfig.getRoleToMentionID());
                if (roleToMention != null) {
                    response = response.replaceAll("#mentionAdmin#", roleToMention.mention());
                } else {
                    response = response.replaceAll("#mentionAdmin#", "Admin");
                }
                Utility.sendMessage(response, command.channel.get());
                return true;
            }
        }
        return false;
    }

    private boolean rateLimiting(CommandObject command) {
        //make sure that the rate limiting should actually happen
        if (Utility.testForPerms(command, Permissions.MANAGE_MESSAGES)) return false;
        if (!command.guild.config.rateLimiting) return false;
        if (!command.guild.rateLimit(command.user.longID)) return false;

        //send a dm to let the user know that they are being rate limited
        IChannel userDMs = command.user.get().getOrCreatePMChannel();
        if (userDMs != null) {
            Utility.sendMessage("Your message was deleted because you are being rate limited.\nMax messages per 10 seconds : " + command.guild.config.messageLimit, userDMs);
        }

        //user is now being rate limited
        Utility.deleteMessage(command.message.get());
        if (command.guild.config.deleteLogging) {
            Utility.sendLog("> **@" + command.user.username + "** is being rate limited", command.guild, false);
        }

        //if mute continue
        if (!command.guild.config.muteRepeatOffenders) return true;
        IRole muteRole = command.guild.getRoleByID(command.guild.config.getMutedRoleID());
        if (muteRole == null) return true;

        //if they are over the rate limit start muting
        int rate = command.guild.getUserRate(command.user.longID);
        if (rate - 3 < command.guild.config.messageLimit) return true;

        //this mutes them
        RequestBuffer.request(() -> command.user.get().addRole(muteRole));

        //setup of the admin channel
        IChannel adminChannel = null;
        List<IChannel> adminChannels = command.guild.config.getChannelsByType(Command.CHANNEL_ADMIN, command.guild);
        if (adminChannels.size() != 0) adminChannel = adminChannels.get(0);
        if (adminChannel == null) adminChannel = command.channel.get();

        //sends the response if they got muted
        Utility.sendDM("You have been muted for abusing the Guild rate limit.", command.user.longID);
        Utility.sendMessage("> " + command.user.get().mention() + " has been muted for repetitively abusing Guild rateLimit.", adminChannel);
        return true;
    }

    //File com.github.vaerys.handlers

    //BlackListed Phrase Remover
    private boolean checkForInvites(CommandObject command) {
        GuildConfig guildconfig = command.guild.config;
        IMessage message = command.message.get();
        IGuild guild = command.guild.get();
        IUser author = command.user.get();
        List<String> inviteformats = new ArrayList<String>() {{
            add("discord.gg");
            add("discordapp.com/Invite/");
        }};

        boolean inviteFound = false;
        if (guildconfig.denyInvites) {
            for (String s : inviteformats) {
                if (message.toString().toLowerCase().contains(s.toLowerCase())) {
                    if (guildconfig.testIsTrusted(author, guild)) {
                        return false;
                    }
                    inviteFound = true;
                }
            }
        }
        if (inviteFound) {
            String response = "> Please do not post Instant Invites.";
            Utility.deleteMessage(message);
            Utility.sendMessage(response, command.channel.get());
        }
        return inviteFound;
    }
}
