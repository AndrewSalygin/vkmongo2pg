package com.andrewsalygin.repository.mongo;

import com.andrewsalygin.entity.mongo.Item;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class MongoItemRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoItemRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Item> getAllItemsByCollectionName(String collectionName) {
        List<Document> docs = mongoTemplate.findAll(Document.class, collectionName);

        return docs.stream().map(Item::new).toList();
    }

    public Set<String> getAllCollectionNames() {
        return mongoTemplate.getCollectionNames();
    }
}
