package me.astri.discordgarou.generalGame;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.LgClassesAndEnums.EnumGamerule;
import me.astri.discordgarou.LgClassesAndEnums.EnumRole;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Game implements Serializable {
	public Boolean gameStarted;
	public Boolean whitelistToggle;
	public String guildId;
	public String categoryId;
	public String configMessageId;
	public String announceMessageId;
	public String announceChannelId;
	public String gameId;
	public String gameOwner;
	public ArrayList<String> bannedPlayers = new ArrayList<>();
	public ArrayList<String> whiteList = new ArrayList<>();
	public ArrayList<Player> playerList = new ArrayList<>();
	public ArrayList<Gamerule> gameruleList = new ArrayList<>();
	public ArrayList<LgRole> roleList = new ArrayList<>();
	public HashMap<EnumChannel.Channel,ArrayList<GameChannel>> channelList = new HashMap<>();
	

	public static class GameChannel implements Serializable {
		public GameChannel(EnumChannel.Channel channelType, TextChannel channel) {
			this.channelType = channelType;
			this.channelId = channel.getId();
			channel.createWebhook("uselessName").queue(webhook -> this.webhookUrl = webhook.getUrl());
		}

		public EnumChannel.Channel channelType;
		public String channelId;
		public String webhookUrl;
	}

	public static class LgChannel extends GameChannel implements Serializable {
		public LgChannel(EnumChannel.Channel channelType, TextChannel channel, ArrayList<String> channelMemberId) {
			super(channelType,channel);
			this.channelMemberId = channelMemberId;
		}

		public ArrayList<String> channelMemberId;
	}


	public static class LgRole implements Serializable{
		public EnumRole.Role roleEnum;
		public Player player;
		
		public LgRole(EnumRole.Role roleName, Player player) {
			this.roleEnum = roleName;
			this.player = player;
		}
		
	}
	public static class Player implements Serializable{
		
		public String playerId;
		public boolean isAlive;
		public LgRole role;
		
		public Player(String playerId, LgRole role, Boolean isAlive) {
			this.playerId = playerId;
			this.role = role;
			this.isAlive = isAlive;
		}
	}
	public static class Gamerule implements Serializable{
		public EnumGamerule.Gamerule gmEnum;
		public int value;
		
		public Gamerule(EnumGamerule.Gamerule gmEnum, int value) {
			this.gmEnum = gmEnum;
			this.value = value;
		}
	}
	
	public Game(GuildMessageReceivedEvent event) {
		this.guildId = event.getGuild().getId();
		this.gameStarted = false;
		this.whitelistToggle = false;
		this.gameId = event.getMessageId();
		this.gameOwner = event.getAuthor().getId();
		playerList.add(new Player(this.gameOwner, new LgRole(EnumRole.Role.NOTHING,null), true));
		EnumGamerule.Gamerule[] baseGamerules = EnumGamerule.Gamerule.values();
		
		for(EnumGamerule.Gamerule i : baseGamerules) {
			this.gameruleList.add(new Gamerule(i, i.BaseValue()));
		}
	}
	
	public Game(Boolean gameStarted, Boolean whitelistToggle, String guildId, String categoryId, String configMessageId, String gameId, String gameOwner, ArrayList<String> bannedPlayers, ArrayList<String> whiteList, ArrayList<Player> playerList, ArrayList<Gamerule> gameruleList, ArrayList<LgRole> roleList, HashMap<EnumChannel.Channel,ArrayList<GameChannel>> channelList) {
		this.gameStarted = gameStarted;
		this.whitelistToggle = whitelistToggle;
		this.guildId = guildId;
		this.categoryId = categoryId;
		this.configMessageId = configMessageId;
		this.gameId = gameId;
		this.gameOwner = gameOwner;
		this.bannedPlayers = bannedPlayers;
		this.whiteList = whiteList;
		this.playerList = playerList;
		this.gameruleList = gameruleList;
		this.roleList = roleList;
		this.channelList = channelList;
	}
}
