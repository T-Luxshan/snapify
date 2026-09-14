package com.luxshan.snapify.service;

import com.luxshan.snapify.dto.CachedLink;
import com.luxshan.snapify.dto.CreateLinkRequest;
import com.luxshan.snapify.dto.LinkResponse;
import com.luxshan.snapify.exception.LinkExpiredException;
import com.luxshan.snapify.exception.LinkNotFoundException;
import com.luxshan.snapify.exception.ShortCodeGenerationException;
import com.luxshan.snapify.model.Link;
import com.luxshan.snapify.repository.LinkRepository;
import com.luxshan.snapify.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LinkService {

    private final ShortCodeGenerator shortCodeGenerator;
    private final LinkRepository linkRepository;
    private final RedisService redisService;

    public LinkService(ShortCodeGenerator shortCodeGenerator, LinkRepository linkRepository, RedisService redisService) {
        this.shortCodeGenerator = shortCodeGenerator;
        this.linkRepository = linkRepository;
        this.redisService = redisService;
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
                .expiresAt(request.getExpiresAt())
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

        String cacheKey = "link:" + shortCode;
        redisService.delete(cacheKey);
    }

    // Redirect to original URL
    public String getOriginalUrl(String shortCode){

        String cacheKey = "link:" + shortCode;
        CachedLink cachedLink = redisService.get(cacheKey);

        if (cachedLink != null){
            validateLink(
                    cachedLink.isActive(),
                    cachedLink.getExpiresAt(),
                    shortCode
            );
            return cachedLink.getOriginalUrl();
        }

        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(()->
                        new LinkNotFoundException("Short link not found: " + shortCode));

        validateLink(
                link.isActive(),
                link.getExpiresAt(),
                shortCode
        );

        CachedLink newCachedLink =  CachedLink.builder()
                .originalUrl(link.getOriginalUrl())
                .expiresAt(link.getExpiresAt())
                .active(link.isActive())
                .build();

        redisService.set(cacheKey, newCachedLink);

        return link.getOriginalUrl();
    }

    
    // helper method to convert Link to LinkResponse
    private LinkResponse toResponse(Link link) {
        return LinkResponse.builder()
            .shortCode(link.getShortCode())
            .shortUrl(baseUrl + "/r/" + link.getShortCode())
            .originalUrl(link.getOriginalUrl())
            .createdAt(link.getCreatedAt())
            .expiresAt(link.getExpiresAt())
            .build();
    }

    private void validateLink(boolean active, LocalDateTime expiresAt, String shortCode) {

        if (!active) {
            throw new LinkNotFoundException("Short link not found: " + shortCode);
        }
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new LinkExpiredException("Short link is expired: " + shortCode);
        }
    }
}
