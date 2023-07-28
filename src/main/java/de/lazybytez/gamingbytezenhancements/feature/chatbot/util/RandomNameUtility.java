package de.lazybytez.gamingbytezenhancements.feature.chatbot.util;

import com.google.common.collect.Lists;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Utility that provides some random names.
 */
public class RandomNameUtility {
    private static final CopyOnWriteArrayList<String> NAMES;

    static {
        NAMES = new CopyOnWriteArrayList<>(Lists.newArrayList(
            "AmogusPogus",
                "GigaChad",
                "BlockyMcBlockface",
                "CreeperCrusher2000",
                "SteveBot3000",
                "MineCraftyChat",
                "PixelatedPuns",
                "DiamondDork",
                "RedstoneRidiculous",
                "EnderDerp",
                "NotSoCleverCreeper",
                "ZombieZapperZim",
                "PotionPranker",
                "SheepShenanigans",
                "ChickenChatterbox",
                "NetherNonsense",
                "EndermanEpicFail",
                "CowConundrum",
                "GhastlyGuffaws",
                "VillagerVexation",
                "TNTedious",
                "HerobrineHilarity",
                "MemeMachine",
                "ChuckleBot",
                "LulzLord",
                "GiggleGuru",
                "ROFLcopter",
                "SnickerBot",
                "MemeticMax",
                "JokesterJ",
                "LOLinator",
                "PunMaster",
                "TrollyBot",
                "PunnyPals",
                "JestJunkie",
                "GuffawGod",
                "MirthMeister",
                "QuipsterQ",
                "HaHaDroid",
                "JesterXIV",
                "LaughterLad",
                "GagsterGig"
        ));
    }

    public static String getRandomName() {
        if (NAMES.size() < 2) {
            return NAMES.get(0);
        }

        return NAMES.get(new Random().nextInt(0, NAMES.size() - 1));
    }
}
