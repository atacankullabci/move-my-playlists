package com.atacankullabci.immovin.repository;

import com.atacankullabci.immovin.common.MediaContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaContentRepository extends MongoRepository<MediaContent, String> {
}
