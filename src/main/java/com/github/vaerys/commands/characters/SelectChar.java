package com.github.vaerys.commands.characters;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SelectChar extends Command {

    private final static Logger logger = LoggerFactory.getLogger(SelectChar.class);

    @Override
    public String execute(String args, CommandObject command) {
        for (CharacterObject c : command.guild.characters.getCharacters(command.guild.get())) {
            if (c.getName().equalsIgnoreCase(args)) {
                if (c.getUserID() == command.user.longID) {
                    List<IRole> userRoles = command.guild.get().getRolesForUser(command.user.get());
                    //resets User roles back to scratch.
                    for (int i = 0; i < userRoles.size(); i++) {
                        if (command.guild.config.isRoleCosmetic(userRoles.get(i).getLongID())) {
                            userRoles.remove(i);
                        }
                    }
                    //loads new roles.
                    for (long r : c.getRoleIDs()) {
                        userRoles.add(command.guild.getRoleByID(r));
                    }
                    RequestHandler.roleManagement(command.user.get(), command.guild.get(), userRoles);
                    RequestHandler.updateUserNickName(command.user.get(), command.guild.get(), c.getNickname());
                    return "> Character " + c.getNickname() + " Loaded.";
                } else {
                    return "> " + c.getName() + " is not your character.";
                }
            }
        }
        return Constants.ERROR_CHAR_NOT_FOUND;
    }

    protected static final String[] NAMES = new String[]{"Char", "SelChar", "SelectChar"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Selects a Character.";
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
