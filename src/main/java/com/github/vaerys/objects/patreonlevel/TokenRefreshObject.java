package com.github.vaerys.objects.patreonlevel;

import com.google.gson.annotations.SerializedName;

public class TokenRefreshObject {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("expires_in")
    private String expiresIn;
    @SerializedName("scope")
    private String scope;
    @SerializedName("token_type")
    private String tokenType;

    public TokenRefreshObject() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return tokenType;
    }
}
