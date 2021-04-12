package me.astri.discordgarou.generalGame;

import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Test extends ListenerAdapter {

    @SuppressWarnings("unused")
    public static void test(@NotNull GuildMessageReceivedEvent event) {
    }

    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
    }

    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    }

    public void onGuildUpdateBoostCount(@NotNull GuildUpdateBoostCountEvent event) {
    }
}
