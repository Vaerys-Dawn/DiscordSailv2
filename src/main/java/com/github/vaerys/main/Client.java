package com.github.vaerys.main;

import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.github.kennedyoliveira.pastebin4j.PasteBin;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.EventAvatar;
import com.github.vaerys.objects.PatreonAPI;
import com.github.vaerys.objects.TimedEvent;
import com.github.vaerys.objects.TokenRefreshObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.patreon.PatreonOAuth;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        //------------------------------------------client id, client secret, redirect uri
        //PatreonOAuth patreonOAuth = new PatreonOAuth(token.get(0),token.get(1),token.get(2));

        patreonApi = new PatreonAPI(token.get(0));
        try {
            patreonApi.fetchCampaigns();
        } catch (IOException e) {
            if (token.size() == 4) {
                logger.info("Token Invalid attempting to collect new token.");
                refreshPatreonToken(token.get(1), token.get(2), token.get(3), token);
                patreonApi = new PatreonAPI(token.get(0));
            }
        }
        try {
            patreonApi.fetchCampaigns();
            logger.info("Patreon Account Linked.");
        } catch (IOException e) {
            logger.info("Could not Link Patreon Account.");
        }
        checkPatrons();
        return patreonApi;
    }

    public static void initPastebin(List<String> pastebinToken) {
        credentials = new AccountCredentials(pastebinToken.get(0));
        pasteBin = new PasteBin(credentials);
        logger.info("Pastebin Account Linked.");
    }

//    public static ImgurAPI initImgur(List<String> imgurToken) throws IndexOutOfBoundsException {
//        imgurAPI = new ImgurAPI(imgurToken.get(0), imgurToken.get(1));
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

    public static PatreonAPI getPatreonApi() {
        return patreonApi;
    }

    public static void checkPatrons() {
        List<Long> patronIDs = new ArrayList<>();
        try {
            PatreonAPI patreonAPI = getPatreonApi();
            if (patreonAPI == null) return;
            List<Campaign> campaigns = patreonAPI.fetchCampaigns().get();
            for (Campaign c : campaigns) {
                List<Pledge> pledges = patreonAPI.fetchAllPledges(c.getId());
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


    public static void refreshPatreonToken(String clientID, String clientSecret, String refreshToken, List<String> contents) {
        try {
            String refreshURL = "http://www.patreon.com/api/oauth2/";
            String urlArgs = String.format("token?grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s", refreshToken, clientID, clientSecret);
            URL url = new URL(refreshURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setDoOutput(true);
            connection.connect();
            DataOutputStream write = new DataOutputStream(connection.getOutputStream());
            write.writeBytes(urlArgs);
            write.flush();
            write.close();

            logger.info("Sending request to refresh token.");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(reader);
                reader.close();
                stream.close();
                Gson gson = new Gson();
                TokenRefreshObject object = gson.fromJson(jsonElement, TokenRefreshObject.class);
                if (object != null) {
                    logger.info("Token Refreshed");
                    contents.set(0, object.getAccessToken());
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}