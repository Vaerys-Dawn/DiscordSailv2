package Main;

import Handlers.FileHandler;
import Listeners.AnnotationListener;
import POGOs.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.Scanner;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {


    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.out.println("Starting Program...");

        String token;
        // you need to set a token in Token/Token.txt for the bot to run
        try {
            Discord4J.disableChannelWarnings();
            FileHandler handler = new FileHandler();
            handler.createDirectory(Constants.DIRECTORY_STORAGE);
            handler.createDirectory(Constants.DIRECTORY_GLOBAL_IMAGES);
            handler.createDirectory(Constants.DIRECTORY_COMP);
            handler.createDirectory(Constants.DIRECTORY_BACKUPS);
            handler.createDirectory(Constants.DIRECTORY_TEMP);
            handler.createDirectory(Constants.DIRECTORY_OLD_FILES);
            Competition competition = new Competition();
            if (!Files.exists(Paths.get(Constants.FILE_COMPETITION))) {
                competition.setProperlyInit(true);
                handler.writeToJson(Constants.FILE_COMPETITION, competition);
            }
            token = handler.readFromFile(Constants.FILE_TOKEN).get(0);
            IDiscordClient client = Client.getClient(token, false);
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(new AnnotationListener());
            client.login();
            new TimedEvents();
            Globals.setClient(client);
            consoleInput();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    TimedEvents.saveAndLogOff();
                }
            });
        } catch (DiscordException ex) {
            logger.error(ex.getErrorMessage());
        } catch (RateLimitException e) {
            e.printStackTrace();
        }
    }

    private static void consoleInput() {
        Scanner scanner = new Scanner(System.in);
        while (!Globals.isReady);
        logger.info("Console input initiated.");
        while (scanner.hasNextLine()){
            IChannel channel = Globals.getClient().getChannelByID(Globals.consoleMessageCID);
            String message = scanner.nextLine();
            message = message.replaceAll("#Dawn#", Globals.getClient().getUserByID("153159020528533505").toString());
            message = message.replaceAll("teh", "the");
            message = message.replaceAll("Teh", "The");
            if (!message.equals("")) {
                Utility.sendMessage(message, channel);
            }
        }
    }
}
