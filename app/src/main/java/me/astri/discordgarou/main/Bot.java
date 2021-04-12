package me.astri.discordgarou.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Bot {

    public static JDA jda;

    private Bot() throws LoginException, InterruptedException {
        JDABuilder jdaBuilder = JDABuilder.createDefault(Config.get("token"));
        jdaBuilder.addEventListeners(
                new me.astri.discordgarou.generalGame.GameManager(),
                new me.astri.discordgarou.main.CommandManager(),
                new me.astri.discordgarou.gameConfiguration.ReactJoinLeave(),
                new me.astri.discordgarou.EventWaiterPack.EventWaiter(),

                new me.astri.discordgarou.generalGame.Test()
        );

        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.enableCache(CacheFlag.MEMBER_OVERRIDES,CacheFlag.EMOTE);
        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);

        jda = jdaBuilder.build().awaitReady();
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.playing("lg!help"));

    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new Bot();
    }
}
