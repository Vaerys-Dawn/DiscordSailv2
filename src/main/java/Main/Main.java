package Main;

import Handlers.FileHandler;
import Listeners.AnnotationListener;
import POGOs.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {


    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String token;
        // you need to set a token in Token/Token.txt for the bot to run
        try {
            Discord4J.disableChannelWarnings();
            FileHandler handler = new FileHandler();
            handler.createDirectory(Constants.DIRECTORY_STORAGE);
            handler.createDirectory(Constants.DIRECTORY_GLOBAL_IMAGES);
            handler.createDirectory(Constants.DIRECTORY_COMP);
            handler.createDirectory(Constants.DIRECTORY_BACKUPS);
            Competition competition = new Competition();
            if (!Files.exists(Paths.get(Constants.FILE_COMPETITION))){
                competition.setProperlyInit(true);
                handler.writetoJson(Constants.FILE_COMPETITION, competition);
            }
            token = handler.readFromFile(Constants.FILE_TOKEN).get(0);
            IDiscordClient client = Client.getClient(token, false);
            client.isBot();
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(new AnnotationListener());
            client.login();
            Globals.setClient(client);
        } catch (DiscordException ex) {
            logger.error(ex.getErrorMessage());
        }
    }
}
