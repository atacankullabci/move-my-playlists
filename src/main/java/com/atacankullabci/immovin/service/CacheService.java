package com.atacankullabci.immovin.service;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CacheService {

    private final HazelcastInstance hazelcastInstance;

    public CacheService(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void put(String code) {
        Map<String, Boolean> hazelcastMap = hazelcastInstance.getMap("spotify-code-cache");
        hazelcastMap.put(code, false);
    }

    public List<String> getAllInactivatedCaches() {
        Map<String, Boolean> hazelcastMap = hazelcastInstance.getMap("spotify-code-cache");
        List<String> inactiveCacheList = new ArrayList<>();
        for (String cache : hazelcastMap.keySet()) {
            if (!hazelcastMap.get(cache)) {
                inactiveCacheList.add(cache);
            }
        }
        return inactiveCacheList;
    }
}
