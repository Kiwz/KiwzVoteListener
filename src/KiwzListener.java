import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;

public class KiwzListener implements VoteListener {
	
	private Logger log = Logger.getLogger("Minecraft");
	private Server server = Bukkit.getServer();
	
	private int amount = 100;
	private String broadcast = "&6{player}&2 has voted for this server;&6{player}'s&2 account was raised with &6{amount}";
	private String sendMsg = "&2Your account was raised with &6{amount}";
	private String logSavePath = "plugins/Votifier";
	
	public KiwzListener() {
		Properties props = new Properties();
		try {
			File configFile = new File("plugins/Votifier/KiwzListener.properties");
			if (!configFile.exists()) {
				configFile.createNewFile();

				// Load the configuration.
				props.load(new FileReader(configFile));

				// Write the default configuration.
				props.setProperty("RewardAmount", Integer.toString(amount));
				props.setProperty("BroadcastMsg", broadcast);
				props.setProperty("sendMsg", sendMsg);
				props.setProperty("LogSavePath", logSavePath);
				props.store(new FileWriter(configFile),
						"KiwzListener Properties\n" +
						"{player} = The name of the voter\n" +
						"{amount} = Amount of money that was given\n" +
						"; = This character will place the following words on a new line\n" +
						"Standard Minecraft color codes are supported\n" +
						"This can be used in BroadcastMsg and sendMsg\n" +
						"Do not use the following character: \\");
				log.info("[Votifier] KiwzListener properties not found, making a fresh one and loading this!");
			}
			else {
				// Load the configuration.
				props.load(new FileReader(configFile));
			}

			amount = Integer.parseInt(props.getProperty("RewardAmount", "100"));
			broadcast = props.getProperty("BroadcastMsg", broadcast);
			sendMsg = props.getProperty("sendMsg", sendMsg);
			logSavePath = props.getProperty("LogSavePath", logSavePath);
			log.info("[Votifier] KiwzListener properties loaded!");
		} catch (Exception ex) {
			log.warning("Unable to load KiwzListener.proterties, using default values");
		}
	}
	
	public void voteMade(Vote vote) {
		
		Format sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String userName = vote.getUsername();
		String service = vote.getServiceName();
		String out = time + " [" + userName + "] Voted from: " + service + "\n";
		Player player = Bukkit.getServer().getPlayer(userName);
		String[] splitBroadcast = broadcast.split(";");
		String[] splitSendMsg = sendMsg.split(";");
		
	    //If voter is a player then give some money + announce it
		Economy econ = server.getServicesManager().getRegistration(Economy.class).getProvider();
		EconomyResponse r = econ.depositPlayer(userName, amount);
		if (r.transactionSuccess()) {
	    	if (broadcast != "") {
	    		for (int i = 0; i < splitBroadcast.length; i++) {
	    			server.broadcastMessage(splitBroadcast[i].replaceAll("(&([a-f0-9]))", "\u00A7$2")
	    					.replace("{player}", userName).replace("{amount}", Integer.toString(amount)));
	    		}
	    	}
		    
		    if (player != null && sendMsg != "") {
		    	for (int i = 0; i < splitSendMsg.length; i++) {
			    	player.sendMessage(splitSendMsg[i].replaceAll("(&([a-f0-9]))", "\u00A7$2")
			    			.replace("{player}", userName).replace("{amount}", Integer.toString(amount)));
		    	}
		    }
	    }
	    
		//Log the vote
	    try {
	    	BufferedWriter writer = new BufferedWriter (new FileWriter(logSavePath + "/votes.txt", true));
			writer.write(out);
			writer.close();
		} catch (Exception ex) {
			log.warning("Unable to log vote: " + out);
		}
	}
}