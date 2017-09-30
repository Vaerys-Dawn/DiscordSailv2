package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.DMCommand;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.pogos.GlobalData;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class BlockUser implements DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.user.longID == Globals.creatorID) {
            GlobalData globalData = Globals.getGlobalData();
            boolean worked = false;
            SplitFirstObject object = new SplitFirstObject(args);
            if (globalData != null) {
                long id = Utility.stringLong(object.getFirstWord());
                IUser user = command.client.get().fetchUser(id);
                if (id == Globals.creatorID) {
                    return "> What are you doing. Don't try to block yourself.";
                }
                if (user != null) {
                    globalData.blockUserFromDMS(id);
                    worked = true;
                } else {
                    worked = false;
                }
            }
            if (worked) {
                return "> User was Blocked.";
            } else {
                return "> An Error Occurred.";
            }
        } else {
            return command.user.notAllowed;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"BlockUser"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows the author to block a user from sending them DMs.";
    }

    @Override
    public String usage() {
        return "[User ID]";
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
