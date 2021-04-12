package me.astri.discordgarou.game;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.LgClassesAndEnums.EnumRole;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.LgUtilities;
import me.astri.discordgarou.main.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class GameLaunching {
	public static void Launch(Game game) {

		Guild guild = Bot.jda.getGuildById(game.guildId);
		Category category = guild.getCategoryById(game.categoryId);
		ArrayList<Game.LgRole> roleList = game.roleList;
		ArrayList<Game.Player> playerList = game.playerList;
		HashMap<EnumChannel.Channel,ArrayList<Game.GameChannel>> channelList = game.channelList;

		System.out.println(channelList);

		game.gameStarted = true;
		ClearChannel(game);
		Bot.jda.getTextChannelById(game.announceChannelId).deleteMessageById(game.announceMessageId)
				.queue(null, ErrorResponseException.ignore(ErrorResponse.UNKNOWN_MESSAGE));

		AssignRoles(roleList,playerList);


		for(Game.LgRole lgRole : roleList) {
			TextChannel channel = null;
			switch(lgRole.roleEnum) {
			case CUPIDON:
			case CORBEAU:
				case ASSASSIN:
				case SIMPLE_VILLAGEOIS:
				CreateChannel(lgRole.roleEnum,guild,category,channelList); //private channel
				setPermissions(lgRole.roleEnum,lgRole.player.playerId,guild,channelList);
				break;

				case INFECT_PERE_DES_LOUPS:
				case LOUP_GAROU_BLANC:
				CreateChannel(lgRole.roleEnum,guild,category,channelList); //private channel
				setPermissions(lgRole.roleEnum,lgRole.player.playerId,guild,channelList);
				case LOUP_GAROU:
				if(!channelList.containsKey(EnumChannel.Channel.LG)) //shared channel
					CreateChannel(EnumRole.Role.LOUP_GAROU, guild, category, channelList);
				setPermissions(EnumRole.Role.LOUP_GAROU,lgRole.player.playerId,guild,channelList);
				break;
			default:
				System.out.println("error, role non reconnu");
				break;
			}


		}
	}
	
	private static void ClearChannel(Game game) {
		TextChannel configChannel = LgUtilities.getConfigChannel(game);
		AtomicInteger nbrMsg = new AtomicInteger(0);

		configChannel.retrieveMessageById(game.configMessageId).queue(msg -> msg.clearReactions().queue());

		do {
			configChannel.getHistory().retrievePast(100).queue(list -> {
				nbrMsg.set(list.size());

				System.out.println(list.size());

				configChannel.retrieveMessageById(game.configMessageId).queue(msg -> {
					list.remove(msg);
					configChannel.deleteMessages(list).queue();

				});

				System.out.println(list.size());
				System.out.println(nbrMsg.get());
			});

		} while(nbrMsg.get() == 100);
	}

	private static void AssignRoles(ArrayList<Game.LgRole> roleList, ArrayList<Game.Player> playerList)
	{
		Collections.shuffle(roleList);
		Collections.shuffle(playerList);
		StringBuilder roleLog = new StringBuilder();

		for(int i = 0 ; i < playerList.size() ; i++) {
			playerList.get(i).role = roleList.get(i);
			roleList.get(i).player = playerList.get(i);
			roleLog.append(Bot.jda.getUserById(playerList.get(i).playerId).getAsMention()).append(" is ")
					.append(playerList.get(i).role.roleEnum).append("\n");
		}
		roleLog.append("===========" + "\n");
		Bot.jda.getGuildById("720014513025581117").getTextChannelById("816057561648332801").sendMessage(roleLog.toString()).queue();
	}

	private static void CreateChannel(EnumRole.Role roleEnum, Guild guild, Category category, HashMap<EnumChannel.Channel,ArrayList<Game.GameChannel>> channelList) {
		TextChannel channel = category.createTextChannel(LgUtilities.getChannelName(roleEnum.getChannelType())).complete();
		if(!channelList.containsKey(roleEnum.getChannelType()))
			channelList.put(roleEnum.getChannelType(),new ArrayList<>());

		channelList.get(roleEnum.getChannelType()).add(new Game.LgChannel(roleEnum.getChannelType(),channel,new ArrayList<>()));
		channel.getPermissionOverride(guild.getPublicRole()).getManager().setDeny(Permission.ALL_PERMISSIONS).queue();
	}
	
	private static void setPermissions(EnumRole.Role roleEnum, String memberId, Guild guild, HashMap<EnumChannel.Channel,ArrayList<Game.GameChannel>> channelList) {
		ArrayList<Game.GameChannel> list = channelList.get(roleEnum.getChannelType());
		Game.LgChannel lgChannel = (Game.LgChannel) list.get(list.size()-1);
		lgChannel.channelMemberId.add(memberId);
		TextChannel channel = guild.getTextChannelById(lgChannel.channelId);	//last index is the last added channel
		channel.createPermissionOverride(guild.getMemberById(memberId)).setAllow(LgUtilities.grantedPermissions).queue();
		
	}
}