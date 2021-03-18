package com.github.vaerys.commands.groups;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.GroupUpObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

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
        command.guild.channelData.checkGroupUp(command);
        List<GroupUpObject> list = command.guild.channelData.getGroupUpObjects();
        for (GroupUpObject g : list) {
            if (command.user.longID == g.getUserID()) {
                list.remove(g);
                return "\\> You have been removed from the list.";
            }
        }
        list.add(new GroupUpObject(presence, command.user.longID));
        //person added to list :D

        List<String> completeList = new ArrayList<>();

        for (GroupUpObject g : list) {
            Member user = command.guild.getUserByID(g.getUserID());
            if (user == null) continue;
            OnlineStatus userStatus = user.getOnlineStatus();
            Activity userPres = user.getActivities().get(0);
            if (!userStatus.equals(OnlineStatus.DO_NOT_DISTURB) && !userStatus.equals(OnlineStatus.OFFLINE) && !userStatus.equals(OnlineStatus.UNKNOWN)) {
                if (g.getPresence() != null) {
                    String newPres;
                    if (g.getPresence() == null) {
                        newPres = userPres.getName();
                    } else {
                        newPres = g.getPresence();
                    }
                    StringHandler builder = new StringHandler(newPres);
                    if (builder.length() > 40) {
                        builder.delete(41, builder.length());
                        builder.append("...");
                    }
                    builder.replaceRegex("(?i)@(here|everyone)", "[REDACTED]");
                    builder.replaceRegex("<@(!|&)?[0-9]*>", "[REDACTED]");
                    completeList.add(INDENT + user.getAsMention() + " Playing: " + builder.toString());
                } else {
                    completeList.add(INDENT + user.getAsMention());
                }
            }
        }

        return "**\\> You have been added to the GroupUp list.**\n\n" + "Here are the others currently waiting:\n" +
                Utility.listFormatter(completeList, false) + "\n*To opt out simply run this command again*\n" +

                missingArgs(command);

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
    protected Permission[] perms() {
        return new Permission[0];
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
