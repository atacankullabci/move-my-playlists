package com.atacankullabci.immovin.repository;

import com.atacankullabci.immovin.common.StateMap;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateMapRepository extends CrudRepository<StateMap, String> {
}
