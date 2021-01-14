package com.github.vaerys.templates;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.masterobjects.CommandObject;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 05/02/2017.
 */
public abstract class DMCommand extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return null;
    }

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
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    public void init() {

    }
}
