package me.astri.discordgarou.main;

import java.util.ArrayList;

import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.exceptions.InvalidUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class Utilities {
	public static User getMentionedUser(@NotNull GuildMessageReceivedEvent event) throws InvalidUser, GlobalException {
		Message message = event.getMessage();
		User user;
		if(message.getMentionedUsers().size() > 0)
			user = message.getMentionedUsers().get(0);
		else throw new InvalidUser(event); 
		return user;
	}
	
	public static int find(ArrayList<String> list, String str) {
		for(int i = 0 ; i < list.size() ; i++) {
			if(list.get(i).equals(str)) return i;
		}
		return -1;
	}
}
