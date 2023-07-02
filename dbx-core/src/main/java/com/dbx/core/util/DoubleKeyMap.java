package com.dbx.core.util;

import org.checkerframework.checker.units.qual.K;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Aqoo
 */
public class DoubleKeyMap<K1, K2, V> {
    private final HashMap<Pair<K1, K2>, V> map = new HashMap<>();

    public boolean containsKey(K1 key1, K2 key2) {
        return map.containsKey(new Pair<>(key1, key2));
    }

    public V get(K1 key1, K2 key2) {
        return map.get(new Pair<>(key1, key2));
    }

    public V put(K1 key1, K2 key2, V v) {
        return map.put(new Pair<>(key1, key2), v);
    }

    public V remove(K1 key1, K2 key2) {
        return map.remove(new Pair<>(key1, key2));
    }


    static class Pair<K1, K2> {
        private final K1 key1;
        private final K2 key2;

        public Pair(K1 key1, K2 key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Pair<K1, K2> pair = (Pair<K1, K2>) o;
            return Objects.equals(key1, pair.key1) && Objects.equals(key2, pair.key2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key1, key2);
        }
    }
}
