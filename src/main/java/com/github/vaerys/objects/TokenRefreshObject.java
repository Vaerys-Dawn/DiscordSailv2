package com.github.vaerys.objects;

public class TokenRefreshObject {
    String access_token;
    String refresh_token;
    long expires_in;
    String scope;
    String token_type;

    public String getAccessToken() {
        return access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public long getExpiresIn() {
        return expires_in;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return token_type;
    }
}
