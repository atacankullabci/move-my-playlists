package com.atacankullabci.immovin.repository;

import com.atacankullabci.immovin.common.InProgressMap;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InProgressMapRepository extends CrudRepository<InProgressMap, String> {
}
