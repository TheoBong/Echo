package ac.echo.classes;

import ac.echo.Echo;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("unchecked")
public class Storage {
    private final File folder;
    private final String location = "staff.json";
    private Map<String, String> users;

    public Storage(Echo echo) {
        folder = echo.getDataFolder();
        users = new HashMap<>();
    }

    public void exportConfig() {
        File file = new File(folder, location);

        try {
            file.createNewFile();

            JSONObject mainObject = new JSONObject();
            mainObject.put("users", new JSONObject(users));

            FileWriter fileWriter = new FileWriter(file.getCanonicalFile());
            fileWriter.write(mainObject.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void importConfig() {
        JSONParser parser = new JSONParser();

        File file = new File(folder, location);

        try {
            if(file.createNewFile()){

                JSONObject mainObject = new JSONObject();
                mainObject.put("users", new JSONObject(users));

                FileWriter fileWriter = new FileWriter(file.getCanonicalFile());
                fileWriter.write(mainObject.toJSONString());
                fileWriter.close();
            }
        } catch (IOException ignored) {}

        File newFile = new File(folder, location);

        Scanner fileReader;
        try {
            fileReader = new Scanner(newFile);

            try {
                Object config = parser.parse(fileReader.nextLine());
                JSONObject jConfig = (JSONObject)config;

                users = ((HashMap)jConfig.get("users"));


            } catch (ParseException e) {
                Bukkit.getLogger().warning(e.getMessage());
                e.printStackTrace();
            }

        } catch (FileNotFoundException ignored) {
        }


    }

    public String getConsole(){
        if(!users.containsKey("console")){
            return null;
        }
        return users.get("console");
    }

    public void addConsole(String key) {
        users.put("console", key);
        exportConfig();
    }

    public Boolean keyUsed(String key){
        return users.containsValue(key);
    }

    public String getKey(String uuid){
        if(!users.containsKey(uuid)){
            return null;
        }
        return users.get(uuid);
    }

    public void addUser(String uuid, String key) {
        users.put(uuid, key);
        exportConfig();
    }

}
