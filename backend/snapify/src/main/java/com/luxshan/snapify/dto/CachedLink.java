package com.luxshan.snapify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CachedLink {

    private String originalUrl;
    private LocalDateTime expiresAt;
    private boolean active;
}