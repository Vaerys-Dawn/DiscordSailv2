package Objects;

import Main.Constants;
import Main.Utility;
import POGOs.*;

import java.util.ArrayList;

/**
 * Created by Vaerys on 20/01/2017.
 */
public class GuildContentObject {
    private String guildID;
    private GuildConfig guildConfig;
    private CustomCommands customCommands;
    private Servers servers;
    private Characters characters;
    private Competition competition;
    private GuildUsers guildUsers;
    private ChannelData channelData;
    public ArrayList<UserRateObject> ratelimiting = new ArrayList<>();
    private int doAdminMention;

    public GuildContentObject(String guildID, GuildConfig guildConfig, CustomCommands customCommands, Servers servers, Characters characters, Competition competition, GuildUsers guildUsers, ChannelData channelData) {
        this.guildID = guildID;
        this.guildConfig = guildConfig;
        this.customCommands = customCommands;
        this.servers = servers;
        this.characters = characters;
        this.competition = competition;
        this.guildUsers = guildUsers;
        this.channelData = channelData;
    }

    public ChannelData getChannelData() {
        return channelData;
    }

    public boolean rateLimit(String userID) {
        int max = guildConfig.messageLimit;
        if (max == -1) {
            return false;
        }
        boolean isfound = false;
        for (UserRateObject r : ratelimiting) {
            if (r.userID.equals(userID)) {
                r.counterUp();
                isfound = true;
                if (r.counter > max) {
                    return true;
                }
            }
        }
        if (!isfound) {
            ratelimiting.add(new UserRateObject(userID));
        }
        return false;
    }

    public void resetRateLimit() {
        ratelimiting.clear();
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
        Utility.flushFile(guildID, Constants.FILE_GUILD_USERS, guildUsers, guildUsers.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_CHANNEl_DATA, channelData, true);
    }

    public int getUserRate(String userID) {
        for (UserRateObject u : ratelimiting) {
            if (u.userID.equals(userID)) {
                return u.counter;
            }
        }
        return 0;
    }

    public GuildUsers getGuildUsers() {
        return guildUsers;
    }

    public int getDoAdminMention() {
        return doAdminMention;
    }

    public void setDoAdminMention(int doAdminMention) {
        this.doAdminMention = doAdminMention;
    }
}
