package com.zingplay.controllers;

import com.zingplay.module.tracking.TrackingService;
import com.zingplay.service.user.RunOfferService;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    TrackingService trackingService;

    @Autowired
    RunOfferService runOfferService;

    @GetMapping("/all/{game}/{country}")
    public String allAccess(@PathVariable String game, @PathVariable String country) {
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game, country);
        trackingService.checkNumLog(game, country);
        runOfferService.checkNumOfferRunning(game, country);
        return "connect";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
