package me.astri.discordgarou.gameConfiguration;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.GameManager;
import me.astri.discordgarou.generalGame.LgUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewGame {
	public static void StartNewGame(GuildMessageReceivedEvent event) {
		event.getMessage().delete().queue();
		try {

			Game OldGame = null;
			boolean anotherGame = false;

			for(Game game : GameManager.gameList) {
				if(game.gameOwner.equals(event.getAuthor().getId())) {
					OldGame = game;
					anotherGame = true;
					break;
				}
			}
			if(!anotherGame) {
				Game game = new Game(event);

				event.getGuild().createCategory("Lg - #" + event.getMessageId()).queue((category) -> {
					game.categoryId = category.getId();
					category.createTextChannel(LgUtilities.getChannelName(EnumChannel.Channel.CONFIG_CHANNEL)).queue((channel) -> { //config partie

						PermissionOverride publicRolePerm = event.getChannel().getPermissionOverride(event.getGuild().getPublicRole());
						if(publicRolePerm != null) {
							channel.putPermissionOverride(event.getGuild().getPublicRole())
									.setAllow(publicRolePerm.getAllowed())
									.setDeny(publicRolePerm.getDenied())
									.queue();
						}

						List<PermissionOverride> rolePermList = event.getChannel().getRolePermissionOverrides();
						System.out.println("passe");
						for(PermissionOverride i : rolePermList) {
							channel.createPermissionOverride(i.getRole()).setPermissions(i.getAllowed(), i.getDenied()).deny(Permission.MESSAGE_WRITE,Permission.MESSAGE_MANAGE,Permission.MESSAGE_ADD_REACTION).queue((perm) ->
								perm.getManager().deny(Permission.MESSAGE_WRITE,Permission.MANAGE_CHANNEL,Permission.MESSAGE_MANAGE,Permission.MESSAGE_ADD_REACTION).queue()
							);
						}

						List<PermissionOverride> memberPermList = event.getChannel().getMemberPermissionOverrides();
						for(PermissionOverride i : memberPermList) {
							channel.createPermissionOverride(i.getMember()).setPermissions(i.getAllowed(), i.getDenied()).queue((perm) -> {
								perm.getManager().deny(Permission.MESSAGE_WRITE,Permission.MANAGE_CHANNEL,Permission.MESSAGE_MANAGE,Permission.MESSAGE_ADD_REACTION).queue();

								if(perm.getMember().equals(event.getMember())) {
									Collection<Permission> newPerm = i.getAllowed(); newPerm.add(Permission.MESSAGE_WRITE);
									perm.getManager().setAllow(newPerm).queue();
								}
							});
						}

						event.getChannel().sendMessage("Une partie de Loup-Garou va commencer dans " + channel.getAsMention() + " Venez participer !").queue(
								msg -> game.announceMessageId = msg.getId());
						game.announceChannelId = event.getChannel().getId();

						game.channelList.put(EnumChannel.Channel.CONFIG_CHANNEL,new ArrayList<>());
						GameManager.save(null);
						game.channelList.get(EnumChannel.Channel.CONFIG_CHANNEL).add(new Game.GameChannel(EnumChannel.Channel.CONFIG_CHANNEL,channel));

						DisplayConfig.CreateGameConfig(game);
					});
					category.createTextChannel(LgUtilities.getChannelName(EnumChannel.Channel.PLACE_VILLAGE)).queue((channel) -> { //place village
						channel.createPermissionOverride(event.getMember()).setAllow(LgUtilities.grantedPermissions).queue();

						channel.createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.ALL_PERMISSIONS).queue();

						game.channelList.put(EnumChannel.Channel.PLACE_VILLAGE,new ArrayList<>());
						GameManager.save(null);
						game.channelList.get(EnumChannel.Channel.PLACE_VILLAGE).add(new Game.GameChannel(EnumChannel.Channel.PLACE_VILLAGE,channel));
					});
				});
				GameManager.CreateGame(game);
			}
			else {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("**Erreur**");
				embed.setDescription("Vous ne pouvez pas héberger plusieurs parties à la fois !");
				event.getChannel().sendMessage(embed.build()).queue((message) ->
					message.delete().queueAfter(10, TimeUnit.SECONDS)
				);
				LgUtilities.getConfigChannel(OldGame).sendMessage(
						event.getAuthor().getAsMention() + " tu ne peux pas lancer une nouvelle partie avant de terminer/annuler celle ci !").queue((message) ->
							message.delete().queueAfter(10, TimeUnit.SECONDS)
						);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}
}
