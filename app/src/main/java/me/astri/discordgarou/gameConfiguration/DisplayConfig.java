package me.astri.discordgarou.gameConfiguration;

import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.Game.Gamerule;
import me.astri.discordgarou.generalGame.Game.LgRole;
import me.astri.discordgarou.generalGame.Help;
import me.astri.discordgarou.generalGame.LgUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import me.astri.discordgarou.main.Bot;

import java.util.ArrayList;

public class DisplayConfig {

	public static void CreateGameConfig(Game game) {
		TextChannel configChannel = LgUtilities.getConfigChannel(game);

		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(0x00FFB6)
			.setTitle("game configuration init...");
		configChannel.sendMessage(Bot.jda.getGuildById(game.guildId).getMemberById(game.gameOwner).getAsMention()).queue((message) ->
			message.delete().queue()
		);

		Help.RoleList(configChannel);
		Help.GameruleList(configChannel);

		configChannel.sendMessage(embed.build()).queue(message -> {
			game.configMessageId = message.getId();
			EditGameConfig(game, message);
			message.addReaction(":plus:705756555366367232").queue();
			message.addReaction(":moins:705756522155868251").queue();
		});
	}
	public static void EditGameConfig(Game game, Message message) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Configuration de la partie");
		embed.setDescription("Toutes les commandes de configuration sont à effectuer dans ce channel. Également merci de ne pas supprimer les channels.");
		embed.setColor(0x00FFB6);
		embed.setFooter("Game by " + Bot.jda.getGuildById(game.guildId).getMemberById(game.gameOwner).getEffectiveName() + " - #" + game.gameId,
				game.whitelistToggle ? "https://cdn.discordapp.com/attachments/675727223021502504/722484606397448252/white.png" :
						"https://cdn.discordapp.com/attachments/675727223021502504/722485074234179584/Black.png");
		String players = "";
		String roles = "";
		String gamerules;
		for(Game.Player player : game.playerList) {
			players += Bot.jda.getGuildById(game.guildId).getMemberById(player.playerId).getAsMention() + "\n";
		}
		for(Game.LgRole role : game.roleList) {
			if(!roles.contains(role.roleEnum.getEmote())) 	//not displayed yet
				roles += role.roleEnum.getEmote() + " " + role.roleEnum.getFullName() + " x" + LgUtilities.getRoleIteration(role.roleEnum, game.roleList) + "\n";
		}
		gamerules = DisplayGamerules(game.gameruleList,game.roleList);
		embed.addField("Players (" + game.playerList.size() + ")",players,true);
		embed.addField("Composition (" + game.roleList.size() +  ")",roles,true);
		embed.addField("Règles de jeu",gamerules,true);

		message.editMessage(embed.build()).queue();
	}
	
	private static String DisplayGamerules(ArrayList<Gamerule> grList, ArrayList<LgRole> roleList) {
		String str = "";
		for(Gamerule gamerule : grList) {
			switch(gamerule.gmEnum.name()) {
			case "maire":
			case "couple_aleatoire":
				if(gamerule.value == 1) {
					str += gamerule.gmEnum.FullName() + "\n";
				}
				break;
				
			case "malediction_du_corbeau":
				if(roleList.stream().anyMatch(role -> role.roleEnum.getFullName().equals("Corbeau"))) {
					str += gamerule.gmEnum.FullName() + " : " + gamerule.value + "\n";
				}
				break;
			}
		}
		return str;
	}
}
