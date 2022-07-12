package ca.obsidiantech.mcmod;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
	public static boolean inProgress= false;
	public static String webpath = "/webpath/worlds/";
	public static String downpath = "https://files.obsidiantech.ca/worlds/";
	public static String lastWD = null;
    public void onEnable() {
		getDataFolder().mkdirs();
		Copy.webpath = webpath;
		Copy.downpath = downpath;
	}
    @Override
    public void onDisable() {}
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("modadmin")) {
        	if (inProgress == true) {
        		sender.sendMessage("Error, world download in progress");
        	}else {
            	
            	
        	}
        	return true;
        }
        
        if (command.getName().equalsIgnoreCase("getworld")) {
        	if (lastWD == null) {
        		sender.sendMessage("No recent download found");
        		sender.sendMessage("Executing world download");
        		if (inProgress == true) {
            		sender.sendMessage("World download in progress, please wait a bit.");
            	}else{
            		inProgress = true;
                	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");        	
                	Copy threadcopy = new Copy();
                	threadcopy.person = sender;
                	threadcopy.start();
            	}
        	}else {
        		sender.sendMessage("Recent world download found");
        		Copy.person = sender;
        		Copy.sendLink(lastWD);
        	}
        	
        	
        	return true;
        }
        return false;    
    }
    
    
    
    
    
    
}