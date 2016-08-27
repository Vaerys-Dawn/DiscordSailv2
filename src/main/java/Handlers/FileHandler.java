package Handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Vaerys on 04/06/2016.
 */
public class FileHandler {

    private final static Logger logger = LoggerFactory.getLogger(FileHandler.class);

    /**Creates Directory using "dirName" as the path.*/
    public void createDirectory(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            file.mkdirs();
            logger.info("Directory Created: " + dirName);
        }
    }

    /**Reads from File using "file" as the path and returns a ArrayList<String>.*/
    public List<String> readFromFile(String file) {
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
    public void writeToFile(String file, int line, String text) {
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
    public void writeToFile(String file, String text){
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

    /**Reads from a .Json File using path "file" and returns a POGO based on "objClass".*/
    public Object readfromJson(String file, Class<?> objClass){
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            Object newObject = gson.fromJson(reader, objClass);
            logger.debug("Reading Data from Json File: " + file + "applying to Object: " + objClass.getName());
            return newObject;
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
        logger.error("Failed to Read Data, Returning Null");
        return null;
    }

    /**saves data from POGO of type "object" using path "file".*/
    public void writetoJson(String file, Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(object, writer);
            logger.debug("Saving Data to Json File: " + file);
        } catch (IOException e) {
            logger.error(e.getCause().toString());
        }
    }

    private boolean exists(String path){
        return Files.exists(Paths.get(path));
    }

    public void initFile(String path, Object object){
        if (!exists(path)) writetoJson(path,object);
    }

    public void initFile(String path){
        if (!exists(path)) writeToFile(path, "");
    }
}