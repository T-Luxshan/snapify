package com.luxshan.snapify.controller;

import com.luxshan.snapify.dto.CachedLink;
import com.luxshan.snapify.dto.CreateLinkRequest;
import com.luxshan.snapify.dto.LinkResponse;
import com.luxshan.snapify.service.LinkService;
import com.luxshan.snapify.service.RedisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;
    private final RedisService redisService;

    public LinkController(LinkService linkService, RedisService redisService) {
        this.linkService = linkService;
        this.redisService = redisService;
    }

    // Get link with shortCode
    @GetMapping("/{shortCode}")
    public LinkResponse getLink(@PathVariable String shortCode){
        return linkService.getLink(shortCode);
    }

    // Create shortLink
    @PostMapping
    public LinkResponse createLink(@Valid @RequestBody CreateLinkRequest linkRequest){
        return linkService.createLink(linkRequest);
    }

    @DeleteMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(@PathVariable String shortCode){
        linkService.deleteLink(shortCode);
    }

    // redirect
    @GetMapping("/r/{shortCode}")
    @ResponseStatus
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        String originalUrl = linkService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

//    temp endpoint to test redis
    @GetMapping("/redis-test")
    public CachedLink redisTest(){
        CachedLink cachedLink = CachedLink.builder()
                .originalUrl("https://example.com")
                .expiresAt(LocalDateTime.now())
                .active(true)
                .build();

        redisService.set("test:link", cachedLink);

        return redisService.get("test:link");

    }

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
