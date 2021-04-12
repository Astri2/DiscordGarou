package me.astri.discordgarou.gameConfiguration;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.LgClassesAndEnums.EnumRole;
import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.game.GameLaunching;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.Game.LgRole;
import me.astri.discordgarou.generalGame.GameManager;
import me.astri.discordgarou.generalGame.LgUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Start {
	public static void StartGame(GuildMessageReceivedEvent event) throws Exception {
		Game game = LgUtilities.GetGame(event, EnumChannel.Channel.CONFIG_CHANNEL, "Owner");
		if (game.playerList.size() < 2) //TODO set a 4
			throw new GlobalException(event.getMessage(), "Erreur", "Vous ne pouvez pas lancer une partie avec moins de 4 joueurs !", 5);

		int rolesNumber = LgUtilities.getRolesCount(game.roleList);

		if (rolesNumber > game.playerList.size())
			throw new GlobalException(event.getMessage(), "Erreur", "Vous ne pouvez pas lancer une partie avec plus de r�les que de joueurs !", 5);
		if (isEveryoneTogether(game.roleList, game.playerList.size() > rolesNumber))
			throw new GlobalException(event.getMessage(), "Erreur", "Vous ne pouvez pas lancer une partie avec tous les rôles dans le même camp potentiel !", 5);

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("La partie va commencer.")
				.setDescription("Toute modification de la config est maintenant impossible.")
				.setColor(0x0000ff)
				.setTimestamp(Instant.now())
				.setFooter(game.gameId);

		if (game.playerList.size() > rolesNumber) {
			int difference = game.playerList.size() - rolesNumber;
			EditRoleConfig.AddRole(game, EnumRole.Role.SIMPLE_VILLAGEOIS, difference);
			event.getChannel().retrieveMessageById(game.configMessageId).queue(msg -> DisplayConfig.EditGameConfig(game, msg));
			GameManager.save(null);
			embed.appendDescription("\n\n_Il y a " + difference + " joueurs de plus que de roles dans la partie, des simples villageois ont donc été ajoutés en conséquence._");
		}
		LgUtilities.getPlaceVillageChannel(game).sendMessage(embed.build()).queue();
		List<PermissionOverride> perms = event.getChannel().getPermissionOverrides();
		for (PermissionOverride perm : perms) {
			if (perm.isMemberOverride() && perm.getMember().getId().equals(game.gameOwner)) {
				perm.delete().queue();
				break;
			}
		}
		GameManager.save(null);
		GameLaunching.Launch(game);
	}
	
	private static Boolean isEveryoneTogether(ArrayList<Game.LgRole> roleList, Boolean needSV) {
		if(roleList.isEmpty()) return true;
		EnumRole.Role.Side oldSide = (needSV ? EnumRole.Role.Side.VILLAGE : roleList.get(0).roleEnum.getSide());
		for(LgRole role : roleList) {
			if(role.roleEnum.getSide().equals(EnumRole.Role.Side.ALONE) || !role.roleEnum.getSide().equals(oldSide)) {
				return false;
			}
			oldSide = role.roleEnum.getSide();
		}
		return true;
	}
}
