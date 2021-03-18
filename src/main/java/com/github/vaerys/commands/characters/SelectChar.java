package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Constants;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CharacterObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SelectChar extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        for (CharacterObject c : command.guild.characters.getCharacters(command.guild.get())) {
            if (c.getName().equalsIgnoreCase(args)) {
                if (c.getUserID() == command.user.longID) {
                    List<Role> userRoles = new ArrayList<>(command.user.roles);
                    //resets User roles back to scratch.
                    userRoles.removeIf(r -> command.guild.config.isRoleCosmetic(r.getIdLong()));
                    //loads new roles.
                    for (long r : c.getRoleIDs()) {
                        userRoles.add(command.guild.getRoleById(r));
                    }
                    command.guild.get().modifyMemberRoles(command.user.getMember(), userRoles).complete();
                    command.guild.get().modifyNickname(command.user.getMember(), c.getEffectiveName()).complete();
                    return "\\> Character " + c.getEffectiveName() + " Loaded.";
                } else {
                    return "\\> " + c.getName() + " is not your character.";
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
    protected Permission[] perms() {
        return new Permission[0];
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
        // does nothing
    }
}
