package com.weatherapp.cache;

import java.time.Instant;

public class CacheEntry<T> {

    private final T data;
    private final Instant savedAt;

    public CacheEntry(T data, Instant savedAt) {
        this.data = data;
        this.savedAt = savedAt;
    }

    public T getData() {
        return data;
    }

    public Instant getSavedAt() {
        return savedAt;
    }
}
