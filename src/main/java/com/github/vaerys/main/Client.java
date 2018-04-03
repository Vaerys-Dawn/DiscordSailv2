package com.github.vaerys.main;

import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.github.kennedyoliveira.pastebin4j.PasteBin;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.objects.EventAvatar;
import com.github.vaerys.objects.PatreonAPI;
import com.github.vaerys.objects.TimedEvent;
import com.patreon.PatreonOAuth;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Client {
    final static Logger logger = LoggerFactory.getLogger(Client.class);
    private static PatreonAPI patreonApi = null;
    private static IDiscordClient client = null;
    private static AccountCredentials credentials = null;
    private static PasteBin pasteBin = null;
    private static PatreonOAuth patreonOAuth = null;
//    private static ImgurAPI imgurAPI = null;

    public static IDiscordClient createClient(String token, boolean login) throws DiscordException {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        clientBuilder.setMaxReconnectAttempts(4000);
        if (login) {
            logger.info("Logging in to Discord");
            client = clientBuilder.login();
        } else {
            client = clientBuilder.build();
        }
        return client;
    }

    public static IDiscordClient getClient() {
        return client;
    }


    public static void main(String[] args) {
        IDiscordClient client = new ClientBuilder().withToken(args[0]).build();
        client.getDispatcher().registerListener((IListener<ReadyEvent>) readyEvent -> System.out.println("login successful"));
        client.login();
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
        List<Long> patronIDs = new ArrayList<>();
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
        Image avatar = Image.forFile(avatarFile);
        if (event != null && event.doRotateAvatars()) {
            EventAvatar eventAvatar = event.getAvatarDay(timeNow.getDayOfWeek());
            if (eventAvatar != null) {
                avatar = Image.forUrl(FilenameUtils.getExtension(eventAvatar.getLink()), eventAvatar.getLink());
            }
        }
        RequestHandler.updateAvatar(avatar);
        //wait for the avatar to update properly
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
        }
    }


    public static void refreshPatreonToken(String clientID, String clientSecret, String refreshToken) {
        try {
            patreonOAuth = new PatreonOAuth(clientID, clientSecret, "");
            PatreonOAuth.TokensResponse refresh = patreonOAuth.refreshTokens(refreshToken);
            StringHandler tokenData = new StringHandler();
            tokenData.append(refresh.getAccessToken() + "\n");
            tokenData.append(clientID + "\n");
            tokenData.append(clientSecret + "\n");
            tokenData.append(refresh.getRefreshToken());
            FileHandler.writeToFile(Constants.FILE_PATREON_TOKEN, tokenData.toString(), true);
            patreonApi = new PatreonAPI(refresh.getAccessToken());
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
}
