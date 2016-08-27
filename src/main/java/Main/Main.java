package Main;

import Handlers.FileHandler;
import Listeners.AnnotationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {


    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String token;
        // you need to set a token in Token/Token.txt for the bot to run
        try {
            FileHandler handler = new FileHandler();
            handler.createDirectory(Constants.DIRECTORY_STORAGE);
            handler.createDirectory(Constants.DIRECTORY_IMAGES);
            token = handler.readFromFile(Constants.FILE_TOKEN).get(0);
            IDiscordClient client = Client.getClient(token, true);
            client.isBot();
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(new AnnotationListener());
        } catch (DiscordException ex) {
            logger.error(ex.getErrorMessage());
        }
    }
}
