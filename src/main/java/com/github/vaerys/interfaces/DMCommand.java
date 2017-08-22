package com.github.vaerys.interfaces;

import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 05/02/2017.
 */
public interface DMCommand extends Command {

    //Channel Constants

    @Override
    default String channel() {
        return CHANNEL_DM;
    }

    default Permissions[] perms() {
        return new Permissions[0];
    }

    default boolean doAdminLogging() {
        return false;
    }

    default String dualDescription() {
        return null;
    }

    default String dualUsage() {
        return null;
    }

    default String dualType() {
        return null;
    }

    default Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
