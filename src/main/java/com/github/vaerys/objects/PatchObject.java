package com.github.vaerys.objects;

import java.util.ArrayList;

/**
 * Created by Vaerys on 07/04/2017.
 */
@Deprecated
public class PatchObject {
    String patchLevel = "";
    ArrayList<String> patchedGuildIDs = new ArrayList<>();

    public PatchObject(String patchLevel) {
        this.patchLevel = patchLevel;
    }

    public String getPatchLevel() {
        return patchLevel;
    }

    public ArrayList<String> getPatchedGuildIDs() {
        return patchedGuildIDs;
    }
}
