package com.zingplay.module.objects;

import com.zingplay.models.Condition;
import com.zingplay.models.ConditionObject;
import com.zingplay.models.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConditionObjectRepository extends MongoRepository<ConditionObject, String> {
    Page<ConditionObject> findById(String keySearch, Pageable pageable);
    boolean existsConditionById(String id);
}
