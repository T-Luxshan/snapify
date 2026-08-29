package com.luxshan.linkshort.linkshort.controller;

import com.luxshan.linkshort.linkshort.dto.CreateLinkRequest;
import com.luxshan.linkshort.linkshort.dto.LinkResponse;
import com.luxshan.linkshort.linkshort.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
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

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
