package com.github.vaerys.handlers;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.google.gson.*;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Vaerys on 04/06/2016.
 */
public class FileHandler {

    private FileHandler() {
        // utility class
    }

    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);

    /**
     * Creates Directory using "dirName" as the path.
     */
    public static void createDirectory(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            boolean created = file.mkdirs();
            logger.info("Directory {}: {}", created ? "Created" : "Could not be created", dirName);
        }
    }

    /**
     * Reads from File using "file" as the path and returns a ArrayList<String>.
     */
    public static List<String> readFromFile(String file) {
        try {
            if (!Paths.get(file).toFile().exists()) {
                Files.createFile(Paths.get(file));
            }
            List<String> fileContents;
            fileContents = Files.readAllLines(Paths.get(file));
            logger.trace("Reading from file: {}", file);
            return fileContents;
        } catch (IOException e) {
            logger.error(e.getCause().toString());
            return new ArrayList<>();
        }
    }

    /**
     * Writes to File on line "line" using "file" as the path.
     */
    public static void writeToFile(String file, int line, String text) {
        try {
            List<String> fileContents = readFromFile(file);
            fileContents.set(line, text);
            Files.write(Paths.get(file), fileContents);
            logger.trace("Writing to file: {} at line: {}", file, line);
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
    }

    /**
     * Writes to File using "file" as the path.
     */
    public static void writeToFile(String file, String text, boolean overwrite) {
        try {
            if (!Files.exists(Paths.get(file))) {
                Files.createFile(Paths.get(file));
            }
        } catch (IOException e) {
            Utility.sendStack(e);
        }
        try (FileWriter fileWriter = new FileWriter(file, !overwrite)) {
            if (overwrite) {
                fileWriter.write(text);
            } else {
                fileWriter.append("\n").append(text);
            }
            fileWriter.flush();
            logger.trace("Writing to file: {}", file);
        } catch (IOException e) {
            Utility.sendStack(e);
        }
    }

    /**
     * Reads from a .Json File using path "file" and returns a POGO based on "objClass".
     */
    public static Object readFromJson(String file, Class<?> objClass) {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Object newObject = gson.fromJson(reader, objClass);
            logger.trace("Reading Data from Json File: {} applying to Object: {}", file, objClass.getName());
            return newObject;
        } catch (JsonSyntaxException e) {
            logger.error(file);
            Utility.sendStack(e);
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
        logger.error("Failed to Read Data, Returning Null");
        return null;
    }

    /**
     * saves data from POGO of type "object" using path "file".
     */
    public static void writeToJson(String file, Object object) {
        if (object == null) return;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            gson.toJson(object, writer);
            logger.trace("Saving {} to Json File: {}", object.getClass().getName(), file);
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
    }

    public static boolean exists(String path) {
        return Files.exists(Paths.get(path));
    }

    public static void initFile(String path, Object object) {
        if (!exists(path)) writeToJson(path, object);
    }

    public static void initFile(String path) {
        if (!exists(path)) writeToFile(path, "", false);
    }

    public static JsonObject fileToJsonObject(String filePath) {
        JsonObject jsonObject = new JsonObject();
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(reader);
            jsonObject = jsonElement.getAsJsonObject();
        } catch (IllegalStateException e) {
            logger.error("{} failed to init please check file.", filePath);
        } catch (JsonSyntaxException e) {
            logger.error("File \"{}\" has malformed json data or is corrupted.\n", filePath);
            Utility.sendStack(e);
        } catch (IOException e) {
            Utility.sendStack(e);
        }
        return jsonObject;
    }

    public static void writeToFile(List<String> contents, String path) {
        StringBuilder toSave = new StringBuilder();
        for (String s : contents) {
            if (toSave.length() != 0) s += "\n";
            toSave.append(s);
        }
        writeToFile(path, toSave.toString(), true);
    }

    /***
     * Handler to read the getContents of a discord attachment.
     *
     * @param attachment the file to be decoded.
     * @return the getContents of the file.
     */
    public static String readFromFile(Message.Attachment attachment) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(attachment.getUrl()).openConnection();
            connection.setRequestProperty("User-Agent", Constants.MOZILLA_USER_AGENT);
            Scanner s = new Scanner(connection.getInputStream());
            StringHandler content = new StringHandler();
            while (s.hasNextLine()) {
                if (content.length() != 0) content.append("\n");
                content.append(s.nextLine());
            }
            s.close();
            return content.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static boolean isEmpty(String filePath) {
        if (filePath == null || !exists(filePath)) return true;
        return String.join("", FileHandler.readFromFile(filePath)).isEmpty();
    }

    public static File copyToFile(String filePath, InputStream file) {
        Path newFile = Paths.get(filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            if (!exists(filePath)) {
                Files.createFile(newFile);
            }
            IOUtils.copy(file, writer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Utility.sendStack(e);
        }
        return new File(filePath);
    }
}