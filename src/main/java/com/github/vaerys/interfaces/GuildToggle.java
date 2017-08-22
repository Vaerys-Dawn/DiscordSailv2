package com.github.vaerys.interfaces;

import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public interface GuildToggle {
    String name();
    boolean toggle(GuildConfig config);
    boolean get(GuildConfig config);
    boolean getDefault();
    void execute(GuildObject guild);
    boolean isModule();
}
