package POGOs;

import Handlers.FileHandler;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.BlackListObject;
import Objects.CCommandObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;
import java.util.ArrayList;

// TODO: 31/08/2016 Add the ability to create custom commands -- done
// TODO: 31/08/2016 Add the ability to delete custom commands -- done
// TODO: 31/08/2016 Add the ability to edit custom commands -- later pls
// TODO: 31/08/2016 Add the ability to list all custom commands
// TODO: 31/08/2016 Add the ability to list all custom commands made by a certain user
// TODO: 31/08/2016 Add the ability to search for a custom command based on name, contents, or ShitPost (use separate commands)
// TODO: 31/08/2016 Add the ability to vote to remove a custom command (user must be trusted in order to initiate the vote) (must get 10 votes in 2 hour to remove)
// TODO: 31/08/2016 Add blacklisting of phrases to custom command creation, editing and execution
// TODO: 31/08/2016 Add the ability to see the amount of times the command is run
// TODO: 04/09/2016 Add ShitPost filtering -- done
// TODO: 04/09/2016 Add on creation tags
// TODO: 04/09/2016 Add Command transferring.
// TODO: 28/09/2016 200/100 char limit on #args#
// TODO: 01/10/2016 cc limits 5 for non trusted 20 for trusted roles and unlimited for anyone with the #Mannage_Messages perm
// TODO: 04/09/2016 Make it so that the command is default to ShitPost upon creation in the #shitpost channel
// TODO: 04/09/2016 maye a helpful command list that those with manage messages can add to
// TODO: 07/10/2016 add if tags 'ifRole' and 'ifName'.
// TODO: 07/10/2016 add tag `variant` that allows people to use $addvariant [Commandname] to that (would use 'Random' tag as a base)
// TODO: 28/09/2016 (Maybe) make a way to have auto shitposting (togalable, off by default requires an admin to turn it on)
// TODO: 31/08/2016 (Maybe) Add Fweeee to CC.RewardBag (using VoiceBot functionality)
// TODO: 31/08/2016 (Maybe) Add the ability to have images uploaded to the guild rather than posting a link (maybe)
// TODO: 06/10/2016 (maybe) Add a way to create a CC variant. using $AddCCVariant jeez this is going to be interesting...

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands {
    boolean properlyInit = false;
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    ArrayList<CCommandObject> commands = new ArrayList<>();
    final CCommandObject commandNotFound = new CCommandObject(true, "Error", "404", "> Command not found.", false);

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public String addCommand(boolean isLocked, String userID, String commandName, String commandContents, boolean isShitPost, IGuild guild, boolean isTrusted) {
        int counter = 0;
        int maxCCs = 10;
        String toCheck = commandName + commandContents;
        if (Utility.checkBlacklist(toCheck, blackList) != null) {
            return Utility.checkBlacklist(toCheck, blackList);
        }
        if (commandName.length() > 50) {
            return "> Command name too long.";
        }
        boolean hasManagePerms = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, Globals.getClient().getUserByID(userID), guild);
        boolean hasAdminPerms = Utility.testForPerms(new Permissions[]{Permissions.ADMINISTRATOR}, Globals.getClient().getUserByID(userID), guild);
        if (hasManagePerms) {
            maxCCs += 40;
        }
        if (hasAdminPerms) {
            maxCCs += 100;
        }
        if (isTrusted) {
            maxCCs += 20;
        }
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                return "> Command name already in use.";
            }
            if (c.getUserID().equals(userID)) {
                counter++;
            }
        }
        if (counter < maxCCs) {
            if (commandContents.length() < 1500) {
                commands.add(new CCommandObject(isLocked, userID, commandName, commandContents, isShitPost));
                return "> Command Added you have " + (maxCCs - counter - 1) + " custom command slots left.\n" +
                        Constants.PREFIX_INDENT + "You can run your new command by performing `" + Constants.PREFIX_CC + commandName + "`.";
            } else {
                return "> Command Contents to long. max length = 1500 chars.";
            }
        } else {
            return "> You have run out of custom command slots. you can make room by deleting or editing old custom commands.";
        }
    }

    public void initCustomCommands() {
        if (!properlyInit) {
            blackList.add(new BlackListObject("<@", "Please do not put **mentions** in Custom Commands."));
            blackList.add(new BlackListObject("discord.gg", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListObject("discordapp.com/Invite/", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListObject("@everyone", "Please go not put **mentions** in Custom Commands."));
            blackList.add(new BlackListObject("@here", "Please go not put **mentions** in Custom Commands."));
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getID(), "Echo", "#args#", false));
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getID(), "Wiki", "http://starbounder.org/Special:Search/#args##regex#{ ;_}", false));
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getID(), "TotalCCs", "Base User **+10**\nTrusted Role **+20**\nManage Messages Perm **+40**\nAdministrator perms **+100**", false));
        }
    }

    public ArrayList<CCommandObject> getCommandList() {
        return commands;
    }

    public String checkblackList(String args) {
        return Utility.checkBlacklist(args, blackList);
    }

    public String getCommandInfo(String args) {
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(args)) {
                StringBuilder builder = new StringBuilder();
                IUser author = Globals.getClient().getUserByID(c.getUserID());
                builder.append("> Here is the information for command: **" + c.getName() + "**\n");
                builder.append(Constants.PREFIX_INDENT + "Creator: **@" + author.getName() + "#" + author.getDiscriminator() + "**\n");
                builder.append(Constants.PREFIX_INDENT + "Time Run: **" + c.getTimesRun() + "**\n");
                builder.append(Constants.PREFIX_INDENT + "Is Locked: **" + c.isLocked() + "**\n");
                builder.append(Constants.PREFIX_INDENT + "Is ShitPost: **" + c.isShitPost() + "**");
                return builder.toString();
            }
        }
        return Constants.ERROR_CC_NOT_FOUND;
    }

    public void sendCCasJSON(String channelID, String commandName) {
        IChannel channel = Globals.getClient().getChannelByID(channelID);
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                FileHandler.writeToJson(Constants.DIRECTORY_TEMP + c.getName() + ".json", c);
                File file = new File(Constants.DIRECTORY_TEMP + c.getName() + ".json");
                if (Utility.sendFile("> Here is the Raw Data for Custom Command: **" + c.getName() + "**", channel, file).get()) {
                    Utility.sendMessage("> An error occurred when attempting to get CC data.", channel);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                file.delete();
                return;
            }
        }
        Utility.sendMessage(Constants.ERROR_CC_NOT_FOUND, channel);
        return;
    }

    public String delCommand(String args, IUser author, IGuild guild) {
        int i = 0;
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(args)) {
                boolean canBypass = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, author, guild);
                if (author.getID().equals(guild.getOwnerID()) || author.getID().equals(Globals.creatorID)) {
                    canBypass = true;
                }
                if (author.getID().equals(c.getUserID()) || canBypass) {
                    if (c.isLocked()) {
                        return "> This command is locked and must be unlocked to be deleted.";
                    } else {
                        commands.remove(i);
                        return "> Command Deleted.";
                    }
                }
            }
            i++;
        }
        return Constants.ERROR_CC_NOT_FOUND;
    }

    public String getUserCommands(String userID) {
        IUser user = Globals.getClient().getUserByID(userID);
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are the custom commands for user: **@" + user.getName() + "#" + user.getDiscriminator() + "**.\n`");
        for (CCommandObject sA : commands) {
            if (sA.getUserID().equals(userID)) {
                builder.append(Constants.PREFIX_CC + sA.getName() + ", ");
            }
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("`.");
        return builder.toString();
    }

    public String listCommands(int page) {
        StringBuilder builder = new StringBuilder();
        ArrayList<String> pages = new ArrayList<>();
        int counter = 0;
        int totalCCs = 0;
        for (CCommandObject c : commands) {
            if (counter > 15) {
                pages.add(builder.toString());
                builder.delete(0, builder.length());
                counter = 0;
            }
            builder.append(Constants.PREFIX_CC + c.getName() + ", ");
            totalCCs++;
            counter++;
        }
        pages.add(builder.toString());
        builder.delete(0, builder.length());
        try {
            builder.append("> Here is Page `" + page + "/" + pages.size() + "` of Custom Commands:\n`");
            builder.append(pages.get(page - 1));
            builder.delete(builder.length() - 2, builder.length());
            builder.append(".`\n> Total Custom Commands stored on this Server: " + totalCCs);
            return builder.toString();
        } catch (IndexOutOfBoundsException e) {
            return "> That Page does not exist.";
        }
    }

    public String toggleShitPost(String args) {
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(args)) {
                if (!c.isLocked()) {
                    c.toggleShitPost();
                    return "> Toggled Shitpost for that command.";
                } else return "> That command is locked and cannot be edited.";
            }
        }
        return Constants.ERROR_CC_NOT_FOUND;
    }

    public String toggleLock(String args) {
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(args)) {
                c.toggleLocked();
                return "> Toggled Lock for that command.";
            }
        }
        return Constants.ERROR_CC_NOT_FOUND;
    }
}
