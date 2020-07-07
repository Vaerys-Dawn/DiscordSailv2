package com.github.vaerys.templates;

import com.github.vaerys.enums.ChannelSetting;
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
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
