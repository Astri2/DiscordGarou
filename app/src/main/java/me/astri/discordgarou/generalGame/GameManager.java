package me.astri.discordgarou.generalGame;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import me.astri.discordgarou.main.Bot;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GameManager extends ListenerAdapter{
	
	public static ArrayList<Game> gameList;

	public void onReady(@Nonnull ReadyEvent event) {
		GameManager.gameList = new ArrayList<>();
		try {
			GameManager.load(null);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fatal error while loading games file!");
			Bot.jda.shutdown();
		}
	}
	
	public static void CreateGame(Game game) {
		GameManager.gameList.add(game);
		GameManager.save(null);
	}

	public static void RemoveGame(Game game) {
		GameManager.gameList.remove(game);
		GameManager.save(null);
	}

	public static void save(GuildMessageReceivedEvent event) {
		boolean success = true;
		try {
			FileOutputStream fos = new FileOutputStream("data/gameList.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(GameManager.gameList);
			oos.flush();
			oos.close();
		} catch(IOException e) {e.printStackTrace();}

		if(event != null) {
			event.getMessage().addReaction(success ? "✔" : "❌").queue();
			event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
		}
	}

	@SuppressWarnings("unchecked") //because of an Object to Game cast
	public static void load(GuildMessageReceivedEvent event) {
		boolean success = true;
		try {
			FileInputStream fis = new FileInputStream("data/gameList.dat");
				ObjectInputStream ois = new ObjectInputStream(fis);
				GameManager.gameList = (ArrayList<Game>) ois.readObject();
				fis.close();
				ois.close();

		} catch(EOFException e) { //file empty
			GameManager.gameList = new ArrayList<>();
		} catch(ClassNotFoundException | IOException e) {e.printStackTrace();}
		if(event != null) {
			event.getMessage().addReaction(success ? "✔" : "❌").queue();
			event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
		}
	}
}
