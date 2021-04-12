package me.astri.discordgarou.exceptions;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class GlobalException extends Exception {
	public GlobalException(Message message, String title, String description, int deleteDelay) {
		//if(message.getGuild().getMember(Main.jda.getSelfUser()).hasPermission(Permission.MESSAGE_MANAGE))
			message.addReaction("❌").queue(null, ErrorResponseException.ignore(Arrays.asList(ErrorResponse.values())));
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(title).setDescription(description).setColor(0xff0000);
		message.getChannel().sendMessage(embed.build()).queue((msg) -> {	
			try {
				message.delete().queueAfter(deleteDelay, TimeUnit.SECONDS,
						null, ErrorResponseException.ignore(Arrays.asList(ErrorResponse.values())));
			}catch(Exception ignore) {}
			
			msg.delete().queueAfter(deleteDelay, TimeUnit.SECONDS,
					null, ErrorResponseException.ignore(Arrays.asList(ErrorResponse.values()))); //own msg
		});
	}
}
	