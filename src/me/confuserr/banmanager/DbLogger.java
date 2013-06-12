package me.confuserr.banmanager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.confuserr.banmanager.data.*;

public class DbLogger {
	private Database localConn;
	private BanManager plugin;

	DbLogger(Database conn, BanManager instance) {
		localConn = conn;
		plugin = instance;
	}

	public void logBan(String name, String bannedBy, String reason) {
		Util.asyncQuery("INSERT INTO " + localConn.bansTable + " (banned, banned_by, ban_reason, ban_time, ban_expires_on, server) VALUES ('" + name + "', '" + bannedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '0', '" + plugin.serverName + "')");
	}

	public void logTempBan(String name, String bannedBy, String reason, long expires) {
		Util.asyncQuery("INSERT INTO " + localConn.bansTable + " (banned, banned_by, ban_reason, ban_time, ban_expires_on, server) VALUES ('" + name + "', '" + bannedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '" + expires + "', '" + plugin.serverName + "')");
	}

	public void logIpBan(String name, String bannedBy, String reason) {
		Util.asyncQuery("INSERT INTO " + localConn.ipBansTable + " (banned, banned_by, ban_reason, ban_time, ban_expires_on, server) VALUES ('" + name + "', '" + bannedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '0', '" + plugin.serverName + "')");
	}

	public void logTempIpBan(String name, String bannedBy, String reason, long expires) {
		Util.asyncQuery("INSERT INTO " + localConn.ipBansTable + " (banned, banned_by, ban_reason, ban_time, ban_expires_on, server) VALUES ('" + name + "', '" + bannedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '" + expires + "', '" + plugin.serverName + "')");
	}

	public void logKick(String name, String bannedBy, String reason) {
		Util.asyncQuery("INSERT INTO " + localConn.kicksTable + " (kicked, kicked_by, kick_reason, kick_time, server) VALUES ('" + name + "', '" + bannedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '" + plugin.serverName + "')");
	}

	public void logMute(String name, String mutedBy, String reason) {
		Util.asyncQuery("INSERT INTO " + localConn.mutesTable + " (muted, muted_by, mute_reason, mute_time, mute_expires_on, server) VALUES ('" + name + "', '" + mutedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '0', '" + plugin.serverName + "')");
	}

	public void logTempMute(String name, String mutedBy, String reason, long expires) {
		Util.asyncQuery("INSERT INTO " + localConn.mutesTable + " (muted, muted_by, mute_reason, mute_time, mute_expires_on, server) VALUES ('" + name + "', '" + mutedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '" + expires + "', '" + plugin.serverName + "')");
	}

	public void logWarning(String name, String warnedBy, String reason) {
		Util.asyncQuery("INSERT INTO " + localConn.warningsTable + " (warned, warned_by, warn_reason, warn_time, server) VALUES ('" + name + "', '" + warnedBy + "', '" + reason + "', UNIX_TIMESTAMP(now()), '" + plugin.serverName + "')");
	}

