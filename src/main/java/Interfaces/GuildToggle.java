package Interfaces;

import Commands.CommandObject;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public interface GuildToggle {
    String name();
    boolean toggle(GuildConfig config);
    boolean get(GuildConfig config);
    void execute(CommandObject command);
    boolean isModule();
}
