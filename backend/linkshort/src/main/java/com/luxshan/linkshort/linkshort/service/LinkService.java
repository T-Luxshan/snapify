package com.luxshan.linkshort.linkshort.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.luxshan.linkshort.linkshort.model.Link;
import com.luxshan.linkshort.linkshort.util.ShortCodeGenerator;
import com.luxshan.linkshort.linkshort.util.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import com.luxshan.linkshort.linkshort.dto.LinkResponse;
import com.luxshan.linkshort.linkshort.dto.CreateLinkRequest;
import com.luxshan.linkshort.linkshort.exception.InvalidUrlException;
import com.luxshan.linkshort.linkshort.exception.ShortCodeGenerationException;
import com.luxshan.linkshort.linkshort.exception.LinkNotFoundException;
import java.time.LocalDateTime;

@Service
public class LinkService {

    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlValidator urlValidator;

    public LinkService(ShortCodeGenerator shortCodeGenerator, UrlValidator urlValidator) {
        this.shortCodeGenerator = shortCodeGenerator;
        this.urlValidator = urlValidator;
    }

    private final Map<String, Link> links = new ConcurrentHashMap<>();
    @Value("${app.base-url}")
    private String baseUrl;

    // Create a new link
    public LinkResponse createLink(CreateLinkRequest request){
        if (!urlValidator.isValid(request.getOriginalUrl())) {
            throw new InvalidUrlException("Invalid URL: must start with http:// or https://");
        }
        String shortCode = shortCodeGenerator.generate();
        int attempts = 0;
        while (links.containsKey(shortCode) && attempts < 3) {
            shortCode = shortCodeGenerator.generate();
            attempts++;
        }
        if (attempts >= 3) {
            throw new ShortCodeGenerationException("Failed to generate a unique short code");
        }
        Link link = Link.builder()
            .shortCode(shortCode)
            .originalUrl(request.getOriginalUrl())
            .createdAt(LocalDateTime.now())
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
