package me.astri.discordgarou.LgClassesAndEnums;

public class EnumChannel {
    public enum Channel {
        CONFIG_CHANNEL("🛠") {
            @Override
            public EnumRole.Role getRoleType() {
                return EnumRole.Role.NOTHING;
            }
        },
        PLACE_VILLAGE("🏘") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.NOTHING; }
        },
        SV("👨‍🌾") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.SIMPLE_VILLAGEOIS; }
        },
        CUPI("💘") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.CUPIDON; }
        },
        CORB("🕊") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.CORBEAU; }
        },
        LG("🐺") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.LOUP_GAROU; }
        },
        IPDL("🐺") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.INFECT_PERE_DES_LOUPS; }
        },
        ASSA("🔪") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.ASSASSIN; }
        },
        LGB("🐺") {
            @Override
            public EnumRole.Role getRoleType() { return EnumRole.Role.LOUP_GAROU_BLANC; }
        },
        ;

        Channel(String a) {
            channelEmoji = a;
        }

        public abstract EnumRole.Role getRoleType(); //Overrided
        public String getChannelEmoji() { return channelEmoji; }

        private final String channelEmoji;
    }
}
