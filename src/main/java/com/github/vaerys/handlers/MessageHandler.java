package com.github.vaerys.handlers;

import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;


/**
 * This Class Handles all of the commands that the bot can run not including custom commands.
 */


@SuppressWarnings({"StringConcatenationInsideStringBufferAppend"})
// gdi dawn <3
public class MessageHandler {

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);


    public MessageHandler(String args, CommandObject command, boolean isPrivate) {
        if (!isPrivate) {
            if (SpamHandler.checkForInvites(command)) return;
            if (SpamHandler.checkMentionCount(command)) return;
            if (SpamHandler.commandBlacklisting(command)) return;
            if (SpamHandler.rateLimiting(command)) return;
            if (SpamHandler.catchWalls(command)) return;
            // check for role mentions:
            if (!GuildHandler.testForPerms(command, command.channel.get(), Permissions.MENTION_EVERYONE)) {
                // sanitize @everyone mentions.
                args = args.replaceAll(command.guild.get().getEveryoneRole().mention(), "REDACTED");
                // sanitize @here mentions.
                args = args.replaceAll("@here", "REDACTED");
            }
            PixelHandler.grantXP(command);
            if (command.guild.config.artPinning) {
                if (command.guild.config.autoArtPinning) {
                    ArtHandler.pinMessage(command, command.message.author, command.client.bot);
                }
            }
            if (command.guild.config.moduleCC) {
                if (args.toLowerCase().startsWith(command.guild.config.getPrefixCC().toLowerCase())) {
                    CCHandler.handleCommand(args, command);
                }
            }
            if (command.guild.config.moduleAdminCC) {
                if (args.toLowerCase().startsWith(command.guild.config.getPrefixAdminCC().toLowerCase())) {
                    CCHandler.handleAdminCC(args, command);
                }
            }
        } else {
            // check if in setup mode otherwise do nothing.
            if (SetupHandler.handleMessage(command, args)) return;
        }
        // do the command stuff
        if (handleCommand(command, args)) return;
        if (isPrivate) {
            new DMHandler(command);
        }
    }

//    public static boolean isActive(boolean isPrivate, CommandObject command) {
//        if (!isPrivate) return false;
//        for (GuildObject g : Globals.getGuilds()) {
//            if (g.config.getSatrtupState().equals(BEGIN_SETUP) && command.user.longID == g.getSetupUser()) {
//                command.setGuild(g.getAllToggles());
//                cont(command);
//                return true;
//            }
//        }
//        return false;
//    }

    protected static void handleLogging(CommandObject commandObject, Command command, String args) {
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
        if (commandObject.channel.get().isPrivate()) {
            builder.append(" in their DMs.");
        } else {
            builder.append(" in channel ").append(commandObject.channel.get().mention()).append(".");
        }
        Utility.sendLog(builder.toString(), commandObject.guild, command.doAdminLogging);
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
                command.guild.sendDebugLog(command, "COMMAND", c.getCommand(command), c.getArgs(args, command));
                //test if user has permissions
                if (!GuildHandler.testForPerms(command, c.perms)) {
                    RequestHandler.sendMessage(command.user.notAllowed, currentChannel);
                    return true;
                }
                //check if it is a valid channel
                if (!currentChannel.isPrivate()) {
                    if (c.channel != null && !GuildHandler.testForPerms(command, Permissions.MANAGE_CHANNELS)) {
                        List<IChannel> channels = command.guild.getChannelsByType(c.channel);
                        if (channels.size() != 0 && !channels.contains(command.channel.get())) {
                            List<String> list = Utility.getChannelMentions(command.user.getVisibleChannels(channels));
                            RequestHandler.sendMessage(Utility.getChannelMessage(list), command.channel.get());
                            return true;
                        }
                    }
                }
                if (c.requiresArgs && (commandArgs == null || commandArgs.isEmpty())) {

                    RequestHandler.sendMessage(c.missingArgs(command), currentChannel);
                    return true;
                }
                //command logging
                handleLogging(command, c, commandArgs);
                RequestBuffer.request(() -> command.channel.get().setTypingStatus(true)).get();
//                if (!command.channel.getAllToggles().getTypingStatus()) {
//                    command.channel.getAllToggles().toggleTypingStatus();
//                }
                String response = c.execute(commandArgs, command);
                RequestHandler.sendMessage(response, currentChannel);
                RequestBuffer.request(() -> command.channel.get().setTypingStatus(false)).get();
                return true;
            }
        }
        return false;
    }
}
