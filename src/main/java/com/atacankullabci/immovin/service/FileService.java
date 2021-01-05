package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.mongodb.bulk.BulkWriteInsert;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final MongoTemplate mongoTemplate;

    public FileService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional
    public void bulkSaveMediaContent(List<?> objectList, Class clazz) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz);
        bulkOperations.insert(objectList);
        BulkWriteResult bulkWriteResult = bulkOperations.execute();
        List<BulkWriteInsert> list = bulkWriteResult.getInserts();
        for (BulkWriteInsert insert : list) {
            System.out.println(insert);
        }
    }

    public List<MediaContent> combineNewVersionMediaContentList(List<MediaContent> prevMediaContentList,
                                                                List<MediaContent> currMediaContentList) {
        Set<MediaContent> currSet = new HashSet<>(currMediaContentList);
        Set<MediaContent> prevSet = new HashSet<>(prevMediaContentList);

        currSet.addAll(prevSet.stream().filter(e -> currSet.stream()
                .noneMatch(p -> p.equals(e)))
                .collect(Collectors.toSet()));

        return new ArrayList<>(currSet);
    }
}
