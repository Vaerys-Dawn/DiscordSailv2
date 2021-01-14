package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * This Class Handles all of the commands that the bot can run not including custom commands.
 */


@SuppressWarnings({"StringConcatenationInsideStringBufferAppend"})
// gdi dawn <3
public class MessageHandler {

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public static void handleMessage(String args, CommandObject command) {

        //Set Console Response Channel.
        if (command.user.equals(command.client.creator)) {
            Globals.consoleMessageCID = command.guildChannel.longID;
        }

        command.guild.handleWelcome(command);
        if (SpamHandler.checkForInvites(command)) return;
        if (SpamHandler.checkMentionCount(command)) return;
        if (SpamHandler.commandBlacklisting(command)) return;
        if (SpamHandler.rateLimiting(command)) return;
        if (SpamHandler.catchWalls(command)) return;
        if (checkMuteAppeals(command)) return;
        if (checkForSpammer(command)) return;
        // check for role mentions:
        if (!GuildHandler.testForPerms(command, command.guildChannel.get(), Permission.MESSAGE_MENTION_EVERYONE)) {
            // sanitize @everyone and @here mentions.
            args = args.replaceAll("(?i)@(everyone|here)", "REDACTED");
            for (Role r : command.message.get().getMentionedRoles()) {
                args = args.replaceAll(r.getAsMention(), r.getName());
            }
        }
        PixelHandler.grantXP(command);
        if (command.guild.config.artPinning) {
            if (command.guild.config.autoArtPinning) {
                ArtHandler.pinMessage(command, command.user, command.botUser);
            }
        }
        if (command.guild.config.moduleAdminCC) {
            if (args.toLowerCase().startsWith(command.guild.config.getPrefixAdminCC().toLowerCase())) {
                CCHandler.handleAdminCC(args, command);
            }

        }
        if (command.guild.config.moduleCC) {
            if (args.toLowerCase().startsWith(command.guild.config.getPrefixCC().toLowerCase())) {
                CCHandler.handleCommand(args, command);
            }
        }

        // do the command stuff
        if (handleCommand(command, args)) return;
    }

    public static void handleDmMessage(String args, DmCommandObject command) {
        if (handleDmCommand(command, args)) return;
        new DMHandler(command);
    }

    private static boolean checkForSpammer(CommandObject command) {
        boolean found = command.message.getContent().matches(".*http.*QYHhZv.*");
        if (!found) return false;
        logger.info(String.format("Handling spammer removal for guild with id: %d, in messageChannel %d", command.guild.longID, command.guildChannel.longID));
        if (command.guild.getMutedRole() != null) {
            if (command.guild.users.isUserMuted(command.user.getMember())) return true;
            command.guild.users.muteUser(command, -1);
        }
        command.message.delete();
        return true;
    }

    private static boolean checkMuteAppeals(CommandObject command) {
        if (!command.guild.config.moduleModMute) return false;
        Role muted = command.guild.getMutedRole();
        if (muted == null) return false;
        if (!command.guild.getChannelsByType(ChannelSetting.MUTE_APPEALS).contains(command.guildChannel.get())) return false;
        if (GuildHandler.canBypass(command)) return false;
        if (command.user.roles.contains(muted)) return true;
        return false;
    }

    protected static void handleLogging(CommandObject commandObject, Command command, String args) {
        if (!commandObject.guild.config.moduleLogging) return;
        if (command.doAdminLogging && !commandObject.guild.config.adminLogging) {
            return;
        } else if (!command.doAdminLogging && !commandObject.guild.config.generalLogging) {
            return;
        }
        StringHandler builder = new StringHandler("> **@").append(commandObject.user.username).append("** Has Used Command `").append(command.getCommand(commandObject)).append("`");
        if (args != null && !args.isEmpty()) {

            if (args.length() > 1800) {
                args = StringUtils.truncate(args, 1800);
                args += "...";
            }

            builder.append(" with args: `").append(args).append("`");
        }
        builder.append(" in messageChannel ").append(commandObject.guildChannel.get().getAsMention()).append(".");
        LoggingHandler.sendLog(builder.toString(), commandObject.guild, command.doAdminLogging);
    }

    private static boolean handleDmCommand(DmCommandObject command, String args) {
        List<Command> commands = CommandList.getCommands(true);
        String commandArgs;
        for (Command c : commands) {
            if (c.isCall(args, command)) {
                commandArgs = c.getArgs(args);

                if (c.requiresArgs && (commandArgs == null || commandArgs.isEmpty())) {
                    command.messageChannel.queueMessage(c.missingArgsDm());
                    return true;
                }
                if (c.sendTyping) {
                    command.messageChannel.getMessageChannel().sendTyping();
                }
                String response = c.executeDm(commandArgs, command);
                if (response != null && !response.isEmpty()){
                    command.messageChannel.queueMessage(response);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean handleCommand(CommandObject command, String args) {
        List<Command> commands = new ArrayList<>(command.guild.commands);
        String commandArgs;
        for (Command c : commands) {
            if (c.isCall(args, command)) {
                commandArgs = c.getArgs(args);
                //log command
                command.guild.sendDebugLog(command, "COMMAND", c.getCommand(command), c.getArgs(args));
                //test if globalUser has permissions
                if (!GuildHandler.testForPerms(command, c.perms)) {
                    command.guildChannel.queueMessage(command.user.notAllowed);
                    return true;
                }
                //check if it is a valid messageChannel
                if (c.channel != null && !GuildHandler.testForPerms(command, Permission.MANAGE_CHANNEL)) {
                    List<TextChannel> channels = command.guild.getChannelsByType(c.channel);
                    if (channels.size() != 0 && !channels.contains(command.guildChannel.get())) {
                        List<TextChannel> visibleChannels = command.user.getVisibleChannels(channels);
                        command.guildChannel.queueMessage(Utility.getChannelMessage(visibleChannels));
                        return true;
                    }
                }

                if (c.requiresArgs && (commandArgs == null || commandArgs.isEmpty())) {
                    command.guildChannel.queueMessage(c.missingArgs(command));
                    return true;
                }
                //command logging
                handleLogging(command, c, commandArgs);
                if (c.sendTyping) {
                    command.guildChannel.get().sendTyping();
                }
                String response = c.execute(commandArgs, command);
                if (response != null && !response.isEmpty()){
                    command.guildChannel.queueMessage(response);
                }
                return true;
            }
        }
        return false;
    }
}
