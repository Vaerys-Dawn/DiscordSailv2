package Commands.Creator;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Globals;
import Main.Utility;
import Objects.DailyUserMessageObject;
import Objects.SplitFirstObject;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ListIterator;

public class DailyMsg implements Command {

    String modes = "> Edit - `args = new Contents`\n" +
            "> Delete\n" +
            "> Info - `Default`\n";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject uIDString = new SplitFirstObject(args);
        try {
            long uID = Long.parseLong(uIDString.getFirstWord());
            DailyUserMessageObject messageObject = null;
            for (DailyUserMessageObject u : Globals.getGlobalData().getDailyMessages()) {
                if (u.getUID() == uID) {
                    messageObject = u;
                }
            }
            if (messageObject == null) {
                return "> Could not find daily message with that UID.";
            }
//            if (uIDString.getRest() != null) {
            SplitFirstObject mode = null;
            String totest;
            if (uIDString.getRest() == null) {
                totest = "empty";
            } else {
                mode = new SplitFirstObject(uIDString.getRest());
                totest = mode.getFirstWord();
            }
            switch (totest.toLowerCase()) {
                case "edit":
                    messageObject.setContents(mode.getRest());
                    return "> Daily Message contents updated";
                case "delete":
                    ListIterator iterator = Globals.getGlobalData().getDailyMessages().listIterator();
                    while (iterator.hasNext()) {
                        DailyUserMessageObject object = (DailyUserMessageObject) iterator.next();
                        if (object.getUID() == uID) {
                            iterator.remove();
                        }
                    }
                    return "> Daily Message Deleted";
                case "info":
                    Utility.sendEmbedMessage("", getInfo(messageObject, command), command.channel);
                    return null;
                default:
                    Utility.sendEmbedMessage("", getInfo(messageObject, command), command.channel);
                    return null;
            }
        } catch (NumberFormatException e) {
            return "> Invalid UID.";
        }
    }

    private XEmbedBuilder getInfo(DailyUserMessageObject messageObject, CommandObject command) {
        XEmbedBuilder embedBuilder = new XEmbedBuilder();
        embedBuilder.withColor(Utility.getUsersColour(command.botUser, command.guild));
        IUser user = command.client.getUserByID(messageObject.getUserID());
        if (user != null) {
            embedBuilder.withAuthorName(user.getName() + "#" + user.getDiscriminator());
        }
        embedBuilder.withTitle(messageObject.getDay() + "");
        embedBuilder.withDesc(messageObject.getContents());
        embedBuilder.withFooterText(messageObject.getUID() + "");
        return embedBuilder;
    }

    @Override
    public String[] names() {
        return new String[]{"DailyMsg"};
    }

    @Override
    public String description() {
        return "allows for editing of the daily message list.\n" + modes;
    }

    @Override
    public String usage() {
        return "[ID] (Mode) (args)";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
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
        return true;
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