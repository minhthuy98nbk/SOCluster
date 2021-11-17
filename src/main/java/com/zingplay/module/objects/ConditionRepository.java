package com.zingplay.module.objects;

import com.zingplay.models.Condition;
import com.zingplay.models.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConditionRepository extends MongoRepository<Condition, String> {
    Page<Condition> findByNameContainingOrKeyContaining(String search, String keySearch, Pageable pageable);
    boolean existsConditionByKeyIn(List<String> keys);
    List<Condition> findByKeyIn(List<String> keys);
}
