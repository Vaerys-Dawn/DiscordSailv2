package Handlers;

import Commands.DMCommandObject;
import Interfaces.DMCommand;
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
        message.getTimestamp();
        if (message.getAuthor().isBot()){
            return;
        }
        for (String blocked: Globals.getGlobalData().getBlockedFromDMS()){
            if (message.getAuthor().getStringID().equals(blocked)){
                Utility.sendDM("> You have been blocked from sending DMs to S.A.I.L by the Bot Creator.",blocked);
                return;
            }
        }
        if (message.toString().startsWith(Globals.defaultPrefixCommand)) {
            DMCommandObject commandObject = new DMCommandObject(message);
            SplitFirstObject args = new SplitFirstObject(message.toString());
            for (DMCommand command : Globals.getCommandsDM()){
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
        }else if (!message.getAuthor().getStringID().equals(Globals.creatorID)){
            String logging = "[" + message.getAuthor().getStringID() + "] " + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + ": " + message.toString();
            logger.info(logging);
            if(message.getAttachments().size() > 0){
                String attachmemts = "";
                for (IMessage.Attachment a: message.getAttachments()){
                    attachmemts += "\n" + a.getUrl();
                }
                Utility.sendDM(logging + attachmemts, Globals.creatorID);
            }else {
                Utility.sendDM(logging, Globals.creatorID);
            }
        }
    }
}
