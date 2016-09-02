package Handlers;

import Main.Globals;
import Main.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class DMHandler {

    final static Logger logger = LoggerFactory.getLogger(DMHandler.class);

    public DMHandler(IMessage message){
        String logging = "["+ message.getAuthor().getID()+ "] " + message.getAuthor().getName() + "#"+ message.getAuthor().getDiscriminator() + " : " + message.toString();
        logger.info(logging);
        try {
            IChannel channel = message.getClient().getOrCreatePMChannel(message.getClient().getUserByID(Globals.creatorID));
            Utility.sendMessage(logging,channel);
        } catch (DiscordException e) {
            e.printStackTrace();
        } catch (RateLimitException e) {
            e.printStackTrace();
        }
    }
}