	public boolean handleBukkitBan(String name) {
		ResultSet result2 = localConn.query("SELECT banned FROM " + localConn.bansRecordTable + " WHERE banned = '" + name + "'");

		try {
			if (result2.next()) {
				plugin.getServer().getOfflinePlayer(name).setBanned(false);
			}
			result2.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public int getPastBanCount(String name) {
		ResultSet result = localConn.query("SELECT COUNT(*) AS numb FROM " + localConn.bansRecordTable + " WHERE banned = '" + name + "'");
		int count = 0;
		try {
			if (result.next())
				count = result.getInt("numb");
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	public int getPastMuteCount(String user) {
		ResultSet result = localConn.query("SELECT COUNT(*) AS numb FROM " + localConn.mutesRecordTable + " WHERE muted = '" + user + "'");
		int count = 0;
		try {
			if (result.next())
				count = result.getInt("numb");
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	public int getWarningCount(String user) {
		ResultSet result = localConn.query("SELECT COUNT(*) AS numb FROM " + localConn.warningsTable + " WHERE warned = '" + user + "'");
		int count = 0;
		try {
			if (result.next())
				count = result.getInt("numb");
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	public boolean isMuted(String name) {
		boolean muted = false;

		ResultSet result = localConn.query("SELECT mute_id FROM " + localConn.mutesTable + " WHERE muted = '" + name + "'");
		try {
			if (result.next())
				muted = true;
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return muted;
	}

	public MuteData getMute(String name) {
		MuteData data = null;

		ResultSet result = localConn.query("SELECT * FROM " + localConn.mutesTable + " WHERE muted = '" + name + "'");
		try {
			if (result.next())
				data = new MuteData(name, result.getString("muted_by"), result.getString("mute_reason"), result.getLong("mute_time"), result.getLong("mute_expires_on"));
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	public ArrayList<MuteData> getPastMutes(String name) {
		ArrayList<MuteData> data = new ArrayList<MuteData>();

		ResultSet result = localConn.query("SELECT * FROM " + localConn.mutesRecordTable + " WHERE muted = '" + name + "'");
		try {
			while (result.next()) {
				data.add(new MuteData(name, result.getString("muted_by"), result.getString("mute_reason"), result.getLong("mute_time"), result.getLong("mute_expired_on")));
			}

			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}
	
	public ArrayList<BanData> getPastBans(String name) {
		ArrayList<BanData> data = new ArrayList<BanData>();

		ResultSet result = localConn.query("SELECT * FROM " + localConn.bansRecordTable + " WHERE banned = '" + name + "'");
		try {
			while (result.next()) {
				data.add(new BanData(name, result.getString("banned_by"), result.getString("ban_reason"), result.getLong("ban_time"), result.getLong("ban_expired_on")));
			}

			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}
	
	public ArrayList<IPBanData> getPastIPBans(String ip) {
		ArrayList<IPBanData> data = new ArrayList<IPBanData>();

		ResultSet result = localConn.query("SELECT * FROM " + localConn.ipBansRecordTable + " WHERE banned = '" + ip + "'");
		try {
			while (result.next()) {
				data.add(new IPBanData(ip, result.getString("banned_by"), result.getString("ban_reason"), result.getLong("ban_time"), result.getLong("ban_expired_on")));
			}

			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}
	
	public ArrayList<WarnData> getWarnings(String name) {
		ArrayList<WarnData> data = new ArrayList<WarnData>();

		ResultSet result = localConn.query("SELECT * FROM " + localConn.ipBansRecordTable + " WHERE banned = '" + name + "'");
		try {
			while (result.next()) {
				data.add(new WarnData(name, result.getString("warned_by"), result.getString("warn_reason"), result.getLong("warn_time")));
			}

			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	public void banRemove(String name, String by, boolean keepLog) {
		if (keepLog)
			Util.asyncQuery("INSERT INTO " + localConn.bansRecordTable + " (banned, banned_by, ban_reason, ban_time, ban_expired_on, unbanned_by, unbanned_time, server) SELECT b.banned, b.banned_by, b.ban_reason, b.ban_time, b.ban_expires_on, \"" + by + "\", UNIX_TIMESTAMP(now()), b.server FROM " + localConn.bansTable + " b WHERE b.banned = '" + name + "'");

		// Now delete it
		Util.asyncQuery("DELETE FROM " + localConn.bansTable + " WHERE banned = '" + name + "'");
	}

	public void ipRemove(String ip, String by, boolean keepLog) {
		if (keepLog)
			Util.asyncQuery("INSERT INTO " + localConn.ipBansRecordTable + " (banned, banned_by, ban_reason, ban_time, ban_expired_on, unbanned_by, unbanned_time, server) SELECT b.banned, b.banned_by, b.ban_reason, b.ban_time, b.ban_expires_on, \"" + by + "\", UNIX_TIMESTAMP(now()), b.server FROM " + localConn.ipBansTable + " b WHERE b.banned = '" + ip + "'");

		// Now delete it
		Util.asyncQuery("DELETE FROM " + localConn.ipBansTable + " WHERE banned = '" + ip + "'");
	}

	public void muteRemove(String name, String by, boolean keepLog) {
		if (keepLog)
			Util.asyncQuery("INSERT INTO " + localConn.mutesRecordTable + " (muted, muted_by, mute_reason, mute_time, mute_expired_on, unmuted_by, unmuted_time, server) SELECT b.muted, b.muted_by, b.mute_reason, b.mute_time, b.mute_expires_on, \"" + by + "\", UNIX_TIMESTAMP(now()), b.server FROM " + localConn.mutesTable + " b WHERE b.muted = '" + name + "'");

		// Now delete it
		Util.asyncQuery("DELETE FROM " + localConn.mutesTable + " WHERE muted = '" + name + "'");
	}

	public void create_tables() throws SQLException {
		boolean Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.bansTable + " (" + "ban_id int(255) NOT NULL AUTO_INCREMENT," + "banned varchar(32) NOT NULL," + "banned_by varchar(32) NOT NULL," + "ban_reason text NOT NULL," + "ban_time int(10) NOT NULL," + "ban_expires_on int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (ban_id)," + "KEY `banned` (`banned`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

		if (!Table)
			plugin.getLogger().severe("Unable to create local BanManagement table");
		else {
			Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.bansRecordTable + " (" + "ban_record_id int(255) NOT NULL AUTO_INCREMENT," + "banned varchar(32) NOT NULL," + "banned_by varchar(32) NOT NULL," + "ban_reason text NOT NULL," + "ban_time int(10) NOT NULL," + "ban_expired_on int(10) NOT NULL," + "unbanned_by varchar(32) NOT NULL," + "unbanned_time int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (ban_record_id)," + "KEY `banned` (`banned`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

			if (!Table)
				plugin.getLogger().severe("Unable to create local BanManagement table");
			else {
				Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.ipBansTable + " (" + "ban_id int(255) NOT NULL AUTO_INCREMENT," + "banned varchar(32) NOT NULL," + "banned_by varchar(32) NOT NULL," + "ban_reason text NOT NULL," + "ban_time int(10) NOT NULL," + "ban_expires_on int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (ban_id)," + "KEY `banned` (`banned`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

				if (!Table)
					plugin.getLogger().severe("Unable to create local BanManagement table");
				else {
					Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.ipBansRecordTable + " (" + "ban_record_id int(255) NOT NULL AUTO_INCREMENT," + "banned varchar(32) NOT NULL," + "banned_by varchar(32) NOT NULL," + "ban_reason text NOT NULL," + "ban_time int(10) NOT NULL," + "ban_expired_on int(10) NOT NULL," + "unbanned_by varchar(32) NOT NULL," + "unbanned_time int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (ban_record_id)," + "KEY `banned` (`banned`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

					if (!Table)
						plugin.getLogger().severe("Unable to create local BanManagement table");
					else {
						Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.kicksTable + " (" + "kick_id int(255) NOT NULL AUTO_INCREMENT," + "kicked varchar(32) NOT NULL," + "kicked_by varchar(32) NOT NULL," + "kick_reason text NOT NULL," + "kick_time int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (kick_id)," + "KEY `kicked` (`kicked`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");
						if (!Table)
							plugin.getLogger().severe("Unable to create local BanManagement table");
						else {
							Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.mutesTable + " (" + "mute_id int(255) NOT NULL AUTO_INCREMENT," + "muted varchar(32) NOT NULL," + "muted_by varchar(32) NOT NULL," + "mute_reason text NOT NULL," + "mute_time int(10) NOT NULL," + "mute_expires_on int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (mute_id)," + "KEY `muted` (`muted`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

							if (!Table)
								plugin.getLogger().severe("Unable to create local BanManagement table");
							else {
								Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.mutesRecordTable + " (" + "mute_record_id int(255) NOT NULL AUTO_INCREMENT," + "muted varchar(32) NOT NULL," + "muted_by varchar(32) NOT NULL," + "mute_reason text NOT NULL," + "mute_time int(10) NOT NULL," + "mute_expired_on int(10) NOT NULL," + "unmuted_by varchar(32) NOT NULL," + "unmuted_time int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (mute_record_id)," + "KEY `muted` (`muted`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

								if (!Table)
									plugin.getLogger().severe("Unable to create local BanManagement table");
								else {
									Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.playerIpsTable + " (" + "`player` varchar(25) NOT NULL," + "`ip` int UNSIGNED NOT NULL," + "`last_seen` int(10) NOT NULL," + "PRIMARY KEY `player` (`player`)," + "KEY `ip` (`ip`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

									if (!Table)
										plugin.getLogger().severe("Unable to create local BanManagement table");
									else {
										Table = localConn.createTable("CREATE TABLE IF NOT EXISTS " + localConn.warningsTable + " (" + "warn_id int(255) NOT NULL AUTO_INCREMENT," + "warned varchar(32) NOT NULL," + "warned_by varchar(32) NOT NULL," + "warn_reason text NOT NULL," + "warn_time int(10) NOT NULL," + "server varchar(30) NOT NULL," + "PRIMARY KEY (warn_id)," + "KEY `kicked` (`warned`)" + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1");

										if (!Table)
											plugin.getLogger().severe("Unable to create local BanManagement table");
									}

									/*
									 * if(!Table) plugin.getLogger().severe(
									 * "Unable to create local BanManagement table"
									 * ); else { Table = localConn.createTable(
									 * "CREATE TABLE IF NOT EXISTS "
									 * +localConn.banAppealsTable+" ("+
									 * "`appeal_id` int(255) NOT NULL AUTO_INCREMENT,"
									 * + "`ban_id` int(255) NOT NULL," +
									 * "`ban_type` int(1) NOT NULL,"+
									 * "`appeal_time` int(10) NOT NULL,"+
									 * "PRIMARY KEY `player` (`player`),"+
									 * "KEY `ip` (`ip`)"+
									 * ") ENGINE=MyISAM  DEFAULT CHARSET=latin1"
									 * );
									 * 
									 * if(!Table) plugin.getLogger().severe(
									 * "Unable to create local BanManagement table"
									 * ); else { Table = localConn.createTable(
									 * "CREATE TABLE IF NOT EXISTS "
									 * +localConn.pinsTable+" ("+
									 * "`pin_id` int(255) UNSIGNED NOT NULL AUTO_INCREMENT,"
									 * + "`player` varchar(25) NOT NULL," +
									 * "`ban_type` int(1) NOT NULL,"+
									 * "`appeal_time` int(10) NOT NULL,"+
									 * "PRIMARY KEY `player` (`player`),"+
									 * "KEY `ip` (`ip`)"+
									 * ") ENGINE=MyISAM  DEFAULT CHARSET=latin1"
									 * );
									 * 
									 * if(!Table) plugin.getLogger().severe(
									 * "Unable to create local BanManagement table"
									 * ); else { Table = localConn.createTable(
									 * "CREATE TABLE IF NOT EXISTS "
									 * +localConn.staffTable+" ("+
									 * "`staff_id` int(255) UNSIGNED NOT NULL AUTO_INCREMENT,"
									 * + "`ssid` varchar(32) NOT NULL,"+
									 * "`player` varchar(25) NOT NULL," +
									 * "`permissions` int(255) UNSIGNED NOT NULL,"
									 * +
									 * "`password_hash` varchar(40) NOT NULL,"+
									 * "`password_salt` varchar(10),"+
									 * "PRIMARY KEY `staff_id` (`staff_id`),"+
									 * "KEY `ssid` (`ssid`)"+
									 * ") ENGINE=MyISAM  DEFAULT CHARSET=latin1"
									 * );
									 * 
									 * if(!Table) plugin.getLogger().severe(
									 * "Unable to create local BanManagement table"
									 * ); } } }
									 */
								}
							}
						}
					}
				}
			}
		}
	}

	public void setIP(String name, String ip) {
		Util.asyncQuery("INSERT INTO " + localConn.playerIpsTable + " (`player`, `ip`, `last_seen`) VALUES ('" + name + "', INET_ATON('" + ip + "'), '" + System.currentTimeMillis() / 1000 + "') ON DUPLICATE KEY UPDATE ip = INET_ATON('" + Util.getIP(ip) + "'), last_seen = '" + System.currentTimeMillis() / 1000 + "'");
	}

	public String getIP(String name) {
		String ip = "";

		ResultSet result = localConn.query("SELECT INET_NTOA(ip) AS ipAddress FROM " + localConn.playerIpsTable + " WHERE player = '" + name + "'");

		try {
			if (result.next())
				ip = result.getString("ipAddress");
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ip;
	}

	public String findPlayerIpDuplicates(String ip, String player) {
		ResultSet result = localConn.query("SELECT ip.player FROM " + localConn.bansTable + " b LEFT JOIN " + localConn.playerIpsTable + " ip ON ip.player = b.banned WHERE ip.ip = INET_ATON('" + ip + "')");

		try {
			if (!result.isBeforeFirst()) {
				result.close();
				return "";
			} else {

				ArrayList<String> playerList = new ArrayList<String>();

				while (result.next()) {
					if (!playerList.contains(result.getString("player")) && !result.getString("player").equals(player)) {
						playerList.add(result.getString("player"));
					}
				}

				result.close();

				if (playerList.size() == 0)
					return "";

				String players = "";

				for (String p : playerList) {
					players += p + ", ";
				}

				return players.substring(0, players.length() - 2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void closeConnection() {
		localConn.close();
	}
}