package com.github.vaerys.handlers.setupStages;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.guildtoggles.modules.ModuleLogging;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.handlers.StringHandler;
import sx.blah.discord.handle.obj.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoggingSetupStage extends SetupHandler {

    private final ArrayList<String> serverLogChannelNames = new ArrayList<>(Arrays.asList(
            "server-log",
            "server-logs",
            "server-logging",
            "serverlog",
            "serverlogs",
            "serverlogging",
            "logging",
            "logs",
            "log"
    ));

    private final ArrayList<String> adminLogChannelNames = new ArrayList<>(Arrays.asList(
            "admin-log",
            "admin-logs",
            "admin-logging",
            "adminlog",
            "adminlogs",
            "adminlogging"
    ));

    @Override
    public String title() {
        return "Logging Module";
    }

    @Override
    public void stepText(CommandObject command) {
        // check if logging is enabled, otherwise skip:
        if (!command.guild.config.moduleLogging) {
            logger.trace("module " + new ModuleLogging().name() + " is not enabled. Skipping step");
            SetupHandler.setSetupStage(command, SetupStage.getNextStage(command.guild.config.setupStage));
        }

        // do some message initialization
        StringHandler output = new StringHandler();

        output.append("We are moving on to setting up modules. The first one that needs to be configured is the Logging module");
        output.append("\nThis module can be used to extend and enhance the basic Audit Log used by Discord.");
        output.append("\n\nFirst part of this, I will need a couple of channels to use. One for general logging, and one for admin logging.");
        output.append("\n*Admin Logging* is messages and events related to server configuration and globalUser moderation.");
        output.append("\n*General Logging* is all of the logging that doesn't fit under admin logging, such as new member joins, message edits and deletes, general command usage, etc.");
        output.append("\n\nI am going to quickly scan your channels to see if I can find potential channels you might want to use...");


        if (!checkForGeneralLog(command)) {
            output.append("\nThere weren't any channels that I could see for general logging, checking if you have any for admin logging...");
            if (!checkForAdminLog(command)) {
                output.append("\nI didn't find any channels that could be used. You'll have to tell me which ones you want me to use");
            } else {
                // will ask for general messageChannel thingers only...

            }
        }
        RequestHandler.sendMessage(output.toString(), command.guildChannel);
    }

    private boolean checkForAdminLog(CommandObject command) {
        return false;
    }

    private boolean checkForGeneralLog(CommandObject command) {
        TextChannel channel = null;
        StringHandler output = new StringHandler();
        for (String s : serverLogChannelNames) {
            List<TextChannel> genLogChannel = command.guild.get().getChannelsByName(s);
            if (genLogChannel.size() == 1) {
                channel = genLogChannel.get(0);
                break;
            } else if (genLogChannel.size() > 1) {
                output.append("I found multiple valid channels...")
                .append("\nPick a messageChannel from the list below: ```");
                String format = "\n%s [%s]";
                for (TextChannel c : genLogChannel) {
                    output.appendFormatted(format, c.getName(), c.getIdLong());
                }
                output.append("\n```")
                .append("Respond with the ID of the messageChannel you want to use.");

                // handle this some special way
            }
        }

        if (channel == null) return false;

        output.append("Do you want to use " + channel.getName() + " as the general log messageChannel?");
        RequestHandler.sendMessage(output.toString(), command.guildChannel);
        return true;
    }

    @Override
    public boolean execute(CommandObject command) {
        return false;
    }

    @Override
    public SetupStage setupStage() {
        return SetupStage.SETUP_LOGGING;
    }
}
