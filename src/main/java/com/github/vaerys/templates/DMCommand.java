package com.github.vaerys.templates;

import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 05/02/2017.
 */
public abstract class DMCommand extends Command {

    //Channel Constants

    public String channel() {
        return CHANNEL_DM;
    }

    public Permissions[] perms() {
        return new Permissions[0];
    }

    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }

    public String dualDescription() {
        return null;
    }

    public String dualUsage() {
        return null;
    }

    public String dualType() {
        return null;
    }

    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
