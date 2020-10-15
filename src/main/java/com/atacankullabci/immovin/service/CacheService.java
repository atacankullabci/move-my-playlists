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
    private Map<String, Boolean> codeMap;

    public CacheService(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.codeMap = hazelcastInstance.getMap("spotify-code-cache");
    }

    public void put(String code) {
        codeMap.put(code, false);
    }

    public void updateCode(String code) {
        codeMap.put(code, true);
    }

    public List<String> getAllInactivatedCaches() {
        List<String> inactiveCacheList = new ArrayList<>();
        for (String cache : codeMap.keySet()) {
            if (!codeMap.get(cache)) {
                inactiveCacheList.add(cache);
            }
        }
        return inactiveCacheList;
    }
}
