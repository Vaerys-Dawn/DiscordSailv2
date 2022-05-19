package com.github.vaerys.main;

import com.github.vaerys.enums.FilePaths;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.PatchHandler;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.pogos.Config;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.FileFactory;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {

    static Logger logger;

    public static void main(String[] args) throws UnknownHostException {

        //important, do not move
        PatchHandler.preInitPatches();
        logger = LoggerFactory.getLogger(Main.class);

        logger.info("Starting bot...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info(">>> Running Shutdown Process <<<");
            if (Globals.savingFiles) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
            }
            Globals.saveFiles(true);
        }));


        String token = null;

        // you need to set a token in Token/Token.txt for the bot to run
        try {
            FileHandler.createDirectory(Constants.DIRECTORY_STORAGE);
            FileHandler.createDirectory(Constants.DIRECTORY_GLOBAL_IMAGES);
            FileHandler.createDirectory(Constants.DIRECTORY_COMP);
            FileHandler.createDirectory(Constants.DIRECTORY_BACKUPS);
            FileHandler.createDirectory(Constants.DIRECTORY_TEMP);
            FileHandler.createDirectory(Constants.DIRECTORY_OLD_FILES);
            FileHandler.createDirectory(Constants.DIRECTORY_ERROR);
//            if (!FileHandler.exists(Constants.INFO_TEMPLATE)) {
//                Constants.initInfoTemplate();
//            }

            //load config phase 1
            Config config = FileFactory.create(FilePaths.CONFIG, Config.class);
            GlobalData globalData = FileFactory.create(FilePaths.GLOBAL_DATA, GlobalData.class);

            config = Config.check(config);

            //getting bot token
            try {
                token = FileHandler.readFromFile(Constants.FILE_TOKEN).get(0);
            } catch (IndexOutOfBoundsException e) {
                logger.error("!!!BOT TOKEN NOT VALID PLEASE CHECK \"Storage/Token.txt\" AND UPDATE THE TOKEN!!!");
                System.exit(Constants.EXITCODE_STOP);
            }

            try {
                List<String> pastebinToken = FileHandler.readFromFile(Constants.FILE_PASTEBIN_TOKEN);
                Client.initPastebin(pastebinToken);
            } catch (IndexOutOfBoundsException e) {
                logger.info("No Pastebin Token found.");
            }

            JDA client = Client.createClient(token);

            //load config phase 2
            Globals.initConfig(client, config, globalData);

            if (Globals.creatorID == 153159020528533505L) {
                try {
                    List<String> patreonToken = FileHandler.readFromFile(Constants.FILE_PATREON_TOKEN);
                    Client.initPatreon(patreonToken);
                } catch (IndexOutOfBoundsException e) {
                    logger.info("No Patreon Token found.");
                }
            }

            Globals.validateConfig();
            if (Globals.errorStack != null) {
                logger.error(">\\> Begin Config Error Report <<\n" +
                        "at " + Constants.DIRECTORY_STORAGE + Constants.FILE_CONFIG +
                        "\n" + Globals.errorStack + ">> End Error Report <<");
                System.exit(Constants.EXITCODE_STOP);
            }

            // initialize creatorID if it is completely unset:
            if (config.creatorID == 0) {
                User botOwner = client.retrieveApplicationInfo().complete().getOwner();
                config.creatorID = botOwner.getIdLong();
                Globals.creatorID = config.creatorID;

                logger.info("Default creatorID set to user " + botOwner.getName() + "#" + botOwner.getDiscriminator());

                // save it back out to file.
                config.flushFile();
            }

            //validate config file
            Globals.setVersion();

            //Init Patch system.


            //timed events getSlashCommands
            new TimerHandler();

        } catch (LoginException e) {
            Utility.sendStack(e);
        }
    }

    public static void consoleInput() {
        Scanner scanner = new Scanner(System.in);
        logger.info("Console input initiated.");

        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("!Shutdown")) {
                System.exit(Constants.EXITCODE_STOP);
                return;
            }
            if (Globals.consoleMessageCID != -1) {
                TextChannel channel = Client.getClient().getTextChannelById(Globals.consoleMessageCID);

                if (channel == null) return;
                message = message.replace("#Dawn#", Client.getClient().getUserById(153159020528533505L).getName());
                message = message.replace("teh", "the");
                message = message.replace("Teh", "The");
                logger.info(message);
                if (!message.equals("")) {
                    channel.sendMessage(message).queue();
                }
            }
        }
    }
}
