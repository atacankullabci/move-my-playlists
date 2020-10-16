package com.atacankullabci.immovin.repository;

import com.atacankullabci.immovin.common.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
}
