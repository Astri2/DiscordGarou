package me.astri.discordgarou.generalGame;

import me.astri.discordgarou.LgClassesAndEnums.EnumChannel;
import me.astri.discordgarou.LgClassesAndEnums.EnumRole;
import me.astri.discordgarou.exceptions.GlobalException;
import me.astri.discordgarou.main.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

public class LgUtilities {

    public static Permission[] grantedPermissions = {
            Permission.MESSAGE_READ,
            Permission.MESSAGE_HISTORY,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_ATTACH_FILES,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EXT_EMOJI
    };

    public static int getRolesCount(ArrayList<Game.LgRole> roleList) {
        int roleCount = 0;
        ArrayList<String> treatedRoles = new ArrayList<>();
        for(Game.LgRole role : roleList) {
            if(!treatedRoles.contains(role.roleEnum.name())) {
                treatedRoles.add(role.roleEnum.name());
                roleCount+=getRoleIteration(role.roleEnum,roleList);
            }
        }
        return roleCount;
    }

    public static Game GetGame(GuildMessageReceivedEvent event, EnumChannel.Channel channelType, String perm) throws GlobalException {
        TextChannel channel = event.getChannel();
        for(Game game : GameManager.gameList) {
            for(EnumChannel.Channel lgChannelType : game.channelList.keySet() ) {

                if(!channel.getId().equals(game.channelList.get(lgChannelType).get(0).channelId)) //Id doesn't match
                    continue;
                if(!(perm == null || event.getAuthor().getId().equals(game.gameOwner))) //not enough perm
                    throw new GlobalException(event.getMessage(),"Erreur permissions",event.getAuthor().getAsMention() + ", vous n'avez pas la permission d'effectuer cette commande.",5);
                if(!(channelType == null || channelType.equals(lgChannelType))) //id match but can't channelType requirement doesn't
                    throw new GlobalException(event.getMessage(),"Erreur de channel",event.getAuthor().getAsMention() + ", cette commande ne peut être effectuée dans ce channel.",5);

                return game;
            }
        }throw new GlobalException(event.getMessage(),"Erreur de channel",event.getAuthor().getAsMention() + ", aucune partie n'est associée à ce channel.",5);

    }

    public static int getRoleIteration(EnumRole.Role role, ArrayList<Game.LgRole> roleList) {
        int iteration = 0;
        for(Game.LgRole testedRole : roleList)
            if(testedRole.roleEnum.getFullName().equals(role.getFullName())) iteration++;
        return iteration;
    }

    public static TextChannel getConfigChannel(Game game) {
        System.out.println(game.channelList);
        System.out.println(game.channelList.get(EnumChannel.Channel.CONFIG_CHANNEL));
        System.out.println(game.channelList.get(EnumChannel.Channel.CONFIG_CHANNEL).get(0));
        return Bot.jda.getTextChannelById(game.channelList.get(EnumChannel.Channel.CONFIG_CHANNEL).get(0).channelId);
    }

    public static TextChannel getPlaceVillageChannel(Game game) {
        return Bot.jda.getTextChannelById(game.channelList.get(EnumChannel.Channel.PLACE_VILLAGE).get(0).channelId);
    }

    public static String getChannelName(EnumChannel.Channel channel) {
        return "【" + channel.getChannelEmoji() + "】 " +
                (channel.equals(EnumChannel.Channel.CONFIG_CHANNEL) ? "configuration de la partie" :
                        channel.equals(EnumChannel.Channel.PLACE_VILLAGE) ? "place du village" :
                            channel.getRoleType().getFullName());
    }
}
