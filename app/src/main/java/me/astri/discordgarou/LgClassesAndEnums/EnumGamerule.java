package me.astri.discordgarou.LgClassesAndEnums;

public class EnumGamerule {
    public enum Gamerule {
        MAIRE("Maire",1,"bool",-1,-1),
        COUPLE_ALEATOIRE("Couple aleatoire",0,"bool",-1,-1),
        MALEDICTION_DU_CORBEAU("Malediction du corbeau",2,"int",1,3);

        Gamerule(String a, int b, String c, int d, int e) {
            fullName = a;
            baseValue = b;
            type = c;
            min = d;
            max = e;
        }
        public String FullName() {return fullName;}
        public int BaseValue() {return baseValue;}
        public String Type() {return type;}
        public int Min() {return min;}
        public int Max() {return max;}

        private final String fullName;
        private final int baseValue;
        private final String type;
        private final int min;
        private final int max;
    }
}
