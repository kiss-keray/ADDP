package com.nix.jingxun.addp.rpc.common.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流式map, 操作方便一些
 */
public class FluentMaps {


    public static <K, V> FluentMap<K, V> newMap() {
        return new DefaultFluentMap<>();
    }

    public static <K, V> FluentMap<K, V> newMap(K k, V v) {
        final DefaultFluentMap<K, V> map = new DefaultFluentMap<>();
        return map.plus(k, v);
    }

    public static <K, V> FluentMap<K, V> newMap(K k1, V v1, K k2, V v2) {
        final DefaultFluentMap<K, V> map = new DefaultFluentMap<>();
        return map.plus(k1, v1).plus(k2, v2);
    }

    public static <K, V> FluentMap<K, V> newHashMap() {
        return new DefaultFluentMap<>(new HashMap<>(16));
    }

    public static <K, V> FluentMap<K, V> newConcurrentHashMap() {
        return new DefaultFluentMap<>(new ConcurrentHashMap<>(16));
    }

    /**
     * 支持流式操作的map
     *
     * @param <K>
     * @param <V>
     */
    public interface FluentMap<K, V> extends Map<K, V> {
        default FluentMap<K, V> plus(K key, V value) {
            Objects.requireNonNull(key);
            this.put(key, value);
            return this;
        }

        default FluentMap<K, V> plus(K k1, V v1, K k2, V v2) {
            Objects.requireNonNull(k1);
            Objects.requireNonNull(k2);
            this.put(k1, v1);
            this.put(k2, v2);
            return this;
        }

        default FluentMap<K, V> plus(K k1, V v1, K k2, V v2, K k3, V v3) {
            Objects.requireNonNull(k1);
            Objects.requireNonNull(k2);
            Objects.requireNonNull(k3);
            this.put(k1, v1);
            this.put(k2, v2);
            this.put(k3, v3);
            return this;
        }


        default FluentMap<K, V> plus(Map<? extends K, ? extends V> map) {
            if (map != null) {
                map.forEach(this::plus);
            }
            return this;
        }
    }

}

class DefaultFluentMap<K, V> implements FluentMaps.FluentMap<K, V> {
    private final Map<K, V> map;

    DefaultFluentMap(Map<K, V> map) {
        this.map = map;
    }

    public DefaultFluentMap() {
        this.map = new HashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }


    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
