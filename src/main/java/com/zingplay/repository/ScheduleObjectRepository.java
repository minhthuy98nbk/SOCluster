package com.zingplay.repository;

import com.zingplay.models.Object;
import com.zingplay.models.ScheduleObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleObjectRepository extends MongoRepository<ScheduleObject, String> {
    Optional<ScheduleObject> findFirstByObject(Object object);
    Page<ScheduleObject> findAll(Pageable pageable);
    Page<ScheduleObject> findByGameAndCountry(String game, String country, Pageable pageable);
    Page<ScheduleObject> findByNameContainingAndGameAndCountry(String search, String game, String country, Pageable pageable);
    Page<ScheduleObject> findByObjectIsInAndGameAndCountry(List<Object> objects, String game, String country, Pageable pageable);
    List<ScheduleObject> findAllByTimeScanLessThanEqualAndStatusIn(Date curTime, List<Integer> status);
    List<ScheduleObject> findAllByStatusIn(List<Integer> status);
    Optional<ScheduleObject> findFirstByTimeScanLessThanEqualAndStatus(Date curTime, int status);

    Page<ScheduleObject> findByGameAndCountryAndObject_Id(String game, String country,String objectId, Pageable pageable);
    Page<ScheduleObject> findByNameContainingAndGameAndCountryAndObject_Id(String search, String game, String country,String objectId, Pageable pageable);
    void deleteAllByObject(Object object);
    void deleteAllByGameAndCountry(String game, String country);

}
