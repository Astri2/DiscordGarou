package me.astri.discordgarou.LgClassesAndEnums;

public class EnumRole {

    public enum Role {
        NOTHING("nothing", Side.VILLAGE, "") {
            @Override
            public EnumChannel.Channel getChannelType() {
                return EnumChannel.Channel.CONFIG_CHANNEL;
            }
        },
        SIMPLE_VILLAGEOIS("Simple villageois", Side.VILLAGE,"<:sv:708408665559924807>") {
            @Override
            public EnumChannel.Channel getChannelType() { return EnumChannel.Channel.SV; }
        },
        CUPIDON("Cupidon", Side.VILLAGE,"<:cupi:708408781062799438>") {
            @Override
            public EnumChannel.Channel getChannelType() { return EnumChannel.Channel.CUPI; }
        },
        CORBEAU("Corbeau", Side.VILLAGE,"<:corbeau:708408805918113824>") {
            @Override
            public EnumChannel.Channel getChannelType() { return EnumChannel.Channel.CORB; }
        },
        LOUP_GAROU("Loup garou", Side.WEREWOLVES,"<:lg:708408708853530666>") {
            @Override
            public EnumChannel.Channel getChannelType() { return EnumChannel.Channel.LG; }
        },
        INFECT_PERE_DES_LOUPS("Infect pere des loups", Side.WEREWOLVES,"<:ipdl:708408733667033119>") {
            @Override
            public EnumChannel.Channel getChannelType() { return EnumChannel.Channel.IPDL; }
        },
        ASSASSIN("Assassin", Side.ALONE,"<:assassin:708408826717798460>") {
            @Override
            public EnumChannel.Channel getChannelType() { return EnumChannel.Channel.ASSA; }
        },
        LOUP_GAROU_BLANC("Loup garou blanc", Side.ALONE,"<:lgb:708408686497890304>") {
            @Override
            public EnumChannel.Channel getChannelType() { return EnumChannel.Channel.LGB; }
        },
        ;
        public enum Side {VILLAGE, WEREWOLVES, ALONE}

        Role(String a, Side b, String c) {
            fullName = a;
            side = b;
            emote = c;
        }

        public abstract EnumChannel.Channel getChannelType(); //Overrided
        public String getFullName() {return fullName;}
        public Side getSide() {return side;}
        public String getEmote() {return emote;}
        private final String fullName;
        private final Side side;
        private final String emote;
    }

}
