package com.github.vaerys.commands.groups;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.GroupUpObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class GroupUp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String presence = null;
        if (!args.isEmpty()) {
            presence = args;
        }
        ArrayList<GroupUpObject> list = command.guild.channelData.getGroupUpObjects();
        for (GroupUpObject g : list) {
            if (command.user.longID == g.getUserID()) {
                list.remove(g);
                return "> You have been removed from the list.";
            }
        }
        list.add(new GroupUpObject(presence, command.user.longID));
        //person added to list :D

        ArrayList<String> completeList = new ArrayList<>();


        for (GroupUpObject g : list) {
            IUser user = command.client.get().getUserByID(g.getUserID());
            if (user != null) {
                IPresence userPres = user.getPresence();
                if (!userPres.getStatus().equals(StatusType.DND) && !userPres.getStatus().equals(StatusType.OFFLINE) && !userPres.getStatus().equals(StatusType.UNKNOWN)) {
                    if (g.getPresence() != null || userPres.getText().isPresent()) {
                        String newPres;
                        if (g.getPresence() == null) {
                            newPres = userPres.getText().get();
                        } else {
                            newPres = g.getPresence();
                        }
                        StringBuilder builder = new StringBuilder(newPres);
                        if (builder.length() > 40) {
                            builder.delete(0, 40);
                            builder.append("...");
                        }
                        completeList.add(indent + user.mention() + " Playing: " + builder.toString());
                    } else {
                        completeList.add(indent + user.mention());
                    }
                }
            }
        }
        return "**> You have been added to the GroupUp list.**\n\n" + "Here are the others currently waiting:\n" +
                Utility.listFormatter(completeList, false) + "\n*To opt out simply run this command again*\n" +
                Utility.getCommandInfo(this, command);
    }

    @Override
    protected String[] names() {
        return new String[]{"GroupUp", "GroupMe"};
    }

    @Override
    public String description(CommandObject command) {
        return "Adds you to a list of people that will be mentioned when this command is run.\nRun this command again to leave the list.";
    }

    @Override
    protected String usage() {
        return "(Game)";
    }

    @Override
    protected SAILType type() {
        return SAILType.GROUPS;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.GROUPS;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
