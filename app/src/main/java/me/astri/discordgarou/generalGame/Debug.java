package me.astri.discordgarou.generalGame;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.exceptions.InvalidUser;
import me.astri.discordgarou.gameConfiguration.DisplayConfig;
import me.astri.discordgarou.gameConfiguration.GameModeration;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Debug extends ListenerAdapter{

	public static void RemoveCategory(GuildMessageReceivedEvent event) throws IOException {
		event.getMessage().delete().queue((msg) -> {
			List<Category> list = event.getGuild().getCategories();
			for(Category i : list) {
				if(i.getName().toLowerCase().startsWith("lg ")) {
					List<TextChannel> cList = i.getTextChannels();
					for(TextChannel k : cList) {
						k.delete().queue();
					}
					i.delete().queue();
				}
			}
		});

		new FileWriter("data/gameList.dat", false).close();
		GameManager.load(null);
	}

	public static void forceAddPlayer(GuildMessageReceivedEvent event) throws GlobalException, InvalidUser {
		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL,"Owner");
		final String memberId;

		if(event.getMessage().getMentionedMembers().isEmpty()) {
			try {
				String id = Arrays.asList(event.getMessage().getContentRaw().split("\\s+")).get(1);
				event.getGuild().getMemberById(id).getId();
				memberId = id;
			}catch(NullPointerException unused) {throw new InvalidUser(event);}
		} else memberId = event.getMessage().getMentionedMembers().get(0).getId(); //get by mention

		if(game.playerList.stream().noneMatch(player -> player.playerId.equals(memberId))) {
			GameModeration.AddPlayer(game, memberId);
			event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
		}
	}


	public static void displayGame(GuildMessageReceivedEvent event) throws GlobalException {
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL,"Owner");

		StringBuilder str = new StringBuilder("```");

		if(args.length == 1) { //want to display all information.
			getPlayers(event, game, str);
			getRoles(game, str);
			getGamerules(game, str);
		}

		else switch(args[1]) {
			case "players":
			case "p":
				getPlayers(event, game, str);
				break;
			case "roles":
			case "r":
				getRoles(game, str);
				break;
			case "gamerules":
			case "g":
				getGamerules(game, str);
				break;
			case "all":
			case "a":
				getPlayers(event, game, str);
				getRoles(game, str);
				getGamerules(game, str);
				break;
			default:
				throw new GlobalException(event.getMessage(), "Erreur", "argument non reconnu.\nUsage : lg!display [players/roles/gamerules/all]", 5);
		}
		event.getChannel().sendMessage(str + "```").queue();
	}
	
	private static void getPlayers(GuildMessageReceivedEvent event, Game game, StringBuilder str) {
		str.append("Players : \n");
		for(Game.Player player : game.playerList) {
			str.append(event.getJDA().getUserById(player.playerId)
					.getName()).append(" (").append(player.role.roleEnum).append(") ");
		}
		str.append("\n\n");
	}
	
	private static void getRoles(Game game, StringBuilder str) {
		str.append("Roles : \n");
		for(Game.LgRole role : game.roleList) {
			str.append(role.roleEnum.name()).append(" ");
		}
		str.append("\n\n");
	}
	
	private static void getGamerules(Game game, StringBuilder str) {
		str.append("Gamerules : \n");
		for(Game.Gamerule gamerule : game.gameruleList) {
			str.append(gamerule.gmEnum.name()).append("(").append(gamerule.gmEnum.Type().equals("int") ? gamerule.value : (gamerule.value == 1 ? "true" : "false")).append(") ");
		}
		str.append("\n\n");
	}
}
