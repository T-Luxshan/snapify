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

    // Step 17


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
