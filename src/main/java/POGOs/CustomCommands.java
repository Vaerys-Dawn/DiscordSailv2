package POGOs;

import Handlers.FileHandler;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.BlackListObject;
import Objects.CCommandObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;
import java.util.ArrayList;

// TODO: 31/08/2016 Add the ability to search for a custom command based on name, contents, or ShitPost (use separate commands) -- partially complete.
// TODO: 31/08/2016 Add blacklisting of phrases to custom command creation, editing and execution
// TODO: 07/10/2016 add tag `variant` that allows people to use $addvariant [Commandname] to that (would use 'Random' tag as a base)
// TODO: 31/08/2016 (Maybe) Add Fweeee to CC.RewardBag (using VoiceBot functionality)
// TODO: 04/09/2016 (Maybe) a helpful command list that those with manage messages can add to
// TODO: 06/10/2016 (maybe) Add a way to create a CC variant. using $AddCCVariant jeez this is going to be interesting...

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands {
    boolean properlyInit = false;
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    ArrayList<CCommandObject> commands = new ArrayList<>();

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public int maxCCs(IUser author, IGuild guild, GuildConfig guildConfig) {
        int total = 10;
        boolean hasManagePerms = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, author, guild);
        boolean hasAdminPerms = Utility.testForPerms(new Permissions[]{Permissions.ADMINISTRATOR}, author, guild);
        boolean isTrusted = guildConfig.testIsTrusted(author, guild);
        if (hasManagePerms) {
            total += 40;
        }
        if (hasAdminPerms) {
            total += 100;
        }
        if (isTrusted) {
            total += 20;
        }
        if (guild.getOwner().getStringID().equals(author.getStringID())) {
            total = 170;
        }
        return total;
    }

    public String addCommand(boolean isLocked, IUser author, String commandName, String commandContents, boolean isShitPost, IGuild guild, GuildConfig guildConfig) {
        int counter = 0;
        int limitCCs;
        String toCheck = commandName + commandContents;
        if (Utility.checkBlacklist(toCheck, blackList) != null) {
            return Utility.checkBlacklist(toCheck, blackList);
        }
        if (commandName.length() > 50) {
            return "> Command name too long.";
        }
        if (commandName.isEmpty()){
            return "> Command name cannot be empty.";
        }
        if (StringUtils.countMatches(commandContents, "#embedImage#{") > 1) {
            return "> Custom Commands Cannot have multiple #embedImage# tags";
        }
        limitCCs = maxCCs(author, guild, guildConfig);

        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                return "> Command name already in use.";
            }
            if (c.getUserID().equals(author.getStringID())) {
                counter++;
            }
        }
        if (counter < limitCCs) {
            if (commandContents.length() < 1500) {
                commands.add(new CCommandObject(isLocked, author.getStringID(), commandName, commandContents, isShitPost));
                return "> Command Added you have " + (limitCCs - counter - 1) + " custom command slots left.\n" +
                        Constants.PREFIX_INDENT + "You can run your new command by performing `" + guildConfig.getPrefixCC() + commandName + "`.";
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
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getStringID(), "Echo", "#args#", false));
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getStringID(), "Wiki", "http://starbounder.org/Special:Search/#args##regex#{ ;_}", false));
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getStringID(), "CCAllowance", "Base User **+10**\nTrusted Role **+20**\nManage Messages Perm **+40**\nAdministrator perms **+100**", false));
        }
    }

    public ArrayList<CCommandObject> getCommandList() {
        return commands;
    }

    public String checkblackList(String args) {
        return Utility.checkBlacklist(args, blackList);
    }

    public String sendCCasJSON(String channelID, String commandName) {
        IChannel channel = Globals.getClient().getChannelByID(channelID);
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                FileHandler.writeToJson(Constants.DIRECTORY_TEMP + c.getName() + ".json", c);
                File file = new File(Constants.DIRECTORY_TEMP + c.getName() + ".json");
                if (Utility.sendFile("> Here is the Raw Data for Custom Command: **" + c.getName() + "**", file, channel).get()) {
                    Utility.sendMessage("> An error occurred when attempting to getSlashCommands CC data.", channel);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                file.delete();
                return null;
            }
        }
        Utility.sendMessage(Constants.ERROR_CC_NOT_FOUND, channel);
        return null;
    }

    public String delCommand(String args, IUser author, IGuild guild) {
        int i = 0;
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(args)) {
                boolean canBypass = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, author, guild);
                if (author.getStringID().equals(guild.getOwnerID()) || author.getStringID().equals(Globals.creatorID)) {
                    canBypass = true;
                }
                if (author.getStringID().equals(c.getUserID()) || canBypass) {
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
}
