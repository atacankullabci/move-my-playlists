package com.atacankullabci.immovin.repository;

import com.atacankullabci.immovin.common.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsernameAndExternalUrl(String username, String externalUrl);
}
