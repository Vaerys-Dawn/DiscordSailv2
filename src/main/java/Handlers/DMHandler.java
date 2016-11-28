package Handlers;

import Main.Globals;
import Main.Utility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.util.regex.PatternSyntaxException;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class DMHandler {

    final static Logger logger = LoggerFactory.getLogger(DMHandler.class);

    public DMHandler(IMessage message) {
        String prefixResponse = "#respond#{";
        if (message.getAuthor().getID().equals(Globals.creatorID)) {
            if (message.toString().startsWith(prefixResponse)) {
                try {
                    String response = message.toString();
                    String tagRespond = StringUtils.substringBetween(response, prefixResponse, "}");
                    response = response.replace(prefixResponse + tagRespond + "}", "");
                    if (tagRespond == null){
                        return;
                    }
                    if (Utility.sendDM(response, tagRespond).get()) {
                        Utility.sendDM("> An Error occurred while attempting to run this command.", Globals.creatorID);
                    }
                } catch (PatternSyntaxException ex) {
                    Utility.sendDM("> An Error occurred while attempting to run this command.", Globals.creatorID);
                }
            }
        } else {
            String logging = "[" + message.getAuthor().getID() + "] " + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " : " + message.toString();
            logger.info(logging);
            Utility.sendDM(logging, Globals.creatorID);
        }
    }
}
