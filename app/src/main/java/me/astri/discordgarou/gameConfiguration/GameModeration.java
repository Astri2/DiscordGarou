package me.astri.discordgarou.gameConfiguration;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.LgClassesAndEnums.EnumRole;
import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.exceptions.InvalidUser;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.Game.Player;
import me.astri.discordgarou.generalGame.GameManager;
import me.astri.discordgarou.generalGame.LgUtilities;
import me.astri.discordgarou.main.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import me.astri.discordgarou.main.Bot;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameModeration {
	public static void Ban(GuildMessageReceivedEvent event) throws GlobalException, InvalidUser {
		Game game = LgUtilities.GetGame(event,null,"Owner");
		if(game == null) return;
		if(game.gameStarted)
			throw new GlobalException(event.getMessage(),"Erreur","Vous ne pouvez pas éditer la config après le lancement de la partie!",5);
			
		User user = Utilities.getMentionedUser(event);
		if(user.getId().equals(game.gameOwner)) 
			throw new GlobalException(event.getMessage(),"Erreur permissions",user.getAsMention() + " ne peut être banni !",5);

		for(String bannedId : game.bannedPlayers) {
			if(bannedId.equals(user.getId())) {
				throw new GlobalException(event.getMessage(),"Membre déjà banni",user.getAsMention() + " est déjà banni !",5);
			}
		}			
		
		event.getMessage().addReaction("✅").queue();
		event.getChannel().sendMessage(event.getGuild().getMember(user).getEffectiveName() + " a été banni de cette partie.").queue((message) -> {
			message.delete().queueAfter(5, TimeUnit.SECONDS);
			event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
		});
		game.bannedPlayers.add(user.getId());
		
		for(Player player : game.playerList) { //removing from the game
			if(player.playerId.equals(user.getId())) {
				RemovePlayer(game,player.playerId);
				event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
				break;
			}
		}
		GameManager.save(null);
		
	}

	
	public static void Unban(GuildMessageReceivedEvent event) throws Exception {
		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL,"Owner");
		if(game == null) return;
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		if(args.length > 1 && args[1].equalsIgnoreCase("all")) {
			game.bannedPlayers.clear();
			event.getMessage().addReaction("✅").queue();
			event.getChannel().sendMessage("Tous les joueurs ont étés débannis.").queue((message) -> {
				message.delete().queueAfter(5, TimeUnit.SECONDS);
				event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
			});
			return;
		}
		
		User user = Utilities.getMentionedUser(event);
		for(String bannedId : game.bannedPlayers) {
			if(bannedId.equals(user.getId())) {
				game.bannedPlayers.remove(bannedId);
				GameManager.save(null);
				
				event.getMessage().addReaction("✅").queue();
				event.getChannel().sendMessage(event.getGuild().getMember(user).getEffectiveName() + " a été débanni de cette partie.").queue((message) -> {
					message.delete().queueAfter(5, TimeUnit.SECONDS);
					event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
				});
				return;
			}
		}
		throw new GlobalException(event.getMessage(),"Erreur utilisateur",user.getAsMention() + " n'est pas banni de cette partie.",5);			
	}
	
	public static void Kick(GuildMessageReceivedEvent event) throws Exception {
		Game game = LgUtilities.GetGame(event,null,"Owner");
		if(game == null) return;
		if(game.gameStarted) 
			throw new GlobalException(event.getMessage(),"Erreur","Vous ne pouvez pas éditer la config après le lancement de la partie!",5);
			
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		if(args.length > 1 && args[1].equalsIgnoreCase("all")) {
			event.getMessage().addReaction("✅").queue();
			event.getChannel().sendMessage("Tous les joueurs ont été éjectés de la partie.").queue((message) -> {
				message.delete().queueAfter(5, TimeUnit.SECONDS);
				event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
			});
			for(int i = 0 ; i < game.playerList.size();) {
				if(game.playerList.get(i).playerId.equals(game.gameOwner))
					i++;
				else RemovePlayer(game,game.playerList.get(i).playerId);
			}
			event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
			GameManager.save(null);
			return;
		}
		
		User user = Utilities.getMentionedUser(event);
		if(user.getId().equals(game.gameOwner)) 
			throw new GlobalException(event.getMessage(),"Erreur permissions",user.getAsMention() + " ne peut être ejecté !",5);
		
		for(Player player : game.playerList) {
			if(player.playerId.equals(user.getId())) {
				RemovePlayer(game,player.playerId);
				GameManager.save(null);
				
				event.getMessage().addReaction("✅").queue();
				event.getChannel().sendMessage(event.getGuild().getMember(user).getEffectiveName() + " a été éjecté de la partie.").queue((message) -> {
					message.delete().queueAfter(5, TimeUnit.SECONDS);
					event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
				});
				
				return;
			}
		}
		
		throw new GlobalException(event.getMessage(),"Erreur utilisateur", user.getAsMention() + " n'est pas dans la partie.",5);
	}
	
	public static void BanList(GuildMessageReceivedEvent event) throws GlobalException {
		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL,"Owner");
		if(game == null) return;
			
		String str = "";
		for(String playerId : game.bannedPlayers) {
			str += event.getGuild().getMemberById(playerId).getAsMention() + "\n";
		}
		if(str.equals("")) str = "Aucun joueur banni !";
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Joueurs bannis");
		embed.setDescription(str);
		embed.setTimestamp(Instant.now());
		event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
		
	}
	
	public static void AddPlayer(Game game, String playerId) {
		game.playerList.add(new Player(playerId, new Game.LgRole(EnumRole.Role.NOTHING,null), true));
		String villagePlaceId = game.channelList.get(EnumChannel.Channel.PLACE_VILLAGE).get(0).channelId;

		TextChannel villagePlace = Bot.jda.getGuildById(game.guildId).getTextChannelById(villagePlaceId);
		villagePlace.createPermissionOverride(Bot.jda.getGuildById(game.guildId).getMemberById(playerId)).setAllow(
				LgUtilities.grantedPermissions).queue();
	}
	
	public static void RemovePlayer(Game game, String playerId) {
		for(Player player : game.playerList) {
			if(playerId.equals(player.playerId)) {
				game.playerList.remove(player);
				break;
			}
		}
		String villagePlaceId = game.channelList.get(EnumChannel.Channel.PLACE_VILLAGE).get(0).channelId;

		TextChannel villagePlace = Bot.jda.getGuildById(game.guildId).getTextChannelById(villagePlaceId);
		List<PermissionOverride> permList = villagePlace.getPermissionOverrides();
		for(PermissionOverride perm : permList) {
			if(perm.isMemberOverride() && perm.getMember().getId().equals(playerId)) {
				perm.delete().queue();
				break;
			}
		}
	}
}
