package ca.obsidiantech.mcmod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.lingala.zip4j.ZipFile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Copy extends Thread {
  public static String pluginpath = "plugins/McModServer";
  public static String mojangAPI = "https://api.minecraftservices.com/minecraft/profile/lookup/name/";
  public static CommandSender person = null;
  @Override
  public void run() {
    try {
      FileFilter filter = new FileFilter() {
        public boolean accept(File file) {
          if (file.getName().endsWith(".lock")) {
            return false;
          }
          return true;
        }
      };
      //copying all the worlds
      File worldSrc = new File("world");
      File worldDest = new File(pluginpath + "/world");
      FileUtils.copyDirectory(worldSrc, worldDest, filter);
      person.sendMessage("Copying.(1/6)");

      File netherSrc = new File("world_nether/DIM-1");
      File netherDest = new File(pluginpath + "/world/DIM-1");
      FileUtils.copyDirectory(netherSrc, netherDest, filter);
      person.sendMessage("Copying..(2/6)");

      File endSrc = new File("world_the_end/DIM1");
      File endDest = new File(pluginpath + "/world/DIM1");
      FileUtils.copyDirectory(endSrc, endDest, filter);
      person.sendMessage("Copying...(3/6)");

      if (Bukkit.getOnlineMode() == false) {
        convertUsers(pluginpath);
        person.sendMessage("Processing.(4/6)");
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning("Error Copying files (or processing player data)");
    }
    try {
    	String randtemp = generateRand();
        String zipPath = Settings.webFolderPath + randtemp + Settings.worldName + Main.gamemode + ").zip";
        String zipUrl = Settings.baseUrl + randtemp + Settings.worldName + Main.gamemode + ").zip";
    	person.sendMessage("Zipping..(5/6) (this will take a while)");
	    new ZipFile(pluginpath + "/world.zip").addFolder(new File(pluginpath + "/world"));
	    person.sendMessage("Finishing up...(6/6)");
	    FileUtils.moveFile(new File(pluginpath + "/world.zip"), new File(zipPath));

        person.sendMessage("Here's your world download:");
	    sendLink(zipUrl);
        Main.lastDownload = new Date().getTime();
        Main.lastDownloadPath = zipUrl;

	    FileUtils.deleteDirectory(new File(pluginpath + "/world"));
	    Main.inProgress = false;
	
	    } catch (Exception e1) {
	      Bukkit.getLogger().warning("error");
	    }

  }
  public static void cleanDownloads() {
	  //enumerates through all the folders in the world downloads folder 
	  //and deletes them if they are older than (delhours) from now
	  File dir = new File(Settings.webFolderPath);
      String names[] = dir.list();
      for (int x = 0; x < names.length; x++) {
        try {
          File del = new File(Settings.webFolderPath + names[x]);
          long date = new Date().getTime() - del.lastModified();
          if (date > Settings.delhours * 60 * 60 * 1000) {
            if (FileUtils.deleteQuietly(del) == true) {
              Bukkit.getLogger().info("Deleted file (" + names[x] + ") in webpath");
            }else{
              Bukkit.getLogger().warning("Was not able to delete file (" + names[x] + ")");
            }
          }
        }catch(Exception fileDelError){
          Bukkit.getLogger().warning("Encountered an error deleting files in the the webpath");
        }
      }
  }
  public static void sendLink(String link) {
    TextComponent t = new TextComponent();
    t.setText(link);
    t.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
    t.setColor(ChatColor.GREEN);
    t.setUnderlined(true);
    person.spigot().sendMessage(t);
  }

  public static void convertUsers(String path) {
    OfflinePlayer[] players = Bukkit.getOfflinePlayers();
    for (int y = 0; y < players.length; y++) {
      File datSrc = new File(path + "/world/playerdata/" + getServerUUID(players[y]) + ".dat");
      String onlinename = getUUID(players[y].getName());
      File datDest = new File(path + "/world/playerdata/" + onlinename + ".dat");
      try {
        if (onlinename == "") {} else {
          FileUtils.copyFile(datSrc, datDest);
        }
      } catch (Exception e) {
        Bukkit.getLogger().warning("error at player");
      }
    }
  }
  public static String getUUID(String user) {
    try {
      StringBuilder output = new StringBuilder();
      URL api = new URL(mojangAPI + user);
      HttpURLConnection con = (HttpURLConnection) api.openConnection();
      con.setRequestMethod("GET");
      if (con.getResponseCode() != 200) {
        return "";
      }
      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(con.getInputStream()))) {
        for (String line;
          (line = reader.readLine()) != null;) {
          output.append(line);
        }
      }
      JsonObject jsonObject = new JsonParser().parse(output.toString()).getAsJsonObject();
      UUID uuid = java.util.UUID.fromString(jsonObject.get("id").getAsString().replaceFirst(
        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
      ));
      return uuid.toString();

    } catch (Exception e) {
      Bukkit.getLogger().warning("Error with mojang api, please wait 10 minutes.");
      return null;
    }
  }
  public static String getServerUUID(OfflinePlayer user) {
    return user.getUniqueId().toString();
  }
  public static String generateRand() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVQXYZ1234567890";
    String random = "";
    for (int x = 0; x < 10; x++) {
      random += chars.charAt((int)(Math.random() * 36));
    }
    return random;
  }

}