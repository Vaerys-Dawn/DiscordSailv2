package com.github.vaerys.commands.characters;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

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

    @Override
    protected String[] names() {
        return new String[]{"Char", "SelChar", "SelectChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Selects a Character.";
    }

    @Override
    protected String usage() {
        return "[Character ID]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CHARACTER;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.CHARACTER;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
