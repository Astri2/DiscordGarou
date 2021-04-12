package me.astri.discordgarou.exceptions;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class InvalidUser extends Exception{

	public InvalidUser(GuildMessageReceivedEvent event) throws GlobalException {
		throw new GlobalException(event.getMessage(),"Utilisateur inconnu","L'utilisateur indiqué n'a pas été reconnu.",5);
	}
}
