package com.zingplay.repository;

import com.zingplay.models.CustomGift;
import com.zingplay.models.Object;
import com.zingplay.models.Offer;
import com.zingplay.models.RunOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface CustomGiftRepository extends MongoRepository<CustomGift, String> {
    Page<CustomGift> findByIdContaining(String search, Pageable pageable);
    Page<CustomGift> findById(String search, Pageable pageable);
    List<CustomGift> findByGiftName(String giftName);
    List<CustomGift> findByObjectIdAndTimeStartIsBeforeAndTimeEndIsAfter(String object, Date time1, Date time2);
    List<CustomGift> findByObjectIdAndTimeEndIsAfter(String object, Date time);
}
