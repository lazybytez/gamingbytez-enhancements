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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast;

import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastDimensions;

/**
 * The tuning table for a charge's blast level.
 * <p>
 * {@code size} is the full width of the affected volume in blocks, {@code crossSection} is the side
 * length a bored corridor carves and {@code centreDamage} is the value passed to
 * {@code LivingEntity#damage} at the centre of the blast.
 */
public enum BlastLevel {
    LEVEL_1(1, 8, 3, 10.0, 0x3CE85A, 0.9),
    LEVEL_2(2, 16, 4, 18.0, 0xE8DC3C, 1.0),
    LEVEL_3(3, 24, 6, 26.0, 0xE8913C, 1.1),
    LEVEL_4(4, 32, 8, 34.0, 0xE83C3C, 1.25),
    LEVEL_5(5, 64, 8, 42.0, 0x8E0B0B, 1.5);

    /**
     * The lowest valid blast level.
     */
    public static final int MIN_LEVEL = 1;

    /**
     * The highest valid blast level.
     */
    public static final int MAX_LEVEL = 5;

    private final int level;
    private final int size;
    private final int crossSection;
    private final double centreDamage;
    private final int armingColour;
    private final double waveSpeed;

    BlastLevel(
            int level,
            int size,
            int crossSection,
            double centreDamage,
            int armingColour,
            double waveSpeed
    ) {
        this.level = level;
        this.size = size;
        this.crossSection = crossSection;
        this.centreDamage = centreDamage;
        this.armingColour = armingColour;
        this.waveSpeed = waveSpeed;
    }

    /**
     * Looks up the blast level entry for the given integer level, clamping any out-of-range
     * input into {@link #MIN_LEVEL}..{@link #MAX_LEVEL}.
     *
     * @param level the requested level, may be outside the valid range
     * @return the matching {@link BlastLevel}, clamped into range
     */
    public static BlastLevel of(int level) {
        int clamped = Math.clamp(level, BlastLevel.MIN_LEVEL, BlastLevel.MAX_LEVEL);

        for (BlastLevel entry : BlastLevel.values()) {
            if (entry.level == clamped) {
                return entry;
            }
        }

        throw new IllegalStateException("No BlastLevel entry for clamped level " + clamped);
    }

    /**
     * Returns the numeric level, from {@link #MIN_LEVEL} to {@link #MAX_LEVEL}.
     *
     * @return the level of this entry
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Returns how far the blast wavefront travels per tick, in blocks of radius.
     * <p>
     * The speed grows slightly with the level, so a big blast reads as a stronger shock rather
     * than a slower one, while its greater extent still makes the whole carve last longer. Every
     * level lands between half a second and two seconds: fast enough to impress, slow enough for
     * a player to watch the wave travel.
     *
     * @return the wavefront speed in blocks per tick
     */
    public double getWaveSpeed() {
        return this.waveSpeed;
    }

    /**
     * Returns the colour the arming particles carry, as a packed RGB value.
     * <p>
     * The ramp runs green through amber to red, so a player reads how much of the world a charge
     * is about to remove before it goes off rather than after.
     *
     * @return the packed RGB arming colour
     */
    public int getArmingColour() {
        return this.armingColour;
    }

    /**
     * Returns the full width of the affected volume in blocks.
     *
     * @return the blast size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Returns the measurements a geometry carves this level to.
     * <p>
     * This is the single place a level turns into blocks, so a geometry never has to read the
     * table itself. The top two levels share the eight block cross section deliberately: past
     * eight blocks a corridor stops reading as a tunnel, so level five spends its whole growth on
     * length.
     *
     * @return the dimensions of this level
     */
    public BlastDimensions getDimensions() {
        return new BlastDimensions(this.size, this.crossSection);
    }

    /**
     * Returns the damage value applied at the centre of the blast.
     *
     * @return the centre damage
     */
    public double getCentreDamage() {
        return this.centreDamage;
    }

}
