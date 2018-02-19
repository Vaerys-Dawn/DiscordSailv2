package com.github.vaerys.commands.characters;

import java.util.List;
import java.util.stream.Collectors;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateChar extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        List<CharacterObject> userChars = command.user.characters;
        String charName = args.split(" ")[0];
        CharacterObject selectedChar = command.guild.characters.getCharByName(charName);
        List<IRole> cosmeticRoles = command.user.getCosmeticRoles(command);
        List<Long> cosmeticRoleIDs = cosmeticRoles.stream().map(iRole -> iRole.getLongID()).collect(Collectors.toList());
        String cosmeticString = command.guild.config.moduleRoles ? " and cosmetic roles" : "";
        if (selectedChar != null) {
            if (selectedChar.getUserID() != command.user.longID) {
                return "> That is not your character you cannot update it.";
            }
            if (selectedChar.getNickname().equals(command.user.displayName)
                    && selectedChar.getRoleIDs().containsAll(cosmeticRoleIDs)) {
                return "> You haven't updated your details since the last time this character was updated.\n" +
                        "Characters use your nickname " + cosmeticString + " to use as data.";
            }
            selectedChar.update(command.user.displayName, cosmeticRoles);
            String response = "> Your character has been updated using your nickname" + cosmeticString + ".";
            if (isSubtype(command, names[0])) {
                response += "\nIf you were attempting to create a new character and not edit this character you need to specify a different character ID.";
            }
            return response;
        } else {
            int maxChars = command.guild.characters.maxCharsForUser(command);
            int rewardCount = command.user.getRewardValue(command);
            if (userChars.size() == maxChars) {
                if (command.guild.config.modulePixels && command.guild.config.xpGain && rewardCount != 4) {
                    return "> You have reached the maximum allowed characters for your level," +
                            " you will have to either rank up or delete an old character to make room.";
                }
                return "> You have reached the maximum allowed characters. You will have to delete an old character to make room.";
            }
            command.guild.characters.addChar(charName, cosmeticRoles, command.user);
            int remainingSlots = (maxChars - userChars.size() - 1);
            String response = "> New Character Created using your nickname " + cosmeticString + " to fill in data." +
                    "\n(" + remainingSlots + " Character slot";
            if (remainingSlots != 1) response += "s";
            response += "remaining)";
            if (isSubtype(command, names[0])) {
                response += "\nTo update the name";
                if (command.guild.config.moduleRoles) response += " or roles";
                response += " linked to this character just run this command again.";
            }
            return response;
        }
    }

    protected static final String[] NAMES = new String[]{"NewChar", "UpdateChar"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        String cosmetic = command.guild.config.moduleRoles ? "Cosmetic roles and " : "";
        return "Updates/Creates a character with the data from your discord account into the character. (" + cosmetic + "Nickname)";
    }

    protected static final String USAGE = "[Character ID]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CHARACTER;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.CHARACTER;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
