package me.astri.discordgarou.gameConfiguration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.generalGame.LgUtilities;

import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.LgClassesAndEnums.EnumGamerule.Gamerule;
import me.astri.discordgarou.generalGame.GameManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class EditGameruleConfig {
	public static void EditGamerule(GuildMessageReceivedEvent event) throws  GlobalException {

		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL,"Owner");
		if(game == null) return;
		if(game.gameStarted) throw new GlobalException(event.getMessage(),"Erreur","Vous ne pouvez pas éditer la config après le lancement de la partie!",5);
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		if(args.length < 2) throw new GlobalException(event.getMessage(),"Erreur", "Veuillez préciser sur quelle règle de jeu agir. \nlg!gamerule [règle de jeu] [on/off/valeur]",5);

		if(args[1].equalsIgnoreCase("reset")) {
			resetGamerules(event,game);
			return;
		}

		Game.Gamerule gamerule = getGamerule(args[1].toLowerCase(),game.gameruleList);
		if(gamerule == null)
			throw new GlobalException(event.getMessage(),"Erreur", "La règle spécifié **" + args[1] + "** n'est pas reconnue. \nPour accéder à la liste des règles, entrez la commande **lg!help gamerule**.",5);

		if(args.length < 3) throw new GlobalException(event.getMessage(),"Erreur", "Veuillez spécifier la valeur a attribuer à la règle de jeu. \nlg!gamerule [règle de jeu] [on/off/valeur]",5);


		String value = args[2].toLowerCase();
		switch(gamerule.gmEnum.Type()) {
		case "bool":
			switch(value) {
			case "on":
			case "1":
			case "true":
				gamerule.value = 1;
				break;
			case "off":
			case "0":
			case "false":
				gamerule.value = 0;
				break;
			default :
				throw new GlobalException(event.getMessage(),"Erreur","La valeur spécifiée **" + value + "** n'est pas compatible avec la règle **" + gamerule.gmEnum.FullName() + "**. \nlg!gamerule " + gamerule.gmEnum.name() + " [on/off]",5);
			}
			break;
		case "int":
			try {
				int iValue = Integer.parseInt(value);
				if(iValue < gamerule.gmEnum.Min()) {iValue = gamerule.gmEnum.Min(); }
				if(iValue > gamerule.gmEnum.Max()) {iValue = gamerule.gmEnum.Max(); }
				gamerule.value = iValue;
			}
			catch(NumberFormatException e) {
				throw new GlobalException(event.getMessage(),"Erreur","La valeur spécifiée **" + value + "** n'est pas compatible avec la règle **" + gamerule.gmEnum.FullName() + "**. \nlg!gamerule " + gamerule.gmEnum.name() + " [numeric value]",5);
			}
			break;
		}
		event.getMessage().addReaction("✅").queue((__) ->
			event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS)
		);
		event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
		GameManager.save(null);
	}
	
	private static void resetGamerules(GuildMessageReceivedEvent event, Game game) {
		Gamerule[] baseGamerules = Gamerule.values();
		game.gameruleList.clear();

		for(Gamerule i : baseGamerules) {
			game.gameruleList.add(new Game.Gamerule(i, i.BaseValue()));
		}
		
		event.getMessage().addReaction("✅").queue((__) ->
			event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS)
		);
		event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
		GameManager.save(null);
	}
	
	private static Game.Gamerule getGamerule(String gamerule, ArrayList<Game.Gamerule> gamerules) {
		for(Game.Gamerule gm : gamerules) {
			if(gm.gmEnum.toString().equals(gamerule)) {
				return gm;
			}
		}
		return null;
	}
}
