package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.OffenderObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
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
            if (SpamHandler.checkForInvites(command)) return;
            if (SpamHandler.checkMentionCount(command)) return;
            if (SpamHandler.rateLimiting(command)) return;
            if (SpamHandler.catchWalls(command)) return;
            XpHandler.grantXP(command);
            if (command.guild.config.artPinning) {
                if (command.guild.config.autoArtPinning) {
                    ArtHandler.pinMessage(command);
                }
            }
            if (command.guild.config.moduleCC) {
                if (args.toLowerCase().startsWith(command.guild.config.getPrefixCC().toLowerCase())) {
                    CCHandler.handleCommand(args, command);
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
                logger.debug(Utility.loggingFormatter(command, "COMMAND", c.getCommand(command), c.getArgs(args, command)));
                //test if user has permissions
                if (!Utility.testForPerms(command, c.perms())) {
                    Utility.sendMessage(command.user.notAllowed, currentChannel);
                    return true;
                }
                //check if it is a valid channel
                if (!currentChannel.isPrivate()) {
                    if (c.channel() != null && !Utility.canBypass(command.user.get(), command.guild.get())) {
                        List<IChannel> channels = command.guild.config.getChannelsByType(c.channel(), command.guild);
                        if (channels.size() != 0 && !channels.contains(command.channel.get())) {
                            List<String> list = Utility.getChannelMentions(command.user.getVisibleChannels(channels));
                            Utility.sendMessage(Utility.getChannelMessage(list),command.channel.get());
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
                if (!command.channel.get().getTypingStatus()) {
                    command.channel.get().toggleTypingStatus();
                }
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
}
