package Handlers;

import Commands.CommandObject;
import Interfaces.ChannelSetting;
import Interfaces.Command;
import Interfaces.SlashCommand;
import Main.Globals;
import Main.Utility;
import Objects.BlackListObject;
import Objects.OffenderObject;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


/**
 * This Class Handles all of the commands that the bot can run not incluting custom commands.
 */


@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class MessageHandler {

    private FileHandler handler = new FileHandler();

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(String command, String args, CommandObject commandObject) {
        if (Globals.isModifingFiles) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        checkBlacklist(commandObject);
        checkMentionCount(commandObject);
        XpHandler.grantXP(commandObject);
        if (rateLimiting(commandObject)) {
            return;
        }


        if (commandObject.guildConfig.slashCommands) {
            for (SlashCommand s : Globals.getSlashCommands()) {
                if (commandObject.message.getContent().toLowerCase().startsWith(s.call().toLowerCase())) {
                    Utility.sendMessage(s.response(), commandObject.channel);
                    return;
                }
            }
        }
        if (commandObject.guildConfig.artPinning) {
            if (commandObject.guildConfig.autoArtPinning) {
                new ArtHandler(commandObject);
            }
        }
        if (command.toLowerCase().startsWith(commandObject.guildConfig.getPrefixCommand().toLowerCase())) {
            handleCommand(commandObject, command, args);
        }
        if (commandObject.guildConfig.moduleCC) {
            if (command.toLowerCase().startsWith(commandObject.guildConfig.getPrefixCC().toLowerCase())) {
                new CCHandler(command, args, commandObject);
            }
        }
    }

    //Command Handler
    private void handleCommand(CommandObject commandObject, String command, String args) {
        IChannel channel = commandObject.channel;
        GuildConfig guildConfig = commandObject.guildConfig;
        ArrayList<Command> commands = commandObject.commands;
        IDiscordClient client = commandObject.client;
        for (Command c : commands) {
            for (String name : c.names()) {
                if (command.equalsIgnoreCase(guildConfig.getPrefixCommand() + name)) {
                    //command logging

                    if (c.type().equals(Command.TYPE_CREATOR) && !commandObject.authorSID.equalsIgnoreCase(Globals.creatorID)) {
                        return;
                    }

                    logger.debug(Utility.loggingFormatter("COMMAND", command, args, commandObject));

                    if (c.requiresArgs() && args.isEmpty()) {
                        Utility.sendMessage(Utility.getCommandInfo(c, commandObject), channel);
                        return;
                    }

                    //start section
                    if (c.channel() != null && !Utility.canBypass(commandObject.author, commandObject.guild)) {
                        boolean channelFound = false;
                        ArrayList<String> channelMentions = new ArrayList<>();
                        for (ChannelSetting s : commandObject.channelSettings) {
                            if (s.type().equals(c.channel())) {
                                if (s.getIDs(commandObject.guildConfig) != null) {
                                    for (String id : s.getIDs(commandObject.guildConfig)) {
                                        if (id.equals(commandObject.channelSID)) {
                                            channelFound = true;
                                        }
                                        IChannel testPerms = client.getChannelByID(id);
                                        EnumSet<Permissions> userPerms = testPerms.getModifiedPermissions(commandObject.author);
                                        if (userPerms.contains(Permissions.SEND_MESSAGES) && userPerms.contains(Permissions.READ_MESSAGES)) {
                                            channelMentions.add(commandObject.client.getChannelByID(id).mention());
                                        }
                                    }
                                } else {
                                    channelFound = true;
                                }
                            }
                        }
                        if (!channelFound) {
                            if (channelMentions.size() == 0) {
                                Utility.sendMessage("> You do not have access to any channels that you are able to run this command in.",channel);
                            } else if (channelMentions.size() > 1) {
                                Utility.sendMessage("> Command must be performed in any of the following channels: \n" + Utility.listFormatter(channelMentions, true), channel);
                            } else {
                                Utility.sendMessage("> Command must be performed in: " + channelMentions.get(0), channel);
                            }
                            return;
                        }
                    }
                    //end
                    if (c.perms().length != 0 && !Utility.canBypass(commandObject.author, commandObject.guild)) {
                        if (!Utility.testForPerms(c.perms(), commandObject.author, commandObject.guild)) {
                            Utility.sendMessage(commandObject.notAllowed, channel);
                            return;
                        }
                    }
                    if (c.doAdminLogging()) {
                        if (guildConfig.adminLogging) {
                            handleLogging(commandObject, args, c, true);
                        }
                    } else {
                        if (guildConfig.generalLogging) {
                            handleLogging(commandObject, args, c, false);
                        }
                    }
                    Utility.sendMessage(c.execute(args, commandObject), channel);
                }
            }
        }
    }

    private void handleLogging(CommandObject commandObject, String args, Command command, boolean isAdmin) {
        StringBuilder builder = new StringBuilder();
        builder.append("> **@" + commandObject.authorUserName + "** Has Used Command `" + command.names()[0] + "`");
        if (!args.isEmpty()) {
            builder.append(" with args: `" + args + "`");
        }
        builder.append(" in channel " + commandObject.channel.mention() + ".");
        Utility.sendLog(builder.toString(), commandObject.guildConfig, isAdmin);
    }

    private void checkMentionCount(CommandObject command) {
        IMessage message = command.message;
        GuildConfig guildConfig = command.guildConfig;
        IUser author = command.author;
        List<IRole> oldRoles = new ArrayList<>(command.author.getRolesForGuild(command.guild));
        IGuild guild = command.guild;

        if (message.toString().contains("@everyone") || message.toString().contains("@here")) {
            return;
        }
        if (guildConfig.maxMentions) {
            if (message.getMentions().size() > 8) {
                Utility.deleteMessage(message);
                int i = 0;
                boolean offenderFound = false;
                for (OffenderObject o : guildConfig.getRepeatOffenders()) {
                    if (author.getStringID().equals(o.getID())) {
                        guildConfig.addOffence(o.getID());
                        offenderFound = true;
                        i++;
                        if (o.getCount() > Globals.maxWarnings) {
                            Utility.roleManagement(author, guild, guildConfig.getMutedRole().getRoleID(), true);
                            command.client.getDispatcher().dispatch(new UserRoleUpdateEvent(guild, author, oldRoles, command.author.getRolesForGuild(guild)));
                            Utility.sendMessage("> " + author.mention() + " Has been Muted for repeat offences of spamming Mentions.", command.channel);
                        }
                    }
                }
                if (!offenderFound) {
                    guildConfig.addOffender(new OffenderObject(author.getStringID()));
                }
                String response = "> #mentionAdmin# " + author.mention() + "  has attempted to post more than " + guildConfig.getMaxMentionLimit() + " Mentions in a single message.";
                if (guildConfig.getRoleToMention().getRoleID() != null) {
                    response = response.replaceAll("#mentionAdmin#", guild.getRoleByID(guildConfig.getRoleToMention().getRoleID()).mention());
                } else {
                    response = response.replaceAll("#mentionAdmin#", "Admin");
                }
                if (EventHandler.getDoAdminMention(command.guildSID) == 0) {
                    Utility.sendMessage(response, command.channel);
                    EventHandler.setDoAdminMention(command.guildSID, 60);
                }
            }
        }
    }

    private boolean rateLimiting(CommandObject command) {
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, command.author, command.guild, false) ||
                Utility.canBypass(command.author, command.guild, false)) {
            return false;
        }
        if (command.guildConfig.rateLimiting) {
            if (command.guildContent.rateLimit(command.authorSID)) {
                List<IRole> oldRoles = new ArrayList<>(command.author.getRolesForGuild(command.guild));
                command.message.delete();
                Utility.sendDM("Your message was deleted because you are being rate limited.\nMax messages per 10 seconds : " + command.guildConfig.messageLimit, command.authorSID);
                if (command.guildConfig.muteRepeatOffenders) {
                    int rate = command.guildContent.getUserRate(command.authorSID);
                    if (rate - 3 > command.guildConfig.messageLimit) {
                        //mutes users if they abuse it.
                        boolean failed = Utility.roleManagement(command.author, command.guild, command.guildConfig.getMutedRole().getRoleID(), true).get();
                        command.client.getDispatcher().dispatch(new UserRoleUpdateEvent(command.guild, command.author, oldRoles, command.author.getRolesForGuild(command.guild)));
                        if (!failed) {
                            IChannel adminChannel = null;
                            if (command.guildConfig.getChannelIDsByType(Command.CHANNEL_ADMIN) != null) {
                                adminChannel = command.client.getChannelByID(command.guildConfig.getChannelIDsByType(Command.CHANNEL_ADMIN).get(0));
                            }
                            if (adminChannel == null) {
                                adminChannel = command.channel;
                            }
                            Utility.sendDM("You have been muted for abusing the Guild rate limit.", command.authorSID);
                            Utility.sendMessage("> " + command.author.mention() + " has been muted for repetitively abusing Guild rateLimit.", adminChannel);
                        }
                    }
                }
                if (command.guildConfig.deleteLogging) {
                    Utility.sendLog("> **@" + command.authorUserName + "** is being rate limited", command.guildConfig, false);
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    //File handlers

    //BlackListed Phrase Remover
    private void checkBlacklist(CommandObject command) {
        GuildConfig guildConfig = command.guildConfig;
        IMessage message = command.message;
        IGuild guild = command.guild;
        IUser author = command.author;

        if (guildConfig == null) {
            return;
        }
        if (guildConfig.getBlackList() == null) {
            return;
        }
        if (guildConfig.denyInvites) {
            for (BlackListObject bLP : guildConfig.getBlackList()) {
                if (message.toString().toLowerCase().contains(bLP.getPhrase().toLowerCase())) {
                    if (guildConfig.testIsTrusted(author, guild)) {
                        return;
                    }
                    String response = bLP.getReason();
                    if (response.contains("#mentionAdmin#")) {
                        if (guildConfig.getRoleToMention().getRoleID() != null) {
                            response = response.replaceAll("#mentionAdmin#", guild.getRoleByID(guildConfig.getRoleToMention().getRoleID()).mention());
                        } else {
                            response = response.replaceAll("#mentionAdmin#", "");
                        }
                        response = response.replace("#user#", author.mention());
                        if (EventHandler.getDoAdminMention(command.guildSID) == 0) {
                            Utility.sendMessage(response, command.channel);
                            EventHandler.setDoAdminMention(command.guildSID, 60);
                        }
                        Utility.deleteMessage(message);
                    } else {
                        Utility.deleteMessage(message);
                        Utility.sendMessage(response, command.channel);
                    }
                }
            }
        }
    }
}