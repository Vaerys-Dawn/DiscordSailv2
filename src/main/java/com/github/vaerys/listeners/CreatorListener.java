package com.github.vaerys.listeners;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.commands.creator.Restart;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.MessageHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CreatorListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        restart(event);
        creatorCommands(event);
    }

    public void creatorCommands(MessageReceivedEvent event) {
        if (event.getAuthor().getIdLong() != Globals.creatorID) return;
        List<Command> commands = new ArrayList<>(CommandList.getCreatorCommands());
        String message = event.getMessage().getContentRaw();
        if (event.isFromGuild()) {
            CommandObject command = new CommandObject(event.getMessage(), event.getGuild());
            for (Command c : commands) {
                if (c.isCall(message, command)) {
                    String args = c.getArgs(message);
                    command.guildChannel.queueMessage(c.execute(args, command));
                    MessageHandler.handleLogging(command, c, args);
                    return;
                }
            }
        } else {
            DmCommandObject command = new DmCommandObject(event.getMessage(), event.getChannel(), event.getAuthor());
            for (Command c : commands) {
                if (c.isCall(message, command)) {
                    String args = c.getArgs(message);
                    command.messageChannel.queueMessage(c.executeDm(args, command));
                    return;
                }
            }
        }
    }

    public void restart(MessageReceivedEvent event) {
        DmCommandObject command = new DmCommandObject(event.getMessage(), event.getChannel(), event.getAuthor());
        Restart restart = new Restart();
        if (restart.isCall(event.getMessage().getContentRaw(), command)) {
            List<Long> auth = new LinkedList<>();
            try {
                List<String> toParse = FileHandler.readFromFile(Constants.FILE_AUTH_TO_RESTART);
                if (toParse == null) return;
                auth.addAll(toParse.stream().map(Long::parseUnsignedLong).collect(Collectors.toList()));
            } catch (NumberFormatException e) {
                // do nothing
            }
            auth.add(Globals.creatorID);
            if (auth.contains(event.getAuthor().getIdLong())) {
                restart.executeDm("", command);
                return;
            }
        }
    }
}
