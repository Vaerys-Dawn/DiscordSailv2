package com.github.vaerys.objects;

/**
 * Created by Vaerys on 29/08/2016.
 */
public class CompObject {
    String userName;
    String userID;
    String fileName;
    String fileUrl;
    String timeSubmitted;

    public CompObject(String userName, String userID, String fileName, String fileUrl, String timeSubmitted) {
        this.userName = userName;
        this.userID = userID;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.timeSubmitted = timeSubmitted;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUserID() {
        return userID;
    }
}
