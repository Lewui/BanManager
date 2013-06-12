package me.confuserr.banmanager.Listeners;

import me.confuserr.banmanager.BanManager;
import me.confuserr.banmanager.Util;
import me.confuserr.banmanager.data.MuteData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

@SuppressWarnings("deprecation")
public class SyncChat implements Listener {
	
	private BanManager plugin;

	public SyncChat(BanManager instance) {
		plugin = instance;
	}

	@EventHandler
	public void onPlayerChat(final PlayerChatEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		
		if(plugin.getPlayerMutes().get(playerName) != null) {

			MuteData muteData = plugin.getPlayerMutes().get(playerName);
			String expires = Util.formatDateDiff(muteData.getExpires());
			
			if(muteData.getExpires() != 0) {
				if(System.currentTimeMillis() < muteData.getExpires()) {
					event.setCancelled(true);
					String mutedMessage = plugin.getMessage("tempMuted").replace("[expires]", expires).replace("[reason]", muteData.getReason()).replace("[by]", muteData.getBy());
					player.sendMessage(mutedMessage);
				} else {
					// Removes them from the database and the HashMap
					player.sendMessage("Unmuted!");
					plugin.removePlayerMute(playerName, plugin.getMessage("consoleName"), true);
				}
			} else {
				event.setCancelled(true);
				String mutedMessage = plugin.getMessage("muted").replace("[reason]", muteData.getReason()).replace("[by]", muteData.getBy());
				player.sendMessage(mutedMessage);
			}
		}
	}
}
