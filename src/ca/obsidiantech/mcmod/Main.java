package ca.obsidiantech.mcmod;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

class Settings{
	public static int delhours = 1;
	public static String worldName = "/Techy's Server(";
	public static String webFolderPath = "/webpath/worlds/";
	public static String baseUrl = "https://files.obsidiantech.ca/worlds/";
}

public class Main extends JavaPlugin {
	public static boolean inProgress= false;
	public static String gamemode = Bukkit.getDefaultGameMode().toString().toLowerCase();
	public static long lastDownload = -1;
	public static String lastDownloadPath = "";
    public void onEnable() {
		getDataFolder().mkdirs();

	}
    @Override
    public void onDisable() {}
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("modadmin")) {
        	if (inProgress) sender.sendMessage("Error, world download in progress");
			else {
        	Copy.cleanDownloads();
			}
        	return true;
        }
        if (command.getName().equalsIgnoreCase("getworld")) {
        	if (checkLastDown()) {
				sender.sendMessage("Recent world download found");
				Copy.person = sender;
				Copy.sendLink(lastDownloadPath);
        	}else {
				sender.sendMessage("No recent download found");
				sender.sendMessage("Executing world download");
				if (inProgress) {
					sender.sendMessage("World download in progress, please wait a bit.");
				}else{
					inProgress = true;
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
					Copy threadcopy = new Copy();
					threadcopy.person = sender;
					threadcopy.start();
				}
        	}
        	return true;
        }
        return false;    
    }
	public static boolean checkLastDown(){
		if (Main.lastDownload == -1){
			return false;
		}
		long difference = new Date().getTime() - Main.lastDownload;
		return difference <= Settings.delhours * 60 * 60 * 1000;
	}
}