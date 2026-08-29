package com.luxshan.linkshort.linkshort.service;

import com.luxshan.linkshort.linkshort.dto.CreateLinkRequest;
import com.luxshan.linkshort.linkshort.dto.LinkResponse;
import com.luxshan.linkshort.linkshort.exception.LinkNotFoundException;
import com.luxshan.linkshort.linkshort.exception.ShortCodeGenerationException;
import com.luxshan.linkshort.linkshort.model.Link;
import com.luxshan.linkshort.linkshort.util.ShortCodeGenerator;
import com.luxshan.linkshort.linkshort.util.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LinkService {

    private final ShortCodeGenerator shortCodeGenerator;

    public LinkService(ShortCodeGenerator shortCodeGenerator) {
        this.shortCodeGenerator = shortCodeGenerator;
    }

    private final Map<String, Link> links = new ConcurrentHashMap<>();
    @Value("${app.base-url}")
    private String baseUrl;

    // Create a new link
    public LinkResponse createLink(CreateLinkRequest request){

        String shortCode = shortCodeGenerator.generate();
        int attempts = 1;
        while (links.containsKey(shortCode)) {
            if(attempts >= 3) {
                throw new ShortCodeGenerationException("Failed to generate a unique short code");
            }
            shortCode = shortCodeGenerator.generate();
            attempts++;
        }

        Link link = Link.builder()
                .shortCode(shortCode)
                .originalUrl(request.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
        links.put(shortCode, link);
        return toResponse(link);
    }

    // Get a link by short code
    public LinkResponse getLink(String shortCode){
        Link link = links.get(shortCode);
        if (link == null) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        if(!link.isActive()) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        return toResponse(link);
    }

    // Delete a link by short code
    public void deleteLink(String shortCode){
        if(!links.containsKey(shortCode)) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        links.remove(shortCode);

    }

    // Redirect to original URL
    public String getOriginalUrl(String shortCode){
        Link link = links.get(shortCode);
        if (link == null) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        if(!link.isActive()) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        return link.getOriginalUrl();
    }

    
    // helper method to convert Link to LinkResponse
    private LinkResponse toResponse(Link link) {
        return LinkResponse.builder()
            .shortCode(link.getShortCode())
            .shortUrl(baseUrl + "/r/" + link.getShortCode())
            .originalUrl(link.getOriginalUrl())
            .createdAt(link.getCreatedAt())
            .build();
    }
}
