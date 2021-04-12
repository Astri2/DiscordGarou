package me.astri.discordgarou.exceptions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.menudocs.paste.PasteClient;
import org.menudocs.paste.PasteClientBuilder;
import org.menudocs.paste.PasteHost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/*
NULL POINTER EXCEPTION CAUSES (when the handler doesn't work) :
    - non static method
 */
public class ExceptionManager {

    private static final PasteClient client = new PasteClientBuilder()
            .setUserAgent("DiscordGarouBot")
            .setDefaultExpiry("10m")
            .build();

    public static void errorHandler(GuildMessageReceivedEvent event, Exception e) {
        System.out.println("Error!");
        if(e instanceof  GlobalException || e instanceof  InvalidUser)
            return; //Own Exception, already handled
        if(e instanceof InsufficientPermissionException) {
            PermissionException pe = (PermissionException) e;
            Permission missingPerm = pe.getPermission();
            if(missingPerm == Permission.MESSAGE_WRITE) { //put here all perms I want to ignore
                System.out.println("can't write");
                event.getMessage().addReaction(":error:787644097287553025").queue(
                        null, ErrorResponseException.ignore(ErrorResponse.MISSING_PERMISSIONS)); //ignore error
            }
            else ExceptionManager.sendErrorLog(event,e);
        }
        else ExceptionManager.sendErrorLog(event,e);
    }

    public static void sendErrorLog(GuildMessageReceivedEvent event, Exception e) {
        TextChannel logChannel = event.getJDA().getGuildById("673204175575711759").getTextChannelById("787659459001581580");
        EmbedBuilder logEb = new EmbedBuilder();
        logEb.setTitle("New Exception")
                .addField("**Message link**", event.getMessage().getJumpUrl(),false)
                .addField("**Channel mention**", event.getChannel().getAsMention(),false)
                .addField("**Guild name**",event.getGuild().getName(),false)
                .addField("content",getErrorLog(event,e),false)
                .setFooter(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        logChannel.sendMessage(logEb.build()).queue();

        event.getMessage().addReaction(":error:787644097287553025").queue(null, ErrorResponseException.ignore(Arrays.asList(ErrorResponse.values())));

        if(e.getMessage() == null || !e.getMessage().contains("Cannot perform action due to a lack of Permission. Missing permission: MESSAGE_WRITE") && event.getChannel().canTalk()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Unhandled error: Please report this to my developer!")
                    .setDescription(getErrorLog(event,e))
                    .setFooter(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
            event.getChannel().sendMessage(eb.build()).queue();
        }

        System.out.println("\n\n\n\n" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
        e.printStackTrace();
    }

    public static String getErrorLog(GuildMessageReceivedEvent event, Exception e) {
        StringBuilder content = new StringBuilder("Message from " + event.getAuthor().getName() + ": `" + event.getMessage().getContentRaw() + "`\n");
        content.append("Exception message: `").append(e.getMessage()).append("`\n```");
        for(StackTraceElement element : e.getStackTrace()) {
            content.append(element.getClassName()).append(" ").append(element.getMethodName()).append(" ").append(element.getLineNumber()).append("\n");
            if(element.getClassName().equals("at me.astri.discordgarou.main.CommandManager")) break;
        }
        content.append("```");
        if(content.length() <= 1024) {
            return content.toString();
        }
        else {
            String pasteID = client.createPaste("markdown",content.toString()).execute();
            return client.getPasteUrl(pasteID);
        }
    }
}
