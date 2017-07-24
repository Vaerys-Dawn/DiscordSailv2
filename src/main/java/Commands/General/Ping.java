package Commands.General;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Vaerys on 12/07/2017.
 */
public class Ping implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        ZonedDateTime messageReceived = command.message.getTimestamp().atZone(ZoneOffset.UTC);
        IMessage working = Utility.sendMessage("`Working...`", command.channel).get();
        ZonedDateTime testPing = working.getTimestamp().atZone(ZoneOffset.UTC);
        long ping = (testPing.toInstant().toEpochMilli() - messageReceived.toInstant().toEpochMilli());
        Utility.sendMessage("Pong - " + NumberFormat.getInstance().format(ping) + "ms", command.channel);
        Utility.deleteMessage(working);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Ping"};
    }

    @Override
    public String description() {
        return "Sends a ping.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
