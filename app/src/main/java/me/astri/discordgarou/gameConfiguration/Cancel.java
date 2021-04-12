package me.astri.discordgarou.gameConfiguration;

import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.generalGame.Game;
import me.astri.discordgarou.generalGame.GameManager;
import me.astri.discordgarou.generalGame.LgUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Cancel {
	public static void CancelGame(GuildMessageReceivedEvent event) throws GlobalException {
		Game game = LgUtilities.GetGame(event, null, "Owner");

		GameManager.RemoveGame(game);
		List<TextChannel> channels = event.getChannel().getParent().getTextChannels();

		channels.forEach(channel ->  {
			if(!channel.equals(event.getChannel())) channel.delete().queue();
		});

		event.getGuild().getTextChannelById(game.announceChannelId).deleteMessageById(game.announceMessageId)
				.queue(null, ErrorResponseException.ignore(ErrorResponse.UNKNOWN_MESSAGE));

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("**Partie annulée !**");
		embed.setDescription("La partie a été annulée, ce channel sera détruit dans 20 secondes.");
		embed.setTimestamp(Instant.now());
		event.getChannel().sendMessage(embed.build()).queue((message) -> {
			event.getChannel().delete().queueAfter(20, TimeUnit.SECONDS);
			event.getChannel().getParent().delete().queueAfter(20, TimeUnit.SECONDS);
		});
	}
}
