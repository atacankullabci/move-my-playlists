package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.TrackIdMap;
import com.atacankullabci.immovin.repository.TrackIdMapRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CacheService {

    private final TrackIdMapRepository trackIdMapRepository;

    public CacheService(TrackIdMapRepository trackIdMapRepository) {
        this.trackIdMapRepository = trackIdMapRepository;
    }

    public void put(String iTunesId, String spotifyId) {
        this.trackIdMapRepository.save(new TrackIdMap(iTunesId, spotifyId));
    }

    public String get(String iTunesId) {
        Optional<TrackIdMap> trackIdMap = this.trackIdMapRepository.findById(iTunesId);
        if (trackIdMap.isPresent()) {
            return this.trackIdMapRepository.findById(iTunesId).get().getSpotifyTrackId();
        }
        return null;
    }
}
