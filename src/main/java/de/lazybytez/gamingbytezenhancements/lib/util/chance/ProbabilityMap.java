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
