package Objects;

import Main.Constants;
import Main.Utility;
import POGOs.*;

import javax.annotation.Nonnull;

/**
 * Created by Vaerys on 20/01/2017.
 */
public class GuildContentObject {
    @Nonnull
    private String guildID;
    @Nonnull
    private GuildConfig guildConfig;
    @Nonnull
    private CustomCommands customCommands;
    @Nonnull
    private Servers servers;
    @Nonnull
    private Characters characters;
    @Nonnull
    private Competition competition;

    public GuildContentObject(String guildID, GuildConfig guildConfig, CustomCommands customCommands, Servers servers, Characters characters, Competition competition) {
        this.guildID = guildID;
        this.guildConfig = guildConfig;
        this.customCommands = customCommands;
        this.servers = servers;
        this.characters = characters;
        this.competition = competition;
    }

    public String getGuildID() {
        return guildID;
    }

    public GuildConfig getGuildConfig() {

        return guildConfig;
    }

    public void setGuildConfig(GuildConfig guildConfig) {
        this.guildConfig = guildConfig;
    }

    public CustomCommands getCustomCommands() {
        return customCommands;
    }

    public void setCustomCommands(CustomCommands customCommands) {
        this.customCommands = customCommands;
    }

    public Servers getServers() {
        return servers;
    }

    public void setServers(Servers servers) {
        this.servers = servers;
    }

    public Characters getCharacters() {
        return characters;
    }

    public void setCharacters(Characters characters) {
        this.characters = characters;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void saveFiles() {
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_CUSTOM, customCommands, customCommands.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_CHARACTERS, characters, characters.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_SERVERS, servers, servers.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_COMPETITION, competition, competition.isProperlyInit());
    }
}
