package ac.echo.classes;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class API {

    public API() {}

    public String getRequest(String url_string) {
        URL url;
        try {
            url = new URL(url_string);

            HttpURLConnection con;
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            con.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return content.toString();
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public ArrayList<String> getAlts(String key, String username, CommandSender sender) {
        String meResponse = getRequest("https://api.echo.ac/query/player?key=" + key + "&player=" + username);

        if (meResponse.equals("")) {
            sender.sendMessage(ChatColor.RED + "Couldn't reach Echo servers (API DOWN?).");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject)response;

            if ((Boolean) json_response.get("success")) {
                return (JSONArray) ((JSONObject)json_response.get("result")).get("alts");
            } else {
                sender.sendMessage(ChatColor.RED + "Error from Echo servers: " + (String) json_response.get("message") + ".");
                return null;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getPin(String key, CommandSender sender) {
        String meResponse = getRequest("https://api.echo.ac/query/pin?key=" + key );

        if(meResponse.equals("")){
            sender.sendMessage(ChatColor.RED + "Couldn't reach Echo servers (API DOWN?).");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject)response;

            if((Boolean)json_response.get("success")){
                return (String) ((JSONObject)json_response.get("result")).get("pin");
            } else {
                sender.sendMessage(ChatColor.RED + "Error from Echo servers: " + json_response.get("message") + ".");
                return null;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getLastScan(String key, CommandSender sender){
        String meResponse = getRequest("https://api.echo.ac/query/scans?key=" + key + "&length=1");

        if(meResponse.equals("")){
            sender.sendMessage(ChatColor.RED + "Couldn't reach Echo servers (API DOWN?).");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject)response;

            if ((Boolean)json_response.get("success")){
                String scan = (String) ((JSONObject)json_response.get("result")).get("scans");
                return scan;
            } else {
                sender.sendMessage(ChatColor.RED + "Error from Echo servers: " + json_response.get("message") + ".");
                return null;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getLink(String key, CommandSender sender){
        String meResponse = getRequest("https://api.echo.ac/query/pin?key=" + key);

        if(meResponse.equals("")){
            sender.sendMessage(ChatColor.RED + "Couldn't reach Echo servers (API DOWN?).");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject)response;

            if((Boolean)json_response.get("success")){
                return (String) ((JSONObject)json_response.get("result")).get("link");
            } else {
                sender.sendMessage(ChatColor.RED + "Error from Echo servers: " + json_response.get("message") + ".");
                return null;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String styleCode(String key) {
        String meResponse = getRequest("https://api.echo.ac/query/me?key=" + key);

        if(meResponse.equals("")) {
            System.out.println("Couldn't reach Echo servers (API DOWN?).");
            return "";
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject) response;

            if ((Boolean)json_response.get("success")) {
                String stylecode = (String) ((JSONObject) json_response.get("result")).get("styler_code");
                return stylecode;
            } else {
                System.out.println("Error from Echo servers: " + json_response.get("message"));
                return "";
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public String plan(String key) {
        String meResponse = getRequest("https://api.echo.ac/query/me?key=" + key);

        if(meResponse.equals("")) {
            System.out.println("Couldn't reach Echo servers (API DOWN?).");
            return "";
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject) response;

            if ((Boolean)json_response.get("success")) {
                JSONObject object = (JSONObject) ((JSONObject) json_response.get("result")).get("plan");
                String planName = (String) object.get("name");
                return planName;
            } else {
                System.out.println("Error from Echo servers: " + json_response.get("message"));
                return "";
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public Boolean isValidKey(String key) {
        String meResponse = getRequest("https://api.echo.ac/query/me?key=" + key);

        if(meResponse.equals("")) {
            System.out.println("Couldn't reach Echo servers (API DOWN?).");
            return false;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject) response;
            
            if ((Boolean)json_response.get("success")) {
                return true;
            } else {
                System.out.println("Error from Echo servers: " + json_response.get("message"));
                return false;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
