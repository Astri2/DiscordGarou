package me.astri.discordgarou.gameConfiguration;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.Game.Player;
import me.astri.discordgarou.generalGame.GameManager;
import me.astri.discordgarou.main.Config;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactJoinLeave extends ListenerAdapter{ 
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		if(event.getUser().getId().equals(Config.get("bot_id"))) return;
		ReactionEmote react = event.getReactionEmote();
		if(react.isEmoji() || 
				!(event.getReactionEmote().getId().equals("705756555366367232") || event.getReactionEmote().getId().equals("705756522155868251"))) return;

		Game game = null;
		for(Game gameFor: GameManager.gameList) {
			if(gameFor.channelList.containsKey(EnumChannel.Channel.CONFIG_CHANNEL) &&
					gameFor.channelList.get(EnumChannel.Channel.CONFIG_CHANNEL).get(0).channelId.equals(event.getChannel().getId())) {
				game = gameFor;
				break;
			}
		}
		if(game == null) return;

		event.getReaction().removeReaction(event.getUser()).queue();

		if(game.gameStarted) return;
		
		if(event.getReactionEmote().getId().equals("705756555366367232")) {
			boolean banned = game.bannedPlayers.contains(event.getUserId());
			boolean whiteListed = (!game.whitelistToggle || game.whiteList.contains(event.getUserId()));
			boolean alreadyHere = game.playerList.stream().anyMatch(player -> player.playerId.equals(event.getUserId()));

			if(!banned && !alreadyHere && whiteListed) {
				GameModeration.AddPlayer(game, event.getUserId());
				Game finalGame = game;
				event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(finalGame, msg));
				try {
					GameManager.save(null);
				} catch (Exception e) {e.printStackTrace();}
			}
		}
		else {
			if(!game.gameOwner.equals(event.getUserId())) {
				for(Player player : game.playerList) {
					if(player.playerId.equals(event.getUserId())) {
						GameModeration.RemovePlayer(game,player.playerId);
						Game finalGame1 = game;
						event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(finalGame1, msg));
						try {
							GameManager.save(null);
						} catch (Exception e) {e.printStackTrace();}
						break;
					}
				}
			}	
		}
	}
}
