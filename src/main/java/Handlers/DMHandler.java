package Handlers;

import Commands.DMCommand;
import Commands.DMCommandObject;
import Main.Globals;
import Main.Utility;
import Objects.SplitFirstObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class DMHandler {

    final static Logger logger = LoggerFactory.getLogger(DMHandler.class);

    public DMHandler(IMessage message) {
        if (message.getAuthor().isBot()){
            return;
        }
        if (message.toString().startsWith(Globals.defaultPrefixCommand)) {
            DMCommandObject commandObject = new DMCommandObject(message);
            SplitFirstObject args = new SplitFirstObject(message.toString());
            for (DMCommand command : Globals.commandsDM){
                for (String name : command.names()){
                    if (args.getFirstWord().equalsIgnoreCase(Globals.defaultPrefixCommand + name)){
                        //logging
                        logger.debug(Utility.loggingFormatter(args.getFirstWord(),args.getRest(),commandObject));

                        if (command.requiresArgs() && (args.getRest() == null || args.getRest().isEmpty())){
                            Utility.sendDM("> Command Missing arguments.\n" + Utility.getCommandInfo(command),commandObject.authorID);
                            return;
                        }
                        if (command.type().equals(DMCommand.TYPE_CREATOR)){
                            if (commandObject.authorID.equals(Globals.creatorID)){
                                Utility.sendDM(command.execute(args.getRest(),commandObject),commandObject.authorID);
                                return;
                            }else {
                                Utility.sendDM(commandObject.notAllowed,commandObject.authorID);
                                return;
                            }
                        }else {
                            Utility.sendDM(command.execute(args.getRest(),commandObject),commandObject.authorID);
                            return;
                        }
                    }
                }
            }
        }else if (!message.getAuthor().getID().equals(Globals.creatorID)){
            String logging = "[" + message.getAuthor().getID() + "] " + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " : " + message.toString();
            logger.info(logging);
            Utility.sendDM(logging, Globals.creatorID);
        }
    }
}
