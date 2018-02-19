package com.github.vaerys.templates;

import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 05/02/2017.
 */
public abstract class DMCommand extends Command {

    //Channel Constants
    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.FROM_DM;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
