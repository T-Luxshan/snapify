package com.luxshan.linkshort.linkshort.controller;

import com.luxshan.linkshort.linkshort.dto.LinkResponse;
import com.luxshan.linkshort.linkshort.exception.LinkNotFoundException;
import com.luxshan.linkshort.linkshort.service.LinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/{shortCode}")
    public LinkResponse getLink(@PathVariable String shortCode){
        return linkService.getLink(shortCode);
    }

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
