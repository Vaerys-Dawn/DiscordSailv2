package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.Restart;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CreatorHandler {

    @EventSubscriber
    public void creatorCommands(MessageReceivedEvent event) {
        if (event.getAuthor().getLongID() != Globals.creatorID) return;
        List<Command> commands;
        CommandObject command = new CommandObject(event.getMessage());
        if (event.getChannel().isPrivate()) {
            commands = new ArrayList<>(Globals.getCreatorCommands(true));
        } else {
            commands = new ArrayList<>(Globals.getCreatorCommands(false));
        }
        String message = event.getMessage().getContent();
        for (Command c : commands) {
            if (c.isCall(message, command)) {
                String args = c.getArgs(message, command);
                RequestHandler.sendMessage(c.execute(args, command), command.channel.get());

                MessageHandler.handleLogging(command, c, args);
                return;
            }
        }
    }

    @EventSubscriber
    public void restart(MessageReceivedEvent event) {
        CommandObject command = new CommandObject(event.getMessage());
        Restart restart = new Restart();
        if (restart.isCall(event.getMessage().getContent(), command)) {
            List<Long> auth = new LinkedList<>();
            try {
                List<String> toParse = FileHandler.readFromFile(Constants.FILE_AUTH_TO_RESTART);
                auth.addAll(toParse.stream().map(Long::parseUnsignedLong).collect(Collectors.toList()));
            } catch (NumberFormatException e) {
                // do nothing
            }
            auth.add(Globals.creatorID);
            if (auth.contains(event.getAuthor().getLongID())) {
                restart.execute("", command);
                return;
            }
        }
    }
}
