package me.astri.discordgarou.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Shutdown {
	public static void ShutdownBot(GuildMessageReceivedEvent event) {
		event.getMessage().delete().queue();		
		EmbedBuilder done = new EmbedBuilder();
		done.setColor(0x00ff00);
		done.setTitle("🟢 Success !");
		done.setDescription("Extinction in progress...");
		event.getChannel().sendMessage(done.build()).queue((message) -> {
			done.clear();
			Bot.jda.shutdown();
		});
	}
	 
}
