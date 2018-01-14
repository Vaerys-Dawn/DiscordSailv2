package com.github.vaerys.handlers;

import com.github.vaerys.main.Utility;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Vaerys on 04/06/2016.
 */
public class FileHandler {

    private final static Logger logger = LoggerFactory.getLogger(FileHandler.class);

    /**
     * Creates Directory using "dirName" as the path.
     */
    public static void createDirectory(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            file.mkdirs();
            logger.info("Directory Created: " + dirName);
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
            logger.trace("Reading from file: " + file);
            return fileContents;
        } catch (IOException e) {
            logger.error(e.getCause().toString());
            return null;
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
            logger.trace("Writing to file: " + file + " at line: " + line);
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
            if (overwrite) {
                FileWriter fileWriter = new FileWriter(file, false);
                fileWriter.write(text);
                fileWriter.flush();
                fileWriter.close();
            } else {
                FileWriter fileWriter = new FileWriter(file, true);
                fileWriter.append(text + "\n");
                fileWriter.flush();
                fileWriter.close();
            }
            logger.trace("Writing to file: " + file);
        } catch (IOException e) {
            Utility.sendStack(e);
        }
    }

    /**
     * Reads from a .Json File using path "file" and returns a POGO based on "objClass".
     */
    public static Object readFromJson(String file, Class<?> objClass) {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(new FileInputStream(new File(file)), StandardCharsets.UTF_8)) {
            Object newObject = gson.fromJson(reader, objClass);
            logger.trace("Reading Data from Json File: " + file + " applying to Object: " + objClass.getName());
            reader.close();
            return newObject;
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            gson.toJson(object, writer);
            logger.trace("Saving " + object.getClass().getName() + " to Json File: " + file);
            writer.close();
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
        try {
            Reader reader = new InputStreamReader(new FileInputStream(new File(filePath)), StandardCharsets.UTF_8);
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(reader);
            reader.close();
            jsonObject = jsonElement.getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static void writeToFile(List<String> contents, String path) {
        String toSave = "";
        for (String s : contents) {
            if (toSave.length() != 0) s += "\n";
            toSave += s;
        }
        writeToFile(path, toSave, true);
    }
}