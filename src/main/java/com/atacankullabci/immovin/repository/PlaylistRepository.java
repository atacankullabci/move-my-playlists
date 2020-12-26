package com.atacankullabci.immovin.repository;

import com.atacankullabci.immovin.common.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaylistRepository extends MongoRepository<Playlist, String> {
}
