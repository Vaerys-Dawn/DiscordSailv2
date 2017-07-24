package Commands.DMCommands;

import Commands.DMCommandObject;
import Interfaces.DMCommand;
import sx.blah.discord.handle.obj.IChannel;

/**
 * Created by Vaerys on 15/07/2017.
 */
public class TestDM implements DMCommand {
    @Override
    public String execute(String args, DMCommandObject command) {
//        for (IChannel c : command.client.getGuildByID(176434793674833920L).getChannels()) {
//            if (("#" + c.getName()).equalsIgnoreCase(args)) {
//                return c.getName();
//            }
//        }
//        return "channel could not be found";
        return "> You've done your testing";
    }

    @Override
    public String[] names() {
        return new String[]{"Test"};
    }

    @Override
    public String description() {
        return "Is a test";
    }

    @Override
    public String usage() {
        return "[args]";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}
