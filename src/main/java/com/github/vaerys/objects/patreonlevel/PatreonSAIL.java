package com.github.vaerys.objects.patreonlevel;


import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Constants;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PatreonSAIL {

    public static void refreshToken(String clientID, String clientSecret, String refreshToken) {
        String postUrl = "https://www.patreon.com/api/oauth2/token";// put in your url
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(postUrl);
        List<NameValuePair> data = new LinkedList<>();
        data.add(new BasicNameValuePair("grant_type", "refresh_token"));
        data.add(new BasicNameValuePair("refresh_token", refreshToken));
        data.add(new BasicNameValuePair("client_id", clientID));
        data.add(new BasicNameValuePair("client_secret", clientSecret));
        try {
            post.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));

            HttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() >= 400) return;

            InputStream in = entity.getContent();
            try {
                Scanner scanner = new Scanner(in);
                StringHandler builder = new StringHandler();
                while (scanner.hasNextLine()) {
                    if (builder.length() != 0) {
                        builder.append("\n");
                    }
                    builder.append(scanner.nextLine());
                }
                Gson gson = new Gson();
                TokenRefreshObject object = gson.fromJson(builder.toString(), TokenRefreshObject.class);
                refreshFile(object.getAccessToken(), clientID, clientSecret, object.getRefreshToken());
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void refreshFile(String accessToken, String clientID, String clientSecret, String refreshToken) {
        String contents = String.format("%s\n%s\n%s\n%s", accessToken, clientID, clientSecret, refreshToken);
        FileHandler.writeToFile(Constants.FILE_PATREON_TOKEN, contents, true);
    }

}
