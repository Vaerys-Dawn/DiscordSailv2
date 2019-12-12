package com.github.vaerys.commands.adminccs;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.utils.TriVar;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AdminCCInfo extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        AdminCCObject cc = command.guild.adminCCs.getCommand(args);
        if (cc == null) return "\\> Could not find Admin Custom Command.";

        //get keys and their counts
        List<TriVar<Long, String, Long>> keys = cc.getPathKeys();
        Map<String, Long> keyCount = new HashMap<>();
        for (TriVar<Long, String, Long> key : keys) {
            String keyCode = key.getVar2();
            if (keyCount.containsKey(keyCode)) {
                keyCount.put(keyCode, keyCount.get(keyCode) + 1);
            } else {
                keyCount.put(keyCode, 1L);
            }
        }

        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(String.format("\\> Here is the information for the Admin Custom Command: %s", cc.getName()));
        UserObject creator = UserObject.getNewUserObject(cc.getCreatorID(), command.guild);
        StringHandler desc = new StringHandler("Creator: **@%s**\nTimes Run: **%d**\nSlots Used: **%d**", creator.username, cc.getTimesRun(), cc.getSlots(), cc.getPathKeys().size());
        if (cc.hasLimitTry()) {
            long attempts = command.guild.adminCCs.getTries(cc).size();
            desc.appendFormatted("\nCurrent Tries: **%d**", attempts);
        }
        if (keyCount.size() == 0) {
            desc.append("\nKey Count: 0");
        } else {
            List formattedKeys = new LinkedList();
            keyCount.forEach((key, count) -> formattedKeys.add(String.format("%s: %d", key, count)));
            String compiled = Utility.listFormatter(formattedKeys, true);
            desc.appendFormatted("\nKey Counts: \n```\n%s```", compiled);
        }
        builder.withDesc(desc.toString());
        builder.send(command);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"AdminCCInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about an Admin CC";
    }

    @Override
    public String usage() {
        return "[Admin Custom Command Name]";
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN_CC;
    }

    @Override
    public ChannelSetting channel() {
        return ChannelSetting.MANAGE_CC;
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
    public void init() {

    }
}