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
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

// TODO: 31/08/2016 Add the ability to edit custom commands -- later pls
// TODO: 31/08/2016 Add the ability to search for a custom command based on name, contents, or ShitPost (use separate commands)
// TODO: 31/08/2016 Add blacklisting of phrases to custom command creation, editing and execution
// TODO: 04/09/2016 maye a helpful command list that those with manage messages can add to
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

    private int maxCCs(IUser author, IGuild guild, GuildConfig guildConfig) {
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
        if (StringUtils.countMatches(commandContents, "#embedImage#{") > 1) {
            return "> Custom Commands Cannot have multiple #embedImage# tags";
        }
        limitCCs = maxCCs(author, guild, guildConfig);

        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                return "> Command name already in use.";
            }
            if (c.getUserID().equals(author.getID())) {
                counter++;
            }
        }
        if (counter < limitCCs) {
            if (commandContents.length() < 1500) {
                commands.add(new CCommandObject(isLocked, author.getID(), commandName, commandContents, isShitPost));
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
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getID(), "Echo", "#args#", false));
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getID(), "Wiki", "http://starbounder.org/Special:Search/#args##regex#{ ;_}", false));
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getID(), "CCAllowance", "Base User **+10**\nTrusted Role **+20**\nManage Messages Perm **+40**\nAdministrator perms **+100**", false));
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

    public String sendCCasJSON(String channelID, String commandName) {
        IChannel channel = Globals.getClient().getChannelByID(channelID);
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                FileHandler.writeToJson(Constants.DIRECTORY_TEMP + c.getName() + ".json", c);
                File file = new File(Constants.DIRECTORY_TEMP + c.getName() + ".json");
                if (Utility.sendFile("> Here is the Raw Data for Custom Command: **" + c.getName() + "**", file, channel).get()) {
                    Utility.sendMessage("> An error occurred when attempting to get CC data.", channel);
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

    public String getUserCommands(String userID, IGuild guild, GuildConfig guildConfig) {
        IUser user = Globals.getClient().getUserByID(userID);
        StringBuilder builder = new StringBuilder();
        int total = 0;
        int max = maxCCs(user, guild, guildConfig);
        builder.append("> Here are the custom commands for user: **@" + user.getName() + "#" + user.getDiscriminator() + "**.\n`");
        for (CCommandObject sA : commands) {
            if (sA.getUserID().equals(userID)) {
                builder.append(guildConfig.getPrefixCC() + sA.getName() + ", ");
                total++;
            }
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("`.\nTotal Custom commands: " + total + "/" + max + ".");
        return builder.toString();
    }

    public String listCommands(int page, GuildConfig guildConfig) {
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
            builder.append(guildConfig.getPrefixCC() + c.getName() + ", ");
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

    public String search(String args, GuildConfig guildConfig, IChannel channel, String messageID) {
        ArrayList<CCommandObject> searched = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (CCommandObject c : commands) {
            StringBuilder toSearch = new StringBuilder();
            toSearch.append(c.getName().toLowerCase());
            toSearch.append(c.getContents(false).toLowerCase());
            if (c.isLocked()) {
                toSearch.append("#locked#");
            }
            if (c.isShitPost()) {
                toSearch.append("#shitpost#");
            }
            if ((toSearch.toString()).contains(args.toLowerCase())) {
                searched.add(c);
            }
        }
        builder.append("> Here is your search:\n`");
        for (CCommandObject c : searched) {
            builder.append(guildConfig.getPrefixCC() + c.getName() + ", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(".`");
        if (searched.size() < 40) {
            return builder.toString();
        } else {
            String path = Constants.DIRECTORY_TEMP + messageID + ".txt";
            FileHandler.writeToFile(path,builder.toString());
            File file = new File(path);
            Utility.sendFile("> Here is your Search:",file,channel);
            try {
                Thread.sleep(4000);
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public String editCC(String name, IUser user, IGuild guild, String mode, String content) {
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(name)) {
                if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, user, guild) || user.getID().equals(c.getUserID()) && !c.isLocked() || Utility.canBypass(user,guild)) {
                    Method[] methods = this.getClass().getMethods();
                    if (StringUtils.countMatches(mode + " " + content, "#embedImage#{") > 1) {
                        return "> Custom Commands Cannot have multiple #embedImage# tags";
                    }
                    switch (mode.toLowerCase()) {
                        case "replace":
                            return editModeReplace(c, content);
                        case "toembed":
                            return editModeToEmbed(c);
                        case "append":
                            return editModeAppend(c, content);
                        case "delet":
                            return editModeDeleteTag(c);
                        case "delcall":
                            return editModeDeleteTag(c);
                        default:
                            if (content == null || content.isEmpty()) {
                                return editModeReplace(c, mode);
                            } else {
                                return editModeReplace(c, mode + " " + content);
                            }
                    }
                } else {
                    return "> You do not have permission to edit this command.";
                }
            }
        }
        return "> Command Not found.";
    }

    private String editModeDeleteTag(CCommandObject c) {
        String delCall = "#delCall#";
        if (c.getContents(false).contains(delCall)){
            return "> Command will already delete the calling message.";
        }else {
            c.setContents(c.getContents(false) + delCall);
            return "> Tag added";
        }
    }


    private String editModeReplace(CCommandObject command, String content) {
        if (content == null || content.isEmpty()){
            return "> Missing content to replace with.";
        }
        command.setContents(content);
        return "> Command Edited.";
    }

    private String editModeToEmbed(CCommandObject commmand) {
        String contents = commmand.getContents(false);
        if (contents.contains(" ") || contents.contains("\n")) {
            return "> Failed to add embed tag.";
        }
        if (contents.contains("#embedImage#{")) {
            return "> Command already has an EmbedImage Tag, cannot add more than one.";
        }
        commmand.setContents("#embedImage#{" + contents + "}");
        return "> Embed tag added.";
    }

    private String editModeAppend(CCommandObject command, String content) {
        if (content == null || content.isEmpty()){
            return "> Missing content to append.";
        }
        if ((command.getContents(false) + content).length() > 2000) {
            return "> Cannot append content, would make command to large.";
        }
        command.setContents(command.getContents(false) + content);
        return "> Content appended to end of command.";
    }
}
