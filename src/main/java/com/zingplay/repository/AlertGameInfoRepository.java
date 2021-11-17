package com.zingplay.repository;

import com.zingplay.models.AlertGameInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by thuydtm on 9:22 PM 7/10/2021
 */

public interface AlertGameInfoRepository extends MongoRepository<AlertGameInfo, String> {

    AlertGameInfo findFirstByGame(String game);

    List<AlertGameInfo> findByGame(String game);
}
