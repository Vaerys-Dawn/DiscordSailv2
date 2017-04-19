package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class RateLimiting implements GuildToggle{

    @Override
    public String name() {
        return "RateLimiting";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.rateLimiting = !config.rateLimiting;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.rateLimiting;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().rateLimiting;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
