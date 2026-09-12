package com.luxshan.snapify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CachedLink {

    private String originalUrl;
    private LocalDateTime expiresAt;
    private boolean active;
}