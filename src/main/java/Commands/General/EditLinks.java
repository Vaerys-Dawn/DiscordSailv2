package Commands.General;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.SplitFirstObject;
import Objects.UserLinkObject;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Vaerys on 17/06/2017.
 */
public class EditLinks implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        IUser user = command.author;
        SplitFirstObject userID = new SplitFirstObject(args);
        boolean adminEdit = false;
        if (Utility.testForPerms(dualPerms(), command.author, command.guild) || Utility.canBypass(command.author, command.guild)) {
            try {
                user = command.client.getUserByID(Long.parseLong(userID.getFirstWord()));
                if (user != null) {
                    adminEdit = true;
                }
            }catch (NumberFormatException e){
                //do nothing
            }
        }
        SplitFirstObject linkName;
        if (adminEdit) {
            linkName = new SplitFirstObject(userID.getRest());
        }else {
            linkName = new SplitFirstObject(args);
        }
        UserTypeObject userObject = command.guildUsers.getUserByID(user.getStringID());
        userObject.setLinks();
        for (UserLinkObject link: userObject.getLinks()){
            if (link.getName().equalsIgnoreCase(linkName.getFirstWord())){
                userObject.getLinks().remove(link);
                if (adminEdit){
                    return "> User's Link was removed.";
                }else {
                    return "> Link removed.";
                }
            }
        }
        if (linkName.getRest() == null){
            return "> Cannot add link, Must specify a URL.";
        }else {
            if (userObject.getLinks().size() > 4){
                if (adminEdit){
                    return "> User already has 5 links, a link must be removed to add a new one.";
                }else {
                    return "> You already have 5 links, you must remove one to add a new one.";
                }
            }
            if (linkName.getFirstWord().length() > 15){
                return "> Link Name too long. (Max 15 chars)";
            }
            if (Utility.checkURL(linkName.getRest())){
                try {
                    new URL(linkName.getRest());
                    userObject.getLinks().add(new UserLinkObject(linkName.getFirstWord(),linkName.getRest()));
                    if (adminEdit){
                        return "> New link added for User.";
                    }else {
                        return "> New link added.";
                    }
                }catch (MalformedURLException e){
                    return "> Cannot add link, Invalid URL.";
                }
            }else {
                return "> Cannot add link, Invalid URL.";
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"EditLinks","NewLink"};
    }

    @Override
    public String description() {
        return "Allows uses to manage the links attached to their profile (Max 5 links per user).";
    }

    @Override
    public String usage() {
        return "[Link Name] (Link)";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
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
        return "Allows the modification of user links.";
    }

    @Override
    public String dualUsage() {
        return "[UserID] [Link Name] (Link)";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }
}
