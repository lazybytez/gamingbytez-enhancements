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
package de.lazybytez.gamingbytezenhancements.lib.util.chance;

import de.lazybytez.gamingbytezenhancements.lib.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProbabilityMap<V> {
    private double probabilitySum = 0;
    private final HashMap<V, Double> map = new HashMap<>();

    public void put(V value, Double probability) {
        this.map.put(value, probability);
        this.probabilitySum += probability;
    }

    @SafeVarargs
    public final void put(Pair<V, Double>... pairs) {
        for (Pair<V, Double> pair: pairs) {
            this.put(pair.key(), pair.value());
        }
    }

    public double get(V value) {
        return this.map.get(value);
    }

    public double getProbabilitySum() {
        return this.probabilitySum;
    }

    public Set<Map.Entry<V, Double>> entrySet() {
        return this.map.entrySet();
    }

    public Collection<Double> values() {
        return this.map.values();
    }
}
