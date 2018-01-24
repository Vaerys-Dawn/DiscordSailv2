package com.github.vaerys.main;

import com.github.vaerys.handlers.*;
import com.github.vaerys.pogos.Config;
import com.github.vaerys.pogos.GlobalData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info(">>> Running Shutdown Process <<<");
                if (Globals.savingFiles) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Globals.saveFiles(true);
            }
        });


        String token = null;

        // you need to set a token in Token/Token.txt for the bot to run
        try {
            Discord4J.disableAudio();
            FileHandler.createDirectory(Constants.DIRECTORY_STORAGE);
            FileHandler.createDirectory(Constants.DIRECTORY_GLOBAL_IMAGES);
            FileHandler.createDirectory(Constants.DIRECTORY_COMP);
            FileHandler.createDirectory(Constants.DIRECTORY_BACKUPS);
            FileHandler.createDirectory(Constants.DIRECTORY_TEMP);
            FileHandler.createDirectory(Constants.DIRECTORY_OLD_FILES);
            FileHandler.createDirectory(Constants.DIRECTORY_ERROR);

            //load config phase 1
            Config config = (Config) Config.create(Constants.FILE_CONFIG, new Config());
            GlobalData globalData = (GlobalData) GlobalData.create(Constants.FILE_GLOBAL_DATA, new GlobalData());

            config = Config.check(config);

            //getting bot token
            try {
                token = FileHandler.readFromFile(Constants.FILE_TOKEN).get(0);
            } catch (IndexOutOfBoundsException e) {
                logger.error("!!!BOT TOKEN NOT VALID PLEASE CHECK \"Storage/Token.txt\" AND UPDATE THE TOKEN!!!");
                System.exit(Constants.EXITCODE_CONF_ERROR);
            }

            try {
                List<String> pastebinToken = FileHandler.readFromFile(Constants.FILE_PASTEBIN_TOKEN);
                Client.initPastebin(pastebinToken);
            } catch (IndexOutOfBoundsException e) {
                logger.info("No Pastebin Token found.");
            }

            try {
                List<String> patreonToken = FileHandler.readFromFile(Constants.FILE_PATREON_TOKEN);
                Client.initPatreon(patreonToken);
            } catch (IndexOutOfBoundsException e) {
                logger.info("No Patreon Token found.");
            }


            //stuff that i cant get to work because reasons, ignore completely

//            try{
//                List<String> richPresesnce = FileHandler.readFromFile(Constants.FILE_RPC_TOKEN);
//                Client.initRichPresence(richPresesnce);
//            }catch (IndexOutOfBoundsException e){
//                logger.info("Rich presence information missing.");
//            }

//            try {
//                List<String> imgurToken = FileHandler.readFromFile(Constants.FILE_IMGUR_TOKEN);
//                Client.initImgur(imgurToken);
//            } catch (IndexOutOfBoundsException e) {
//                logger.info("No Patreon Token found.");
//            }


            IDiscordClient client = Client.createClient(token, false);

            //load config phase 2
            Globals.initConfig(client, config, globalData);


            ThreadGroup group = new ThreadGroup("GuildCreateGroup");
            final int[] count = new int[]{0};

            //login + register listener.
            client.login();
            ExecutorService guildService = new ThreadPoolExecutor(2, 50, 1,
                    TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000),
                    r -> new Thread(group, r, group.getName() + "-Thread-" + ++count[0]));
            ExecutorService commandService = new ThreadPoolExecutor(2, 50, 1,
                    TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000),
                    r -> new Thread(group, r, group.getName() + "-Thread-" + ++count[0]));
            ExecutorService creatorService = new ThreadPoolExecutor(2, 50, 1,
                    TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000),
                    r -> new Thread(group, r, group.getName() + "-Thread-" + ++count[0]));

            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(guildService, new GuildCreateListener());
            dispatcher.registerListener(commandService, new AnnotationListener());
            dispatcher.registerListener(creatorService, new CreatorHandler());
            dispatcher.registerTemporaryListener(new InitEvent());


            //Init Patch system.


            //timed events getSlashCommands
            new TimerHandler();


        } catch (DiscordException ex) {
            logger.error(ex.getErrorMessage());
        } catch (RateLimitException e) {
            Utility.sendStack(e);
        }
    }

    public static void consoleInput() {
        Scanner scanner = new Scanner(System.in);
        logger.info("Console input initiated.");

        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("!Shutdown")) {
                System.exit(Constants.EXITCODE_NORMAL);
                return;
            }
            if (Globals.consoleMessageCID != -1) {
                IChannel channel = Globals.getClient().getChannelByID(Globals.consoleMessageCID);

                message = message.replace("#Dawn#", Globals.getClient().getUserByID(153159020528533505L).getName());
                message = message.replace("teh", "the");
                message = message.replace("Teh", "The");
//            System.out.println(message);
                if (!message.equals("")) {
                    RequestHandler.sendMessage(message, channel);
                }
            }
        }
    }
}
