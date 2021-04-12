package me.astri.discordgarou.gameConfiguration;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.exceptions.InvalidUser;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.GameManager;
import me.astri.discordgarou.generalGame.LgUtilities;
import me.astri.discordgarou.main.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import me.astri.discordgarou.main.Bot;

public class WhiteListClass {
	public static void Whitelist(GuildMessageReceivedEvent event) throws Exception {
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		
		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL,"Owner");
		if(game == null) return;
		EmbedBuilder embed;
		if(args.length < 2) {
			embed = display(game);	
			event.getChannel().sendMessage(embed.build()).queue((message) -> {
				event.getMessage().addReaction("✅").queue();
				event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
				message.delete().queueAfter(5, TimeUnit.SECONDS);
			});
			return;
		}

		switch(args[1].toLowerCase()) {
		case "+":
		case "add":
			embed = add(event,game);
			break;
		case "-":
		case "remove":
			embed = remove(event,game);
			break;
		case "display":
			embed = display(game);
			break;
		case "toggle":
			embed = toggle(game);
			break;
		case "on":
			embed = on(event,game);
			break;
		case "off":
			embed = off(event,game);
			break;
		default:
			throw new GlobalException(event.getMessage(),"Erreur argument","L'action **" + args[1] + "** n'est pas reconnue parmi [add/remove/display/toggle/on/off].",5);
		}
		event.getChannel().sendMessage(embed.build()).queue((message) -> {
			event.getMessage().addReaction("✅").queue();
			event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
			message.delete().queueAfter(5, TimeUnit.SECONDS);
		});
		event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
		GameManager.save(null);
	}
	
	
	private static EmbedBuilder display(Game game) {
		EmbedBuilder embed = new EmbedBuilder();
		embed
			.setTitle("White-List")
			.setDescription(game.whitelistToggle ? "⚪  La white-list est actuellement activée." : "⚫ La white-list est actuellement désactivée.")
			.setColor(game.whitelistToggle ? Color.white : Color.black)
			.setFooter("lg!whitelist [add/remove/display/toggle/on/off]");
		
		String str = "";
		for(String playerId : game.whiteList) {
			str += Bot.jda.getUserById(playerId).getAsMention() + "\n";
		}
		if(str.equals("")) str = "Aucun joueur n'est white-list !";
		
		embed.addField("Joueurs white-list",str,false);
		
		return embed;
	}
	
	private static EmbedBuilder add(GuildMessageReceivedEvent event, Game game) throws InvalidUser, GlobalException {
		User user = Utilities.getMentionedUser(event);
		if(Utilities.find(game.whiteList, user.getId()) == -1) {//not yet whitelisted
			game.whiteList.add(user.getId());
			EmbedBuilder embed = new EmbedBuilder();
			embed
				.setTitle("White-List")
				.setDescription(user.getAsMention() + " a été ajouté à la white-list")
				.setColor(game.whitelistToggle ? 0xFFFFEF : 0x000000);
			if(!game.whitelistToggle) {
				embed.setFooter("La white-list est actuellement désactivée !");
			}
			return embed;
		}
		else throw new GlobalException(event.getMessage(),"Erreur utilisateur",user.getAsMention() + " est déjà whitelist dans cette partie.",5);
	}
	
	private static EmbedBuilder remove(GuildMessageReceivedEvent event, Game game) throws InvalidUser, GlobalException {
		User user = Utilities.getMentionedUser(event);
		if(Utilities.find(game.whiteList, user.getId()) != -1) {//already whitelisted
			game.whiteList.remove(user.getId());
			EmbedBuilder embed = new EmbedBuilder();
			embed
				.setTitle("White-List")
				.setDescription(user.getAsMention() + " a été retiré de la white-list")
				.setColor(game.whitelistToggle ? 0xFFFFEF : 0x000000);
			if(!game.whitelistToggle) {
				embed.setFooter("La white-list est actuellement désactivée !");
			}
			return embed;
		}
		else throw new GlobalException(event.getMessage(),"Erreur utilisateur",user.getAsMention() + " n'est pas whitelist dans cette partie.",5);
	}
	
	private static EmbedBuilder toggle(Game game) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("White-list");
		if(game.whitelistToggle) {
			game.whitelistToggle = false;
			embed.setColor(Color.black);
			embed.setDescription("La white-list a été désactivée.");
		}
		else {
			game.whitelistToggle = true;
			embed.setColor(0xFFFFEF);
			embed.setDescription("La white-list a été activée.");
		}
		return embed;
	}
	
	private static EmbedBuilder on(GuildMessageReceivedEvent event, Game game) throws GlobalException {
		if(game.whitelistToggle) throw new GlobalException(event.getMessage(),"Erreur","La white-list est déjà activée.",5);
		game.whitelistToggle = true;
		EmbedBuilder embed = new EmbedBuilder();
		embed
			.setTitle("White-list")
			.setColor(0xFFFFEF)
			.setDescription("La white-list a été activée.");
		return embed;
	}
	
	private static EmbedBuilder off(GuildMessageReceivedEvent event, Game game) throws GlobalException {
		if(!game.whitelistToggle) throw new GlobalException(event.getMessage(),"Erreur","La white-list n'est pas activée.",5);
		game.whitelistToggle = false;
		EmbedBuilder embed = new EmbedBuilder();
		embed
			.setTitle("White-list")
			.setColor(0x000000)
			.setDescription("La white-list a été désactivée.");
		return embed;
	}
}
