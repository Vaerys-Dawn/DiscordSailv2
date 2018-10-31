package com.github.vaerys.pogos;

import com.github.vaerys.main.Constants;
import com.github.vaerys.objects.BlackListObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 04/04/2017.
 */

// TODO: 04/04/2017 add to backups and load ups.
public class BlackLists {
    ArrayList<BlackListObject> blackListObjects = new ArrayList<>();

    public List<BlackListObject> getBlackListObjects() {
        blackListObjects.sort(Comparator.comparing(BlackListObject::getType));
        return blackListObjects;
    }

    public List<BlackListObject> getPending() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_PENDING)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getBlockedForAll() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_ALL)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getBelowTrusted() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_TRUSTED)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getCustomCommands() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_CC)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getServerListings() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_SERVER)).collect(Collectors.toList());
        return list;
    }


}
