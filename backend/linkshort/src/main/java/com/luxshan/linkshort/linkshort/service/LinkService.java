package com.luxshan.linkshort.linkshort.service;

import com.luxshan.linkshort.linkshort.dto.CreateLinkRequest;
import com.luxshan.linkshort.linkshort.dto.LinkResponse;
import com.luxshan.linkshort.linkshort.exception.LinkNotFoundException;
import com.luxshan.linkshort.linkshort.exception.ShortCodeGenerationException;
import com.luxshan.linkshort.linkshort.model.Link;
import com.luxshan.linkshort.linkshort.repository.LinkRepository;
import com.luxshan.linkshort.linkshort.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LinkService {

    private final ShortCodeGenerator shortCodeGenerator;
    private final LinkRepository linkRepository;

    public LinkService(ShortCodeGenerator shortCodeGenerator, LinkRepository linkRepository) {
        this.shortCodeGenerator = shortCodeGenerator;
        this.linkRepository = linkRepository;
    }

    @Value("${app.base-url}")
    private String baseUrl;

    // Create a new link
    public LinkResponse createLink(CreateLinkRequest request){

        String shortCode = shortCodeGenerator.generate();

        int attempts = 1;
        while (linkRepository.existsByShortCode(shortCode)) {
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
        linkRepository.save(link);
        return toResponse(link);
    }

    // Get a link by short code
    public LinkResponse getLink(String shortCode){
        Optional<Link> linkOptional = linkRepository.findByShortCode(shortCode);
        if (linkOptional.isEmpty()) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }

        Link link = linkOptional.get();
        if(!link.isActive()) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        return toResponse(link);
    }

    // Delete a link by short code
    @Transactional
    public void deleteLink(String shortCode){
        if(!linkRepository.existsByShortCode(shortCode)) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        linkRepository.deleteByShortCode(shortCode);
    }

    // Redirect to original URL
    public String getOriginalUrl(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(()->
                        new LinkNotFoundException("Short link not found: " + shortCode));

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
