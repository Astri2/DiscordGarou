package me.astri.discordgarou.main;

import me.astri.discordgarou.exceptions.ExceptionManager;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class CommandManager extends ListenerAdapter{
	public static final ArrayList<Command> commands = new ArrayList<>();

	private enum permissions { All,GameOwner,GuildModerator,BotOwner }
	private static class Command {
		private final String command;
		private final ArrayList<String> alias;
		private final permissions permission;
		private final String method;
		private final String clazz;

		public Command(String command, ArrayList<String> alias, permissions permission, String method, String clazz) {
			this.command = command;
			this.alias = alias;
			this.permission = permission;
			this.method = method;
			this.clazz = clazz;
		}
	}

	@Override
	public void onReady(@Nonnull ReadyEvent event) { //Setup command management system
		try {
			JSONArray JsonCommands = new JSONObject(FileManager.Read("data/Commands.json")).getJSONArray("CommandList");
			for(int i = 0 ; i < JsonCommands.length() ; i++) {
				JSONObject JsonCmd = JsonCommands.getJSONObject(i);

				ArrayList<String> Alias = new ArrayList<>();
				for(int k = 0 ; k < JsonCmd.getJSONArray("Alias").length() ; k++)
					Alias.add(JsonCmd.getJSONArray("Alias").getString(k));

				Command cmd = new Command(
						JsonCmd.getString("Command"),
						Alias,
						permissions.valueOf(JsonCmd.getString("Permission")),
						JsonCmd.getString("Method"),
						JsonCmd.getString("Class")
				);
				commands.add(cmd);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
		if(event.getAuthor().getId().equals(Config.get("bot_id"))) return;

		try {
			String[] args = event.getMessage().getContentRaw().split("\\s+");
			if (!args[0].toLowerCase().startsWith(Config.get("prefix")))
				return;

			String userCmd = args[0].replace(Config.get("prefix"), "").toLowerCase();

			for (Command cmd : commands) {
				if (userCmd.equalsIgnoreCase(cmd.command) || cmd.alias.contains(userCmd)) {
					Class<?> clazz = Class.forName(cmd.clazz);
					java.lang.reflect.Method m = clazz.getDeclaredMethod(cmd.method, event.getClass());
					m.invoke(null, event);
				}
			}
		} catch (Exception e) {
			try{
				throw e.getCause();
			}catch(Throwable e1) {
				assert e1 instanceof Exception;
				ExceptionManager.errorHandler(event,(Exception) e1); //basic Exception doesn't have message with invoke
			}
		}
	}
}
