package com.zingplay.service.user;

import com.zingplay.helpers.Helpers;
import com.zingplay.models.Log;
import com.zingplay.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final Helpers helpers;

    @Autowired
    public LogService(LogRepository logRepository, Helpers helpers) {
        this.logRepository = logRepository;
        this.helpers = helpers;
    }

    public void addLog(String msg){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        addLog(msg, game, country, username);
    }

    public void addLog(String msg, String game, String country, String username){
        Log log = new Log();
        log.setUsername(username);
        log.setGame(game);
        log.setCountry(country);
        log.setMessage(msg);
        log.setTimeCreate(new Date());
        logRepository.save(log);
    }

    public void addLog(int level, String msg){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        Log log = new Log();
        log.setLevel(level);
        log.setUsername(username);
        log.setGame(game);
        log.setCountry(country);
        log.setMessage(msg);
    }

    public Page<?> getAllLogs(String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(search!=null && !search.isEmpty()){
            return logRepository.findByUsernameContainingAndGameAndCountry(search, game, country, pageable);
        }
        return logRepository.findByAndGameAndCountry(game, country, pageable);
    }

    // public void deleteOffer(String id){
    //     logRepository.deleteById(id);
    // }
}
