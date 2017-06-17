package Commands.Groups;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.GroupUpObject;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class GroupUp implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        String presence = null;
        if (!args.isEmpty()) {
            presence = args;
        }
        ArrayList<GroupUpObject> list = command.channelData.getGroupUpObjects();
        for (GroupUpObject g : list) {
            if (command.authorID == g.getUserID()) {
                list.remove(g);
                return "> You have been removed from the list.";
            }
        }
        list.add(new GroupUpObject(presence, command.authorID));
        //person added to list :D
        ArrayList<String> completeList = new ArrayList<>();
        for (GroupUpObject g : list) {
            IUser user = command.client.getUserByID(g.getUserID());
            IPresence userPres = user.getPresence();
            if (!userPres.getStatus().equals(StatusType.DND) || !userPres.getStatus().equals(StatusType.OFFLINE) || !userPres.getStatus().equals(StatusType.UNKNOWN)) {
                if (g.getPresence() != null || userPres.getPlayingText().isPresent()) {
                    String newPres;
                    if (g.getPresence() == null) {
                        newPres = userPres.getPlayingText().get();
                    }else {
                        newPres = g.getPresence();
                    }
                    StringBuilder builder = new StringBuilder(newPres);
                    if (builder.length() > 40){
                        builder.delete(0,40);
                        builder.append("...");
                    }
                    completeList.add(indent + user.mention() + " Playing: " + builder.toString());
                } else {
                    completeList.add(indent + user.mention());
                }
            }
        }
        return "**> You have been added to the GroupUp list.**\n\n" + "Here are the others currently waiting:\n" +
                Utility.listFormatter(completeList, false) + "\n*To opt out simply run this command again*\n" +
                Utility.getCommandInfo(this, command);
    }

    @Override
    public String[] names() {
        return new String[]{"GroupUp", "GroupMe"};
    }

    @Override
    public String description() {
        return "Adds you to a list of people that will be mentioned when this command is run.\nRun this command again to leave the list.";
    }

    @Override
    public String usage() {
        return "(Game)";
    }

    @Override
    public String type() {
        return TYPE_GROUPS;
    }

    @Override
    public String channel() {
        return CHANNEL_GROUPS;
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
