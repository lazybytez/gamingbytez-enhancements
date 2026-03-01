package de.lazybytez.gamingbytezenhancements.lib.util;

public record Pair<K, V>(K key, V value) {
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<K, V>(key, value);
    }
}
