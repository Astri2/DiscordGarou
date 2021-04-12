package me.astri.discordgarou.gameConfiguration;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.LgClassesAndEnums.EnumRole;
import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.Game.LgRole;
import me.astri.discordgarou.generalGame.GameManager;
import me.astri.discordgarou.generalGame.LgUtilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EditRoleConfig {
	
	public static void EditRole(GuildMessageReceivedEvent event) throws GlobalException {
		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL, "Owner");
		if (game == null) return;
		if (game.gameStarted)
			throw new GlobalException(event.getMessage(), "Erreur", "Vous ne pouvez pas éditer la config après le lancement de la partie!", 5);

		String[] args = event.getMessage().getContentRaw().split("\\s+");
		if (args.length < 2)
			throw new GlobalException(event.getMessage(), "Erreur", "Veuillez préciser si vous voulez ajouter, supprimer ou clear des rôles.\n role [+/-] [rôle] (nombre à ajouter/supprimer, 1 par défaut)", 5);

		if (args[1].equalsIgnoreCase("clear") && args.length < 3) {
			List<LgRole> roleList = game.roleList;
			event.getMessage().addReaction("✅").queue();
			event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
			roleList.clear();
			//while(roleList.size() > 0) {
			//	RemoveRole(event.getMessage(), game, roleList.get(0).roleName, roleList.get(0).iteration);
			//}
			event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
			GameManager.save(null);
			return;
		}

		if (args.length < 3)
			throw new GlobalException(event.getMessage(), "Erreur", "Veuillez préciser quel rôle ajouter ou supprimer.\n role [+/-] [rôle] (nombre à ajouter/supprimer, 1 par défaut)", 5);

		EnumRole.Role role;
		int iteration;

		try {
			role = EnumRole.Role.valueOf(args[2].toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new GlobalException(event.getMessage(), "Erreur", "Le rôle spécifié n'est pas reconnu.\n Pour accéder à la liste des rôles, entrez la commande **lg!help role**.", 5);
		}
		if (args.length > 3) {
			try {
				iteration = Integer.parseInt(args[3]);
				if (iteration < 1) iteration = 1;
			} catch (NumberFormatException e) {
				iteration = 1;
			}
		} else iteration = 1;
		switch (args[1].toLowerCase()) {
			case "add":
			case "+":
				AddRole(game, role, iteration);
				event.getMessage().addReaction("✅").queue();
				event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
				event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
				GameManager.save(null);
				return;
			case "remove":
			case "-":
				RemoveRole(event.getMessage(), game, role, iteration);
				event.getMessage().addReaction("✅").queue();
				event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
				event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
				GameManager.save(null);
				return;
			case "clear":
				RemoveRole(event.getMessage(), game, role, LgUtilities.getRoleIteration(role, game.roleList));
				event.getMessage().addReaction("✅").queue();
				event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
				event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
				GameManager.save(null);
				return;
			default:
				throw new GlobalException(event.getMessage(), "Erreur", "L'action à effectuer n'est pas reconnue parmi les possibilités **+** / **add** / **-** / **remove** / **clear**", 5);
		}
	}
	
	public static void AddRole(Game game, EnumRole.Role roleToAdd, int iteration) {
		for(int i = 0 ; i < iteration ; i++)
			game.roleList.add(new LgRole(roleToAdd,null));
	}
	
	public static void RemoveRole(Message message, Game game, EnumRole.Role roleToRemove, int iteration) {
		ArrayList<LgRole> roleList = game.roleList;
		int roleIteration = LgUtilities.getRoleIteration(roleToRemove,game.roleList);
		int iterationToRemove = Math.min(roleIteration, iteration);
		
		int rolesRemoved = 0;
		int i = 0;
		while(rolesRemoved < iterationToRemove) {
			if(roleList.get(i).roleEnum.getFullName().equals(roleToRemove.getFullName())) {
				rolesRemoved++;
				roleList.remove(i);
			}
			else i++;
		}
	}
}
