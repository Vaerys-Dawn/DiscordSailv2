package com.github.vaerys.main;

import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.github.kennedyoliveira.pastebin4j.PasteBin;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.listeners.*;
import com.github.vaerys.masterobjects.ClientObject;
import com.github.vaerys.objects.events.EventAvatar;
import com.github.vaerys.objects.events.TimedEvent;
import com.github.vaerys.objects.patreonlevel.PatreonSAIL;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.patreon.PatreonAPI;
import com.patreon.PatreonOAuth;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Client {
    final static Logger logger = LoggerFactory.getLogger(Client.class);
    private static PatreonAPI patreonApi = null;
    private static JDA client = null;
    private static AccountCredentials credentials = null;
    private static PasteBin pasteBin = null;
    private static PatreonOAuth patreonOAuth = null;
    private static ClientObject clientObject = null;

    public static JDA createClient(String token) throws LoginException {
        ThreadPoolExecutor eventPool = new ThreadPoolExecutor(10, 50, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ScheduledExecutorService gatewayPool = new ScheduledThreadPoolExecutor(10);
        JDABuilder clientBuilder = JDABuilder.createDefault(token);
        clientBuilder.setEventPool(eventPool);
        clientBuilder.setGatewayPool(gatewayPool);
        clientBuilder.setMaxReconnectDelay(4000);
        clientBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES);
        clientBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        clientBuilder.addEventListeners(new ReadyListener(), new LoggingListener(), new EventListener(), new CreatorListener(), new GuildEventListener());
        client = clientBuilder.build();
        return client;
    }

    public static JDA getClient() {
        return client;
    }


    public static void main(String[] args) {
        JsonObject object = FileHandler.fileToJsonObject(Constants.DIRECTORY_STORAGE + "content.json");
        JsonArray array = object.getAsJsonArray("Changes");
        List<JsonElement> sorting = new ArrayList<>(array.size());
        array.forEach(j -> sorting.add(j));
        sorting.sort(Comparator.comparing(o -> o.getAsString()));
        sorting.forEach(j -> array.set(sorting.indexOf(j), j));
        FileHandler.writeToJson(Constants.DIRECTORY_STORAGE + "content.json", object);
    }


    public static PatreonAPI initPatreon(List<String> token) throws IndexOutOfBoundsException {
        patreonApi = new PatreonAPI(token.get(0));

        checkPatrons();
        return patreonApi;
    }

    public static void initPastebin(List<String> pastebinToken) {
        credentials = new AccountCredentials(pastebinToken.get(0));
        pasteBin = new PasteBin(credentials);
        logger.info("Pastebin Account Linked.");
    }

//    public static ImgurAPI initImgur(List<String> imgurToken) throws IndexOutOfBoundsException {
//        imgurAPI = new ImgurAPI(imgurToken.getAllToggles(0), imgurToken.getAllToggles(1));
//        logger.info("Imgur Account Linked.");
//        return imgurAPI;
//    }
//
//    public static ImgurAPI getImgurAPI() {
//        return imgurAPI;
//    }

    public static PasteBin getPastebin() {
        return pasteBin;
    }


    public static void checkPatrons() {
        if (!checkPatreonIsValid()) return;
        List<Long> patronIDs = new ArrayList<Long>() {{
            add(153159020528533505L);
        }};
        try {
            if (patreonApi == null) return;
            List<Campaign> campaigns = patreonApi.fetchCampaigns().get();
            for (Campaign c : campaigns) {
                List<Pledge> pledges = patreonApi.fetchAllPledges(c.getId());
                if (pledges != null) {
                    for (Pledge p : pledges) {
                        if (p.getReward().getTitle().equalsIgnoreCase("Pioneer")) {
                            try {
                                long userID = Long.parseUnsignedLong(p.getPatron().getSocialConnections().getDiscord().getUser_id());
                                patronIDs.add(userID);
                            } catch (NumberFormatException e) {
                                //skip
                            }
                        }
                    }
                }
            }
            logger.info("Patron List Updated.");
        } catch (IOException e) {
            //nothing happens
        }
        Globals.setPatrons(patronIDs);
    }

    private static boolean checkPatreonIsValid() {
        List<String> token = FileHandler.readFromFile(Constants.FILE_PATREON_TOKEN);
        try {
            patreonApi.fetchCampaigns();
        } catch (IOException e) {
            if (token.size() == 4) {
                logger.info("Token Invalid attempting to collect new token.");
                refreshPatreonToken(token.get(1), token.get(2), token.get(3));
            }
        }
        if (FileHandler.exists(Constants.DIRECTORY_STORAGE + "manual_patron.txt")) {
            List<Long> patronIDs = FileHandler.readFromFile(Constants.DIRECTORY_STORAGE + "manual_patron.txt").stream()
                    .map(s -> Utility.stringLong(s)).filter(l -> l != -1).distinct().collect(Collectors.toList());
            Globals.setPatrons(patronIDs);
            logger.info("Patron List Updated.");
            return false;
        }
        try {
            patreonApi.fetchCampaigns();
            logger.info("Patreon Account Linked.");
        } catch (IOException e) {
            logger.info("Could not Link Patreon Account.");
            return false;
        }

        return true;
    }

    public static void handleAvatars() {
        TimedEvent event = Globals.getCurrentEvent();
        ZonedDateTime timeNow = ZonedDateTime.now(ZoneOffset.UTC);
        String dailyFileName = Globals.dailyAvatarName.replace("#day#", timeNow.getDayOfWeek().toString());
        File avatarFile;

        //sets Avatar.
        if (Globals.doDailyAvatars) {
            avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + dailyFileName);
        } else {
            avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.defaultAvatarFile);
        }

        try {
            Icon avatar = Icon.from(avatarFile);
            if (event != null && event.doRotateAvatars()) {
                EventAvatar eventAvatar = event.getAvatarDay(timeNow.getDayOfWeek());
                if (eventAvatar != null) {
                    InputStream stream = Utility.getImageStreamFromURL(eventAvatar.getLink());
                    if (stream != null) Icon.from(stream, Icon.IconType.PNG);
                }
            }
            client.getSelfUser().getManager().setAvatar(avatar).complete();
        } catch (IOException e) {
            Utility.sendStack(e);
        } catch (ErrorResponseException e) {
            if (e.getResponse().isRateLimit()) {
                logger.info("Rate limit reached for avatar changing.");
            }
        }

        //wait for the avatar to update properly
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
            Thread.currentThread().interrupt();
        }
    }

    public static void refreshPatreonToken(String clientID, String clientSecret, String refreshToken) {
        PatreonSAIL.refreshToken(clientID, clientSecret, refreshToken);
        String access = FileHandler.readFromFile(Constants.FILE_PATREON_TOKEN).get(0);
        patreonApi = new PatreonAPI(access);
        try {
        patreonOAuth = new PatreonOAuth(clientID, clientSecret, "");
            PatreonOAuth.TokensResponse refresh = patreonOAuth.refreshTokens(refreshToken);
            StringHandler tokenData = new StringHandler();
            tokenData.append(refresh.getAccessToken() + "\n");
            tokenData.append(clientID + "\n");
            tokenData.append(clientSecret + "\n");
            tokenData.append(refresh.getRefreshToken());
            FileHandler.copyToFile(Constants.FILE_PATREON_TOKEN, new ByteArrayInputStream(tokenData.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 401) {
                logger.error("Refresh Token is invalid.");
                return;
            }
            Utility.sendStack(e);
        } catch (IOException e) {
            Utility.sendStack(e);
        }
    }

    public static ClientObject getClientObject() {
        if (clientObject == null) {
            clientObject = new ClientObject();
        }
        return clientObject;
    }
}
