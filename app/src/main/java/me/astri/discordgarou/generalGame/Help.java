package me.astri.discordgarou.generalGame;

import me.astri.discordgarou.LgClassesAndEnums.EnumGamerule;
import me.astri.discordgarou.LgClassesAndEnums.EnumRole;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import me.astri.discordgarou.main.FileManager;

public class Help{
	public static void DisplayHelp(GuildMessageReceivedEvent event) throws JSONException {
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		if(args[0].equalsIgnoreCase("lg!help")) {
			event.getMessage().delete().queue();
			if(args.length == 1) {
				MessageBuilder msgBuilder = new MessageBuilder();
				EmbedBuilder embed = new EmbedBuilder();
				JSONArray JsonEmbedList = new JSONObject(FileManager.Read("data/Help.json")).getJSONArray("Help");
				for(int i = 0 ; i < JsonEmbedList.length() ; i++) {
					embed.setColor(0x0000FF);
					embed.setTitle(JsonEmbedList.getJSONObject(i).getString("Title"));
					JSONArray JsonFieldList = JsonEmbedList.getJSONObject(i).getJSONArray("Fields");
					for(int k = 0 ; k < JsonFieldList.length() ; k++) {
						embed.addField(JsonFieldList.getJSONObject(k).getString("FieldTitle"),JsonFieldList.getJSONObject(k).getString("FieldContent"),false);
					}
					msgBuilder.append(embed.build());

					embed.clear();
				}
				sendHelpMessage(event.getAuthor(),msgBuilder,event.getChannel());
			}
			else if(args[1].equalsIgnoreCase("role")) {
				RoleList(event.getChannel());
			}
			else if(args[1].equalsIgnoreCase("gamerule")) {
				GameruleList(event.getChannel());
			}
			else {			
				try {
					JSONObject JsonRoleList = new JSONObject(FileManager.Read("data/Help.json")).getJSONObject("RolesList");
					EmbedBuilder embed = new EmbedBuilder();
					JSONObject JsonRole = JsonRoleList.getJSONObject(args[1].toLowerCase());
					embed.setTitle(JsonRole.getString("Title"));
					embed.setThumbnail(JsonRole.getString("Url"));
					switch(JsonRole.getString("Side")) {
					case "Village":
						embed.setColor(0x00FF00);
						embed.addField("Camp","Villageois",false);
						break;
					case "Werewolves":
						embed.setColor(0xFF0000);
						embed.addField("Camp","Loups-garous",false);
						break;
					case "Neutral":
						embed.setColor(0x0000FF);
						embed.addField("Camp","Neutre",false);
						break;
					default:
						break;
					}
					embed.addField("Descirption",JsonRole.getString("Description"),false);
					event.getChannel().sendMessage(embed.build()).queue();
				}
				catch (JSONException e) {if(e.getMessage().startsWith("JSONObject[\"")) {
						try {
							JSONObject JsonGameruleList = new JSONObject(FileManager.Read("data/Help.json")).getJSONObject("Gamerules");
							EmbedBuilder embed = new EmbedBuilder();
							JSONObject JsonGamerule = JsonGameruleList.getJSONObject(args[1].toLowerCase());
							embed.setTitle(JsonGamerule.getString("Title"));
							embed.setThumbnail(JsonGamerule.getString("Url"));
							embed.addField("Descirption",JsonGamerule.getString("Description"),false);
							event.getChannel().sendMessage(embed.build()).queue();
						}
						catch(JSONException e1) {if(e1.getMessage().startsWith("JSONObject[\"")) {
								event.getChannel().sendMessage("Erreur, `" + args[1] + "` n'est pas reconnu en tant que r�le ou r�gle de jeu").queue();
							} else e.printStackTrace();
						}
					} else e.printStackTrace();
				}
			}
		}
	}
	
	public static void RoleList(TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Liste des roles");
		
		
		String str = "";
		EnumRole.Role[] roles = EnumRole.Role.values();
		for(int i = 1 ; i < roles.length ; i++) {
			str += roles[i].name() + " ; ";
		}
		str += "END";
		str = str.replace(" ; END", "");
		embed.setDescription(str);
		channel.sendMessage(embed.build()).queue();
	}
	
	public static void GameruleList(TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Liste des r�gles");
		String str = "";
		EnumGamerule.Gamerule[] gamerules = EnumGamerule.Gamerule.values();
		for(EnumGamerule.Gamerule gamerule : gamerules)
			str += gamerule.name() + " ; ";
		str += "END";
		str = str.replace(" ; END", "");
		embed.setDescription(str);
		channel.sendMessage(embed.build()).queue();
	}

	private static void sendHelpMessage(User user, MessageBuilder msgBuilder, TextChannel context) {
		user.openPrivateChannel()
				.flatMap(channel -> channel.sendMessage(msgBuilder.build()))
				.queue(success ->
					context.sendMessage(user.getAsMention() + ", check your DM's!").queue()
				, new ErrorHandler()
						.ignore(ErrorResponse.UNKNOWN_MESSAGE) // if delete fails that's fine
						.handle(
								ErrorResponse.CANNOT_SEND_TO_USER,  // Fallback handling for blocked messages
								(e) -> context.sendMessage(user.getAsMention() + ", failed to send message, you block private messages!").queue()));
	}
}
