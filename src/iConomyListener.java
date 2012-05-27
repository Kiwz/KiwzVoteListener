import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.nijikokun.register.payment.Methods;
import com.vexsoftware.votifier.Votifier;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;


public class iConomyListener implements VoteListener
{
	
	public void voteMade(Vote vote)
	{
		//Start iConomy
	    if (Methods.getMethod().hasAccount(vote.getUsername()))
	    {
	    	Methods.getMethod().getAccount(vote.getUsername()).add(200);
	    	
		    Server server = Votifier.getInstance().getServer();
		    server.broadcastMessage(ChatColor.DARK_RED + vote.getUsername() + ChatColor.RED + " har stemt på MCLarvik og dermed fått 200 Dollar!");
		    server.broadcastMessage(ChatColor.GREEN + "Info om hvordan du stemmer finner du på http://mclarvik.net");
		    
	    	Player player = Bukkit.getServer().getPlayer(vote.getUsername());
	    	if (player != null)
	    	{
	    		player.sendMessage(ChatColor.GOLD + "Du har fått 200 Dollar!");
	    	}
	    }
	    //End iConomy
	    
		//Start logger
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/dynmap/web/admin/generert/votes.txt", true));
			
			writer.write(vote.toString());
			writer.newLine();
			writer.flush();
			writer.close();
		}
		
		catch (Exception ex)
		{
			Logger.getLogger("iConomyListener").log(Level.WARNING, "Unable to log vote: " + vote);
	    }
		//End logger
	}	
}