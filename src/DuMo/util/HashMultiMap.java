package DuMo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HashMultiMap<K, V> {
    HashMap<K, ArrayList<V>> map = new HashMap<>();

    public void add(K key, V value) {
        ArrayList<V> list = map.computeIfAbsent(key, (k) -> new ArrayList<>());
        list.add(value);
    }

    public Collection<V> get(K key) {
        return map.get(key);
    }

    public Collection<V> remove(K key) {
        return map.remove(key);
    }

    public boolean remove(K key, V value) {
        ArrayList<V> list = map.get(key);
        boolean changed = list.remove(value);
        if (list.isEmpty()) map.remove(key);
        return changed;
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach((k, vs) ->
                vs.forEach((v) ->
                        action.accept(k, v)
                )
        );
    }

    public void clear() {
        map.clear();
    }
}
