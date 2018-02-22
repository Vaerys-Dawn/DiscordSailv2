package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.templates.Command;
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
//                command.setGuild(g.get());
//                cont(command);
//                return true;
//            }
//        }
//        return false;
//    }

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
                if (!Utility.testForPerms(command, c.perms)) {
                    RequestHandler.sendMessage(command.user.notAllowed, currentChannel);
                    return true;
                }
                //check if it is a valid channel
                if (!currentChannel.isPrivate()) {
                    if (c.channel != null && !Utility.testForPerms(command, Permissions.MANAGE_CHANNELS)) {
                        List<IChannel> channels = command.guild.getChannelsByType(c.channel);
                        if (channels.size() != 0 && !channels.contains(command.channel.get())) {
                            List<String> list = Utility.getChannelMentions(command.user.getVisibleChannels(channels));
                            RequestHandler.sendMessage(Utility.getChannelMessage(list), command.channel.get());
                            return true;
                        }
                    }
                }
                if (c.requiresArgs && (commandArgs == null || commandArgs.isEmpty())) {

                    RequestHandler.sendMessage(Utility.getCommandInfo(c, command), currentChannel);
                    return true;
                }
                //command logging
                handleLogging(command, c, commandArgs);
                RequestBuffer.request(() -> command.channel.get().setTypingStatus(true)).get();
//                if (!command.channel.get().getTypingStatus()) {
//                    command.channel.get().toggleTypingStatus();
//                }
                String response = c.execute(commandArgs, command);
                RequestHandler.sendMessage(response, currentChannel);
                RequestBuffer.request(() -> command.channel.get().setTypingStatus(false)).get();
                return true;
            }
        }
        return false;
    }

    protected static void handleLogging(CommandObject commandObject, Command command, String args) {
        if (!command.doAdminLogging && !commandObject.guild.config.adminLogging) {
            return;
        } else if (!commandObject.guild.config.generalLogging) {
            return;
        }
        StringHandler builder = new StringHandler("> **@").append(commandObject.user.username).append("** Has Used Command `").append(command.getCommand(commandObject)).append("`");
        if (args != null && !args.isEmpty()) {
            builder.append(" with args: `").append(args).append("`");
        }
        builder.append(" in channel ").append(commandObject.channel.get().mention()).append(".");
        Utility.sendLog(builder.toString(), commandObject.guild, command.doAdminLogging);
    }
}
