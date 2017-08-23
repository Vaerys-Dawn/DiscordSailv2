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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {


    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Starting Program...");

        //important, do not move
        PatchHandler.globalDataPatch();

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
                Globals.saveFiles();
                Globals.shuttingDown = true;
            }
        });


        String token;
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
            if (!Files.exists(Paths.get(Constants.FILE_CONFIG))) {
                FileHandler.writeToJson(Constants.FILE_CONFIG, new Config());
            }
            if (!Files.exists(Paths.get(Constants.FILE_GLOBAL_DATA))) {
                FileHandler.writeToJson(Constants.FILE_GLOBAL_DATA, new GlobalData());
            }

            //load config phase 1
            Config config = (Config) FileHandler.readFromJson(Constants.FILE_CONFIG, Config.class);
            GlobalData globalData = (GlobalData) FileHandler.readFromJson(Constants.FILE_GLOBAL_DATA, GlobalData.class);


            config.initObject(config);
            FileHandler.writeToJson(Constants.FILE_CONFIG, config);

            //getting bot token
            token = FileHandler.readFromFile(Constants.FILE_TOKEN).get(0);
            if (token == null) {
                logger.error("!!!BOT TOKEN NOT VALID PLEASE CHECK \"Storage/Token.txt\" AND UPDATE THE TOKEN!!!");
            }

            IDiscordClient client = Client.getClient(token, false);

            //load config phase 2
            Globals.initConfig(client, config, globalData);

            PatchHandler.globalPatches();


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
            new EventHandler();


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
            if (Globals.consoleMessageCID != null) {
                IChannel channel = Globals.getClient().getChannelByID(Globals.consoleMessageCID);
                String message = scanner.nextLine();
                message = message.replace("#Dawn#", Globals.getClient().getUserByID(153159020528533505L).getName());
                message = message.replace("teh", "the");
                message = message.replace("Teh", "The");
//            System.out.println(message);
                if (!message.equals("")) {
                    Utility.sendMessage(message, channel);
                }
            }
        }
    }
}
