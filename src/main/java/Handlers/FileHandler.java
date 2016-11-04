package Handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Created by Vaerys on 04/06/2016.
 */
public class FileHandler {

    private final static Logger logger = LoggerFactory.getLogger(FileHandler.class);

    /**Creates Directory using "dirName" as the path.*/
    public static void createDirectory(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            file.mkdirs();
            logger.info("Directory Created: " + dirName);
        }
    }

    /**Reads from File using "file" as the path and returns a ArrayList<String>.*/
    public static List<String> readFromFile(String file) {
        try {
            if (!Paths.get(file).toFile().exists()) {
                Files.createFile(Paths.get(file));
            }
            List<String> fileContents;
            fileContents = Files.readAllLines(Paths.get(file));
            logger.debug("Reading from file: " + file);
            return fileContents;
        } catch (IOException e) {
            logger.error(e.getCause().toString());
            return null;
        }
    }

    /**Writes to File on line "line" using "file" as the path.*/
    public static void writeToFile(String file, int line, String text) {
        try {
            List<String> fileContents = readFromFile(file);
            fileContents.set(line, text);
            Files.write(Paths.get(file), fileContents);
            logger.debug("Writing to file: " + file + " at line: " + line);
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
    }

    /**Writes to File using "file" as the path.*/
    public static void writeToFile(String file, String text){
        try {
            if(!Files.exists(Paths.get(file))){
                Files.createFile(Paths.get(file));
            }
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append(text + "\n");
            fileWriter.flush();
            fileWriter.close();
            logger.debug("Writing to file: " + file);
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
    }

    /**saves data from POGO of type "object" to temp file using String "name".*/
    public static File createTempFile(Object object, String name){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            File file = File.createTempFile(name,".json");
            FileWriter writer = new FileWriter(file);
            gson.toJson(object,writer);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**Reads from a .Json File using path "file" and returns a POGO based on "objClass".*/
    public static Object readFromJson(String file, Class<?> objClass){
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(new FileInputStream(new File(file)), StandardCharsets.UTF_8)) {
            Object newObject = gson.fromJson(reader, objClass);
            logger.debug("Reading Data from Json File: " + file + " applying to Object: " + objClass.getName());
            return newObject;
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
        logger.error("Failed to Read Data, Returning Null");
        return null;
    }

    /**saves data from POGO of type "object" using path "file".*/
    public static void writeToJson(String file, Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            gson.toJson(object, writer);
            logger.debug("Saving Data to Json File: " + file);
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
    }

    private static boolean exists(String path){
        return Files.exists(Paths.get(path));
    }

    public static void initFile(String path, Object object){
        if (!exists(path)) writeToJson(path,object);
    }

    public static void initFile(String path){
        if (!exists(path)) writeToFile(path, "");
    }
}