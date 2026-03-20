/*
 * Gaming Bytez Enhancements - Gameplay enhancements used on our SMP servers.
 * Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
