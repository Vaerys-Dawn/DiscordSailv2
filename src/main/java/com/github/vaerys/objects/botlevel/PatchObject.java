package com.github.vaerys.objects.botlevel;

import com.github.vaerys.pogos.GuildUsers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class PatchObject {
    JsonObject object;
    String path;
    double patchID;

    public PatchObject(JsonObject object, String path, double patchID) {
        this.object = object;
        this.path = path;
        this.patchID = patchID;
    }

    public JsonObject getObject() {
        return object;
    }

    public String getPath() {
        return path;
    }

    public double getPatchID() {
        return patchID;
    }
}
