package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.setupStages.ModulesStage;
import com.github.vaerys.handlers.setupStages.SettingsStage;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;

/**
 * Class that implements a setup system for the bot's modules and settings.
 */
public abstract class SetupHandler {

    public static final int SETUP_UNSET = -1;
    public static final int SETUP_COMPLETE = Short.MAX_VALUE;

    public static final Logger logger = LoggerFactory.getLogger(SetupHandler.class);
    private static final HashMap<Integer, SetupHandler> configStages = new HashMap<>();

    /**
     * Nicely checks whether or not setup is currently being run.
     *
     * @param guild the guild that the setup is being run on.
     * @return True if the setup is currently active, false otherwise.
     */
    public static boolean isRunningSetup(GuildObject guild) {
        return guild.config.setupStage != SETUP_UNSET &&
                guild.config.setupStage != configStages.size();
    }

    /**
     * Handles responding to messages from the configuring user.
     *
     * @param command The {@link CommandObject} containing a valid user.
     * @param args    the message that was received by the handler.
     * @return false if the {@link MessageHandler} should continue, otherwise true.
     */
    public static boolean handleMessage(CommandObject command, String args) {

        for (GuildObject g : Globals.getGuilds()) {
            if ((isRunningSetup(g)) && command.user.longID == g.config.setupUser) {

                //populate a commandObject with the guild the user is setting up,
                //letting us play with these commands in DMs.
                command.setGuild(g.get());

                //handle responding to user input:
                if (!configStages.get(command.guild.config.setupStage).execute(command)) return true;
                if (handleCommand(command, args)) return true;
            }
        }
        return false;
    }

    /**
     * Handle commands in the Setup Mode DMs.
     *
     * @param command The {@link CommandObject} associated with this setup.
     * @param args    The contents of the message sent.
     * @return false if command isn't run, otherwise true.
     */
    private static boolean handleCommand(CommandObject command, String args) {
        List<Command> commands = Globals.getSetupCommands();
        IChannel currentChannel = command.channel.get();
        String commandArgs;
        for (Command c : commands) {
            if (c.isCall(args, command)) {
                commandArgs = c.getArgs(args, command);
                //log command
                MessageHandler.handleLogging(command, c, commandArgs);
                if (c.requiresArgs && (commandArgs == null || commandArgs.isEmpty())) {

                    RequestHandler.sendMessage(Utility.getCommandInfo(c, command), currentChannel);
                    return true;
                }
                RequestBuffer.request(() -> command.channel.get().setTypingStatus(true)).get();
                String response = c.execute(commandArgs, command);
                RequestHandler.sendMessage(response, currentChannel);
                RequestBuffer.request(() -> command.channel.get().setTypingStatus(false)).get();
                return true;
            }
        }
        return false;
    }

    public static void setSetupStage(CommandObject command, int stage) {
        GuildConfig config = command.guild.config;

        config.setupStage = stage;
        config.setupUser = stage == SETUP_UNSET ? -1 : command.user.longID;

        // send the title and step text to channel
        if (stage != SETUP_UNSET && stage != SETUP_COMPLETE) {
            //command.setChannel(command.user.getDmChannel());
            // get stage:
            SetupHandler currentStage = configStages.get(config.setupStage);
            String titleText = "**__Step " + (stage + 1) + ": " + currentStage.title() + "__**";
            RequestHandler.sendMessage(titleText, command.user.getDmChannel());
            // it is expected that you send the message yourself;
            currentStage.stepText(command);
        }
    }

    /**
     * Get the number of stages currently available to the setup.
     *
     * @return The size of the configStages map.
     */
    public static int getNumSetupSteps() {
        return configStages.size();
    }

    /**
     * Add a new object to the setup process. {@link SetupHandler#setupStage()} cannot be duplicate.
     *
     * @param setupHandler The object that will be added.
     */
    private static void addStage(SetupHandler setupHandler) {
        if (configStages.containsKey(setupHandler.setupStage())) {
            Globals.addToErrorStack("   > DuplicateKeyValue: setupHandler cannot contain duplicate keys.");
        }
        configStages.put(setupHandler.setupStage(), setupHandler);
    }

    public static void initStages() {
        addStage(new ModulesStage());
        addStage(new SettingsStage());
    }

    /**
     * Title of the setup stage being run.
     *
     * @return The text to display
     */
    public abstract String title();

    /**
     * The initial text to be displayed for the step currently running.<br />
     * Send a message to the user with {@link RequestHandler#sendMessage(String, IChannel)}
     *
     * @param command A {@link CommandObject} passed to each setup instance.
     * @return A string value representing the text output.
     * @see RequestHandler#sendMessage(String, IChannel)
     * @see com.github.vaerys.objects.XEmbedBuilder
     */
    public abstract void stepText(CommandObject command);

    /**
     * This method is called on a setup stage whenever the user responds in a DM. If you need to reply to the user,
     * use {@link RequestHandler#sendMessage(String, com.github.vaerys.masterobjects.ChannelObject)}.
     *
     * @param command A {@link CommandObject} passed to each setup instance.
     * @return Whether or not the response should also allow commands to run.
     */
    public abstract boolean execute(CommandObject command);

    /**
     * Order of this setup object in the map. Cannot have children with duplicate values.
     *
     * @return A unique number representing the ordering of the step.
     */
    public abstract int setupStage();
}
